package checkers.core.tables

import com.softwaremill.macwire._

class TablesModule {
  lazy val neighborTable = wire[NeighborTable]

  lazy val jumpTable = wire[JumpTable]
}

