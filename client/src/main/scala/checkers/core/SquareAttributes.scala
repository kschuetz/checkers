package checkers.core

case class SquareAttributes(clickable: Boolean,
                            highlighted: Boolean,
                            ghost: Boolean)

case class SquareAttributesVector(items: Vector[SquareAttributes]) {
  def withClickable(ids: Set[Int]): SquareAttributesVector = {
    modify { case (attr, idx) => attr.copy(clickable = ids.contains(idx)) }
  }

  def withGhost(ids: Set[Int]): SquareAttributesVector = {
    modify { case (attr, idx) => attr.copy(ghost = ids.contains(idx)) }
  }

  def modify(f: ((SquareAttributes, Int)) => SquareAttributes): SquareAttributesVector = {
    val newItems = items.zipWithIndex.map(f)
    SquareAttributesVector(newItems)
  }
}

object SquareAttributes {
  val default = SquareAttributes(clickable = false,
    highlighted = false,
    ghost = false)
}

object SquareAttributesVector {
  val default = SquareAttributesVector(Vector.fill(32)(SquareAttributes.default))
}