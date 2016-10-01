package checkers.benchmarks

import checkers.benchmarks.suites.{Evaluator, MoveGenerator, Sandbox}
import japgolly.scalajs.benchmark.gui.BenchmarkGUI
import org.scalajs.dom.document

import scala.scalajs.js.annotation.JSExport

@JSExport("BenchmarksMain")
object Main extends scalajs.js.JSApp {

  def main(): Unit = {
    val tgt = document.getElementById("body")

    BenchmarkGUI.renderMenu(tgt)(Evaluator.suite,
      MoveGenerator.suite,
      Sandbox.suite)
  }
}
