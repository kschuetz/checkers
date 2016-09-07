package checkers.core

import checkers.computer.ProgramRegistry
import checkers.core.tables.TablesModule
import checkers.persistence.NewGameSettingsPersister
import org.scalajs.dom

class MainLoop(programRegistry: ProgramRegistry,
               tablesModule: TablesModule,
               animationSettings: AnimationSettings,
               newGameSettingsPersister: NewGameSettingsPersister,
               makeGameLogicModule: GameLogicModuleFactory) {

  def run(gameHost: dom.Node, dialogHost: dom.Node): Unit = {




  }

}