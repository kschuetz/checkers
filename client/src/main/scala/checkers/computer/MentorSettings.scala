package checkers.computer

trait MentorSettings {
  def Phase1MaxKCycles: Int
  def Phase2MaxKCycles: Int
  def MainMaxKCycles: Int
  def LateMaxKCycles: Int
}

class DefaultMentorSettings extends MentorSettings {
  def Phase1MaxKCycles: Int = 100

  def Phase2MaxKCycles: Int = 100

  def MainMaxKCycles: Int = 250

  override def LateMaxKCycles: Int = 500
}