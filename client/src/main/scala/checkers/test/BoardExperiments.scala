package checkers.test

import checkers.core.BoardState

object BoardExperiments {

  lazy val board1: BoardState = BoardUtils.parseBoard("""
              L - - -
             - - - D
              - l - -
             - - - -
              - l l -
             d d d d
              l - - -
             - - - -
    """)

  lazy val board2: BoardState = BoardUtils.parseBoard("""
              - - - -
             d l - -
              - - - -
             - d - -
              - d - -
             - - - -
              - d - -
             - - - -
                                          """)

  lazy val board3: BoardState = BoardUtils.parseBoard("""
              - - - -
             - - - -
              - - l -
             - - - -
              - d - -
             - - - -
              - - - -
             - - - -
                                          """)

}