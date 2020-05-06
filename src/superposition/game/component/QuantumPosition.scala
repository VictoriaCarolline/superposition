package superposition.game.component

import com.badlogic.ashley.core.{Component, ComponentMapper}
import superposition.math.{Vector2d, Vector2i}
import superposition.quantum.{MetaId, StateId}

final class QuantumPosition(val absolute: MetaId[Vector2d], val cell: StateId[Vector2i], var relative: Vector2d)
  extends Component

object QuantumPosition {
  val Mapper: ComponentMapper[QuantumPosition] = ComponentMapper.getFor(classOf[QuantumPosition])
}
