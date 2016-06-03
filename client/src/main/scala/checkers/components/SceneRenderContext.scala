package checkers.components

import checkers.components.SceneRenderContext.MouseTransform
import checkers.geometry.Point
import japgolly.scalajs.react.TopNode
import org.scalajs.dom.raw.{SVGElement, SVGSVGElement}

object SceneRenderContext {
  type MouseTransform = Point => Point


  val default = SceneRenderContext(identity)


  def fromSVGElement(element: SVGSVGElement): SceneRenderContext = {
    def transform(point: Point): Point = {
      val target = element
      val matrix = target.getScreenCTM().inverse()
      val pt = target.createSVGPoint()
      pt.x = point.x
      pt.y = point.y
      val transformed = pt.matrixTransform(matrix)
      Point(transformed.x ,transformed.y)
    }

    SceneRenderContext(transform)
  }
}


case class SceneRenderContext(cursorToLocal: MouseTransform)



