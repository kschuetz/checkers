package checkers

import checkers.benchmark.MoveGeneratorBenchmarks
import checkers.core.{SimpleMoveIndex, masks}
import checkers.driver.GameScreenDriver
import checkers.logger._
import checkers.models.{GameScreenModel, GameSettings}
import checkers.style.GlobalStyles
import checkers.util.DebugUtils
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._

@JSExport("CheckersMain")
object CheckersMain extends js.JSApp {

  @JSExport
  def main(): Unit = {
    log.warn("Application starting")

    MoveGeneratorBenchmarks.test1()
    println("masks:")
    println(masks.squares)

    // create stylesheet
    GlobalStyles.addToDocument()

    val host = dom.document.getElementById("root")
    sandbox1(host)
  }

  private def sandbox1(host: dom.Node): Unit = {
    val model = GameScreenModel.initial(GameSettings.default)
    println(model.gameState.board.data)
    DebugUtils.printOccupants(model.gameState.board)

    val driver = new GameScreenDriver(host, model)
    driver.run()
  }
}
