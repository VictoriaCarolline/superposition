package superposition.game

import com.badlogic.ashley.core._
import com.badlogic.gdx.graphics.OrthographicCamera
import superposition.game.MultiverseComponent.UniverseMapper
import superposition.math.Vector2i
import superposition.quantum.{MetaId, StateId, Universe}

private final class MultiverseComponent(val camera: OrthographicCamera) extends Component {
  private var _universes: Seq[Universe] = Seq(Universe())

  private var entities: List[Entity] = List()

  private var stateIds: List[StateId[_]] = List()

  def universes: Seq[Universe] = _universes

  def allocate[A](initialValue: A): StateId[A] = {
    val id = new StateId[A]
    _universes = _universes map (_.updatedState(id)(initialValue))
    stateIds ::= id
    id
  }

  def allocateMeta[A](initialValue: A): MetaId[A] = {
    val id = new MetaId[A]
    _universes = _universes map (_.updatedMeta(id)(initialValue))
    id
  }

  def addEntity(entity: Entity): Unit = entities ::= entity

  def allInCell(universe: Universe, cell: Vector2i): Iterable[UniverseComponent] =
    entities map UniverseMapper.get filter (_.position map (universe.state(_)) contains cell)

  def primaryBits(universe: Universe, cell: Vector2i): Iterable[StateId[Boolean]] =
    allInCell(universe, cell) flatMap (_.primaryBit.toList)

  def isBlocked(universe: Universe, cell: Vector2i): Boolean =
    entities map UniverseMapper.get exists (_.blockingCells(universe) contains cell)

  def allOn(universe: Universe, controls: Iterable[Vector2i]): Boolean =
    controls forall { control =>
      entities filter (_.isInstanceOf[Quball]) exists { quball =>
        universe.state(quball.asInstanceOf[Quball].cell) == control && universe.state(quball.asInstanceOf[Quball].onOff)
      }
    }

  def isValid(universe: Universe): Boolean =
    entities map UniverseMapper.get forall { universeComponent =>
      universeComponent.position.isEmpty || !isBlocked(universe, universe.state(universeComponent.position.get)) }
}

private object MultiverseComponent {
  private val UniverseMapper: ComponentMapper[UniverseComponent] = ComponentMapper.getFor(classOf[UniverseComponent])
}
