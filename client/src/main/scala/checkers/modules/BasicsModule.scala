package checkers.modules

import checkers.core.Notation
import checkers.core.tables.TablesModule
import com.softwaremill.macwire.wire

trait BasicsModule {
  lazy val tablesModule: TablesModule = wire[TablesModule]

  import tablesModule._

  lazy val notation: Notation = wire[Notation]
}