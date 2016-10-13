import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

/**
 * Application settings. Configure the build for your application here.
 * You normally don't have to touch the actual build definition after this.
 */
object Settings {
  /** The name of your application */
  val name = "checkers-scalajs"

  /** The version of your application */
  val version = "0.1.0"

  /** Options for the scala compiler */
  val scalacOptions = Seq(
    "-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature"
  )

  /** Declare global dependency versions here to avoid mismatches in multi part dependencies */
  object versions {
    val scala = "2.11.8"
    val scalaDom = "0.9.1"
    val scalajsReact = "0.11.2"
    val scalaCSS = "0.4.1"
    val log4js = "1.4.13"
    val uTest = "0.4.3"
    val nyaya = "0.7.2"
    val macwire = "2.2.4"
    val uPickle = "0.4.1"
    val benchmark = "0.2.3"

    val react = "15.3.2"

    val playScripts = "0.5.0"
  }

  /**
   * These dependencies are shared between JS and JVM projects
   * the special %%% function selects the correct version for each project
   */
  val sharedDependencies = Def.setting(Seq(
  ))

  /** Dependencies only used by the JVM project */
  val jvmDependencies = Def.setting(Seq(
    "com.vmunier" %% "play-scalajs-scripts" % versions.playScripts,
    "com.lihaoyi" %% "utest" % versions.uTest % Test
  ))

  /** Dependencies only used by the JS project (note the use of %%% instead of %%) */
  val scalajsDependencies = Def.setting(Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % versions.scalajsReact,
    "com.github.japgolly.scalajs-react" %%% "extra" % versions.scalajsReact,
    "com.github.japgolly.scalacss" %%% "ext-react" % versions.scalaCSS,
    "org.scala-js" %%% "scalajs-dom" % versions.scalaDom,
    "com.softwaremill.macwire" %% "macros" % versions.macwire % Provided,
    "com.lihaoyi" %%% "upickle" % versions.uPickle,
    "com.lihaoyi" %%% "utest" % versions.uTest % Test,
    "com.github.japgolly.nyaya" %%% "nyaya-test" % versions.nyaya % Test
  ))

  val benchmarkjsDependencies = Def.setting(Seq(
    "com.github.japgolly.scalajs-benchmark" %%% "benchmark" % versions.benchmark
  ))

  /** Dependencies for external JS libs that are bundled into a single .js file according to dependency order */
  val jsDependencies = Def.setting(Seq(
    "org.webjars.bower" % "react" % versions.react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
    "org.webjars.bower" % "react" % versions.react / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
    "org.webjars" % "log4javascript" % versions.log4js / "js/log4javascript_uncompressed.js" minified "js/log4javascript.js"
  ))

  val macrosDependencies = Def.setting(Seq(
    "org.scala-lang" % "scala-reflect" % versions.scala
  ))
}
