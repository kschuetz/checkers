import org.scalajs.linker.interface.ModuleSplitStyle

val publicDev = taskKey[String]("output directory for `npm run dev`")
val publicProd = taskKey[String]("output directory for `npm run build`")

val benchmarksProd = taskKey[String]("output directory for benchmarks")

// use eliding to drop some debug code in the production build
lazy val elideOptions = settingKey[Seq[String]]("Set limit for elidable functions")

lazy val `checkers` = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    elideOptions := {
      // build-mode is passed in from Vite using -Dbuild-mode option. See vite.config.js.
      // This is a hack. Do it differently if there is ever a better way in SBT.
      Option(System.getProperty("build-mode")) match {
        case Some("prod") => elideBelowWarning
        case _ => Seq()
      }
    },
    scalacOptions ++= (Settings.scalacOptions ++ elideOptions.value),

    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("checkers")))
    },
    libraryDependencies ++= Seq(
      "com.github.japgolly.scalajs-react" %%% "core" % Settings.versions.scalajsReact,
      "com.github.japgolly.scalajs-react" %%% "extra" % Settings.versions.scalajsReact,
      "com.github.japgolly.scalacss" %%% "ext-react" % Settings.versions.scalaCSS,
      "com.softwaremill.macwire" %% "macros" % Settings.versions.macwire % Provided,
      "com.lihaoyi" %%% "upickle" % Settings.versions.uPickle,
      "com.lihaoyi" %%% "utest" % Settings.versions.uTest % Test,
      "com.github.japgolly.nyaya" %%% "nyaya-test" % Settings.versions.nyaya % Test
    ),

    // use uTest framework for tests
    testFrameworks += new TestFramework("utest.runner.Framework"),

    publicDev := linkerOutputDirectory((Compile / fastLinkJS).value).getAbsolutePath(),
    publicProd := linkerOutputDirectory((Compile / fullLinkJS).value).getAbsolutePath(),
  )
  .dependsOn(macros)

lazy val macros: Project = (project in file("macros"))
  .settings(
    scalaVersion := Settings.versions.scala,
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % Settings.versions.scala
    )
  )

lazy val benchmarks: Project = (project in file("benchmarks"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "benchmarks",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    elideOptions := elideBelowWarning,
    scalacOptions ++= elideOptions.value,
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("bench")))
    },
    libraryDependencies ++= Seq(
      "com.softwaremill.macwire" %% "macros" % Settings.versions.macwire % Provided,
      "com.github.japgolly.scalajs-benchmark" %%% "benchmark" % Settings.versions.benchmark,
    ),

    benchmarksProd := linkerOutputDirectory((Compile / fullLinkJS).value).getAbsolutePath(),
  )
  .dependsOn(checkers)

def linkerOutputDirectory(v: Attributed[org.scalajs.linker.interface.Report]): File = {
  v.get(scalaJSLinkerOutputDirectory.key).getOrElse {
    throw new MessageOnlyException(
      "Linking report was not attributed with output directory. " +
        "Please report this as a Scala.js bug.")
  }
}

lazy val elideBelowWarning = Seq("-Xelide-below", "WARNING")
