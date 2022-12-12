
object Settings {
  val version = "0.2.0"

  val scalacOptions: Seq[String] = Seq(
    "-Xlint",
    "-unchecked",
    "-encoding",
    "utf-8",
    "-deprecation",
    "-feature"
  )

  object versions {
    val scala = "2.12.17"
    val scalajsReact = "1.7.7"
    val scalaCSS = "0.7.0"
    val uTest = "0.8.1"
    val nyaya = "0.10.0"
    val macwire = "2.5.8"
    val uPickle = "2.0.0"
    val benchmark = "0.9.0"
  }

}
