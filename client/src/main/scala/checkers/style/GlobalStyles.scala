package checkers.style

import scalacss.Defaults._

object GlobalStyles extends StyleSheet.Inline {
  import dsl._

  style(unsafeRoot("body")(
    paddingTop(5.px))
  )

  val bootstrapStyles = new BootstrapStyles
}