package checkers.benchmarks

import checkers.benchmarks.suites.{Evaluator, MoveGenerator, Searcher}
import japgolly.scalajs.benchmark.gui.BenchmarkGUI
import org.scalajs.dom.document

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("BenchmarksMain")
object Main {

  @JSExport
  def main(args: Array[String]): Unit = {
    val tgt = document.getElementById("body")

    BenchmarkGUI.renderMenu(tgt)(Evaluator.suite,
      MoveGenerator.suite, Searcher.suite)
  }
}
