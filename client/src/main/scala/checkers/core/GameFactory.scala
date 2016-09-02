package checkers.core

import checkers.computer.TrivialPlayer
import org.scalajs.dom

class GameFactory(gameLogicModuleFactory: GameLogicModuleFactory,
                  screenLayoutSettings: ScreenLayoutSettings) {

  def create[DS, LS](gameConfig: GameConfig[DS, LS], host: dom.Node): Game[DS, LS] = {
    val gameLogicModule = gameLogicModuleFactory.apply(gameConfig.rulesSettings)
    createGame(gameLogicModule, gameConfig, host)
  }

  // Human vs. TrivialPlayer
  def createSimple1(host: dom.Node) = {
    val rulesSettings = RulesSettings.default
    val gameLogicModule = gameLogicModuleFactory.apply(rulesSettings)
    val light = Computer(new TrivialPlayer(gameLogicModule.moveGenerator)(None))
    val dark = Human
    val gameConfig = GameConfig(rulesSettings, PlayerConfig(dark, light))
    createGame(gameLogicModule, gameConfig, host)
  }

  // Human vs. Human
  def createSimple2(host: dom.Node) = {
    val rulesSettings = RulesSettings.default
    val gameLogicModule = gameLogicModuleFactory.apply(rulesSettings)
    val light = Human
    val dark = Human
    val gameConfig = GameConfig(rulesSettings, PlayerConfig(dark, light))
    createGame(gameLogicModule, gameConfig, host)
  }

  // Trivial Player vs. Trivial Player
  def createSimple3(host: dom.Node) = {
    val rulesSettings = RulesSettings.default
    val gameLogicModule = gameLogicModuleFactory.apply(rulesSettings)
    val light = Computer(new TrivialPlayer(gameLogicModule.moveGenerator)(None))
    val dark =  Computer(new TrivialPlayer(gameLogicModule.moveGenerator)(None))
    val gameConfig = GameConfig(rulesSettings, PlayerConfig(dark, light))
    createGame(gameLogicModule, gameConfig, host)
  }

  private def createGame[DS, LS](gameLogicModule: GameLogicModule, gameConfig: GameConfig[DS, LS], host: dom.Node): Game[DS, LS] = {
    val driver = new GameDriver(gameLogicModule)(gameConfig.playerConfig)
    new Game(driver, screenLayoutSettings)(host)
  }

}