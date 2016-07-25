package checkers.driver

import checkers.computer.TrivialPlayer
import checkers.core._
import org.scalajs.dom

class GameFactory(gameLogicModuleFactory: GameLogicModuleFactory) {

  def create[DS, LS](gameConfig: GameConfig[DS, LS], host: dom.Node): Game[DS, LS] = {
    val gameLogicModule = gameLogicModuleFactory.apply(gameConfig.rulesSettings)
    createGame(gameLogicModule, gameConfig, host)
  }

  def createSimple1(host: dom.Node) = {
    val rulesSettings = RulesSettings.default
    val gameLogicModule = gameLogicModuleFactory.apply(rulesSettings)
    val light = Computer(new TrivialPlayer(gameLogicModule.moveGenerator)(None))
    val dark = Human
    val gameConfig = GameConfig(rulesSettings, PlayerConfig(dark, light))
    createGame(gameLogicModule, gameConfig, host)
  }

  private def createGame[DS, LS](gameLogicModule: GameLogicModule, gameConfig: GameConfig[DS, LS], host: dom.Node): Game[DS, LS] = {
    val driver = new GameDriver(gameLogicModule)(gameConfig.playerConfig)
    val model = driver.createInitialModel
    new Game(gameLogicModule, driver)(host, model)
  }

}