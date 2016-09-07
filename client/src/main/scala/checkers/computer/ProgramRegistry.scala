package checkers.computer

case class ProgramRegistryEntry[S](name: String,
                                   uniqueId: String,
                                   difficultyLevel: Int,
                                   factory: ProgramFactory[S])


class ProgramRegistry  {
  private var _entries: Vector[ProgramRegistryEntry[_]] = Vector.empty

  def entries: Vector[ProgramRegistryEntry[_]] = _entries

  def register(entry: ProgramRegistryEntry[_]): Unit =
    _entries = _entries :+ entry

  def findEntry(id: String): Option[ProgramRegistryEntry[_]] =
    _entries.find(_.uniqueId == id)

}