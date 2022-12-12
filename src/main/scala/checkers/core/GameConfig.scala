package checkers.core

import checkers.computer.Program
import checkers.consts._

case class PlayerConfig(darkPlayer: Player, lightPlayer: Player) {
  def getPlayer(side: Side): Player = if(side == DARK) darkPlayer else lightPlayer
}

case class MentorConfig(darkMentor: Option[Program], lightMentor: Option[Program]) {
  def hasMentor(side: Side): Boolean = if(side == DARK) darkMentor.isDefined else lightMentor.isDefined

  def getMentor(side: Side): Option[Program] = if(side == DARK) darkMentor else lightMentor
}

case class GameConfig(rulesSettings: RulesSettings, playerConfig: PlayerConfig, mentorConfig: MentorConfig)

object GameConfig {
  val test1 = GameConfig(RulesSettings.default, PlayerConfig(Human, Human), MentorConfig.empty)
}

object MentorConfig {
  val empty = MentorConfig(None, None)
}