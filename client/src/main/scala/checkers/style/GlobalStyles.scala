package checkers.style

import scalacss.ProdDefaults._

object GlobalStyles extends StyleSheet.Inline {
  import dsl._

  style(unsafeRoot("body")(
    paddingTop(5.px))
  )

}