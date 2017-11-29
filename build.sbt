import sbt.Keys._
import sbt.Project.projectToRef

// a special crossProject for configuring a JS/JVM/shared structure
lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(
    scalaVersion := Settings.versions.scala,
    libraryDependencies ++= Settings.sharedDependencies.value
  )
  // set up settings specific to the JS project
//  .jsConfigure(_ enablePlugins ScalaJSPlay)
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJVM = shared.jvm.settings(name := "sharedJVM")

lazy val sharedJS = shared.js.settings(name := "sharedJS")

// use eliding to drop some debug code in the production build
lazy val elideOptions = settingKey[Seq[String]]("Set limit for elidable functions")

lazy val macros: Project = (project in file("macros"))
  .settings(
    scalaVersion := Settings.versions.scala,
    libraryDependencies ++= Settings.macrosDependencies.value
  )

// instantiate the JS project for SBT with some additional settings
lazy val client: Project = (project in file("client"))
  .settings(
    name := "client",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Settings.scalajsDependencies.value,
    // by default we do development build, no eliding
    elideOptions := Seq(),
    scalacOptions ++= elideOptions.value,
    jsDependencies ++= Settings.jsDependencies.value,
    // RuntimeDOM is needed for tests
    jsDependencies += RuntimeDOM % "test",
    // yes, we want to package JS dependencies
    skip in packageJSDependencies := false,
    scalaJSUseMainModuleInitializer := true,

    // use uTest framework for tests
    testFrameworks += new TestFramework("utest.runner.Framework"),
    
    jsEnv := PhantomJSEnv().value
  )
//  .enablePlugins(ScalaJSPlugin, ScalaJSPlay)
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .dependsOn(sharedJS)
  .dependsOn(macros)

lazy val benchmarks: Project = (project in file("benchmarks"))
  .settings(
    name := "benchmarks",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Settings.scalajsDependencies.value ++ Settings.benchmarkjsDependencies.value,
    // by default we do development build, no eliding
    elideOptions := Seq(),
    scalacOptions ++= elideOptions.value,
    jsDependencies ++= Settings.jsDependencies.value,
    // RuntimeDOM is needed for tests
    jsDependencies += RuntimeDOM % "test",
    // yes, we want to package JS dependencies
    skip in packageJSDependencies := false,
    // use Scala.js provided launcher code to start the client app
    scalaJSUseMainModuleInitializer := true,
    // use uTest framework for tests
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .dependsOn(client)
  .dependsOn(sharedJS)
  .dependsOn(macros)


// Client projects (just one in this case)
lazy val clients = Seq(client, benchmarks)

// instantiate the JVM project for SBT with some additional settings
lazy val server = (project in file("server"))

  .settings(
    name := "server",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Settings.jvmDependencies.value,
    // connect to the client project
    scalaJSProjects := clients,
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(scalaJSProd),
    // compress CSS
    LessKeys.compress in Assets := true
  )
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin) // use the standard directory layout instead of Play's custom
  .aggregate(clients.map(projectToRef): _*)
  .dependsOn(sharedJVM)


lazy val deploy = TaskKey[Unit]("deploy", "Copy files into dist directory")
lazy val cleanDist = TaskKey[Unit]("cleanDist", "Clean the dist directory")

lazy val root = (project in file(".")).settings(
  deploy := {
    val clientTarget = (crossTarget in client).value
    val clientProjectName = (name in client).value

    val mainJsSource = clientTarget / (clientProjectName + "-opt.js")
    val depsJsSource = clientTarget / (clientProjectName + "-jsdeps.min.js")
    val launcherJsSource = clientTarget / (clientProjectName + "-launcher.js")
    val mainCssSource = file("server/target/web/public/main/stylesheets/main.min.css")
    val indexHtmlSource = file("server/target/web/public/main/index.html")

    IO.copyFile(mainCssSource, file("dist/stylesheets/main.min.css"))
    IO.copyFile(indexHtmlSource, file("dist/index.html"))
    IO.copyFile(mainJsSource, file("dist/scripts/main.js"))
    IO.copyFile(depsJsSource, file("dist/scripts/deps.js"))
  },

  cleanDist := {
    IO.delete(file("dist"))
  },

  commands ++= Seq(DevServerCmd, ReleaseCmd)

)

lazy val DevServerCmd = Command.args("devServer", "<port>") { case (st, args) =>
  val cmd = ("server/run" +: args).mkString(" ")
  cmd :: st
}

// Command for building a release
lazy val ReleaseCmd = Command.command("release") {
  state => "set elideOptions in client := Seq(\"-Xelide-below\", \"WARNING\")" ::
    "client/clean" ::
    "server/clean" ::
    "client/fullOptJS" ::
    "server/assets" ::
    "cleanDist" ::
    "deploy" ::
    "set elideOptions in client := Seq()" ::
    state
}



