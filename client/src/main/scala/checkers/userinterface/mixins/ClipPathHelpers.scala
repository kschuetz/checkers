package checkers.userinterface.mixins

import japgolly.scalajs.react.vdom.VdomAttr

trait ClipPathHelpers {
  // for some reason, React insists on "clipPath" rather than "clip-path"
  protected val clipPathAttr = VdomAttr.Generic("clipPath")
}