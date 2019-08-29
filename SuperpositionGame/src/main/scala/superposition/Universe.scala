package superposition

import engine.core.Behavior.Entity
import engine.util.math.Vec2d

import scala.collection.immutable.{HashMap, HashSet}
import scala.math.pow

/**
 * A game universe.
 *
 * Universes contain objects in a particular (definite) state that can interact with each other, but not with objects
 * from other universes. It corresponds to a basis vector with a particular amplitude (coefficient) in a quantum state.
 */
private class Universe extends Entity {
  /**
   * The probability amplitude of this universe.
   */
  var amplitude: Complex = Complex(1)

  private var _objects: Set[UniverseObject] = new HashSet[UniverseObject]()

  private var _qubits: Map[Int, Qubit] = new HashMap[Int, Qubit]()

  override protected def onDestroy(): Unit = objects.foreach(_.entity.destroy())

  /**
   * The objects in this universe.
   */
  def objects: Set[UniverseObject] = _objects

  /**
   * The qubits in this universe.
   */
  def qubits: Map[Int, Qubit] = _qubits

  /**
   * The state of this universe, given by Σ,,i,, q,,i,, · 2^i^, where q,,i,, is the state of the ith qubit.
   */
  def state: Int =
    qubits.values.map(q => if (q.on) pow(2, q.id).toInt else 0).sum

  /**
   * Creates this universe with the player and quballs in their starting positions.
   *
   * @param size the number of quballs to create
   */
  def create(size: Int): Unit = {
    for (i <- 0 until size) {
      new Quball(this, i, false, new Vec2d(1 + i, 1)).create()
    }
    new Player(this, new Vec2d(0, 0)).create()
  }

  /**
   * Adds the object to this universe.
   *
   * @param universeObject the object to add
   */
  def add(universeObject: UniverseObject): Unit = _objects += universeObject

  /**
   * Adds the qubit to this universe.
   *
   * @param qubit the qubit to add
   */
  def add(qubit: Qubit): Unit = _qubits += (qubit.id -> qubit)

  /**
   * Creates a copy of this universe and all of its objects.
   *
   * @return a copy of this universe
   */
  def copy(): Universe = {
    val universe = new Universe()
    objects.foreach(_.copyTo(universe))
    universe.amplitude = amplitude
    universe
  }

  /**
   * Draws this universe.
   */
  def draw(): Unit = objects.foreach(_.draw())
}
