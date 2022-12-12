package checkers.test

import checkers.core.{Board, BoardState}

object BoardExperiments {

  lazy val board1: BoardState = Board.parseBoard("""
              L - - -
             - - - D
              - l - -
             - - - -
              - l l -
             d d d d
              l - - -
             - - - -
    """)

  lazy val board2: BoardState = Board.parseBoard("""
              - - - -
             d l - -
              - - - -
             - d - -
              - d - -
             - - - -
              - d - -
             - - - -
                                          """)

  lazy val board3: BoardState = Board.parseBoard("""
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