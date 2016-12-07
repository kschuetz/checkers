package checkers.userinterface.mixins

import japgolly.scalajs.react.vdom.ReactAttr

trait ClipPathHelpers {
  // for some reason, React insists on "clipPath" rather than "clip-path"
  protected val clipPathAttr = ReactAttr.Generic("clipPath")
}