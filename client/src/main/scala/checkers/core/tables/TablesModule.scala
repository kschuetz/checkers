package checkers.core.tables

import com.softwaremill.macwire._

class TablesModule {
  lazy val neighborTable: NeighborTable = wire[NeighborTable]

  lazy val jumpTable: JumpTable = wire[JumpTable]
}

