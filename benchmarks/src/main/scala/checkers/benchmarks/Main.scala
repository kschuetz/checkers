package checkers.benchmarks

import checkers.CheckersMain.bootstrap
import checkers.benchmarks.suites.{Evaluator, MoveGenerator, Searcher}
import checkers.logger.log
import japgolly.scalajs.benchmark.gui.BenchmarkGUI
import org.scalajs.dom
import org.scalajs.dom.{Event, document}

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("BenchmarksMain")
object Main {

  @JSExport
  def main(args: Array[String]): Unit = {
    dom.document.addEventListener[Event]("DOMContentLoaded", (event: Event) => {
      val tgt = document.getElementById("root")

      BenchmarkGUI.renderMenu(tgt)(Evaluator.suite,
        MoveGenerator.suite, Searcher.suite)
    })
  }
}
