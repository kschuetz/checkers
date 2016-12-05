package checkers.computer

trait MentorConfig {
  def Phase1MaxKCycles: Int
  def Phase2MaxKCycles: Int
  def MainMaxKCycles: Int
  def LateMaxKCycles: Int
}

class DefaultMentorConfig extends MentorConfig {
  def Phase1MaxKCycles: Int = 100

  def Phase2MaxKCycles: Int = 100

  def MainMaxKCycles: Int = 250

  override def LateMaxKCycles: Int = 500
}