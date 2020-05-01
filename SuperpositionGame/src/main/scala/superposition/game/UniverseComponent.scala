package superposition.game

import com.badlogic.ashley.core.Component
import superposition.math.Vector2i
import superposition.quantum.{StateId, Universe}

import scala.Function.const

private final class UniverseComponent(
    val position: Option[StateId[Vector2i]] = None,
    val primaryBit: Option[StateId[Boolean]] = None,
    val blockingCells: Universe => Set[Vector2i] = const(Set.empty))
    extends Component

//private object UniverseComponent {
//  val All: Iterable[UniverseComponent] = track(classOf[UniverseComponent]).asScala
//}
