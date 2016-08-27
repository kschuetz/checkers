package checkers.test

object BoardExperiments {

  lazy val board1 = BoardUtils.parseBoard("""
              L - - -
             - - - D
              - l - -
             - - - -
              - l l -
             d d d d
              l - - -
             - - - -
    """)

  lazy val board2 = BoardUtils.parseBoard("""
              - - - -
             d l - -
              - - - -
             - d - -
              - d - -
             - - - -
              - d - -
             - - - -
                                          """)

}