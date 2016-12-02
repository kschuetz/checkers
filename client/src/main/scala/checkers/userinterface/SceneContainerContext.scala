package checkers.userinterface

import checkers.util.Point
import org.scalajs.dom.raw.{SVGLocatable, SVGSVGElement}

trait SceneContainerContext {
  def screenToLocal(target: SVGLocatable): Point => Point
}


class MountedSceneContainerContext(val container: SVGSVGElement) extends SceneContainerContext {
  private val pt = container.createSVGPoint()

  def screenToLocal(target: SVGLocatable): Point => Point = { screen: Point =>
    val matrix = target.getScreenCTM().inverse()
    pt.x = screen.x
    pt.y = screen.y
    val transformed = pt.matrixTransform(matrix)
    Point(transformed.x ,transformed.y)
  }

}


object NullSceneContainerContext extends SceneContainerContext {
  override def screenToLocal(target: SVGLocatable): (Point) => Point = identity
}
