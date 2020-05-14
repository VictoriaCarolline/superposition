package superposition.game

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx.graphics
import com.badlogic.gdx.backends.lwjgl3.{Lwjgl3Application, Lwjgl3ApplicationConfiguration}
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import superposition.game.Superposition.Playlist
import superposition.game.system.BeamRenderer.{BeamRendererFamily, renderBeam}
import superposition.game.system.SpriteRenderer.{SpriteRendererFamily, renderSprite}
import superposition.game.system._

/** The Superposition game. */
private final class Superposition extends Game {
  /** The entity engine. */
  private val engine: Engine = new Engine

  /** The time it took to render the last frame in nanoseconds. */
  private var renderNanoTime: Long = 0

  override def create(): Unit = {
    val levels = new LevelPlaylist(engine)
    levels.appendAll(new TmxMapLoader(ResourceResolver), Playlist)
    levels.play()

    engine.addSystem(new LevelSystem(levels))
    engine.addSystem(new MapRenderer)
    engine.addSystem(new MultiverseRenderer)
    engine.addSystem(new PlayerInputSystem(() => levels.current))
    engine.addSystem(new LaserInputSystem(() => levels.current))
    engine.addSystem(new RenderingSystem(Iterable(
      SpriteRendererFamily -> renderSprite(() => levels.current),
      BeamRendererFamily -> renderBeam(() => levels.current))))
    engine.addSystem(new DebugInfoSystem(() => renderNanoTime))
  }

  override def render(): Unit = {
    val startTime = System.nanoTime()
    engine.update(graphics.getDeltaTime)
    super.render()
    renderNanoTime = System.nanoTime() - startTime
  }
}

/** The main class for Superposition. */
private object Superposition {
  /** The level playlist. */
  private val Playlist: Seq[String] = Seq(
    "level1.tmx",
    "level2.tmx",
    "level3.tmx",
    "level4.tmx",
    "win.tmx")

  /** The entry point for Superposition.
    *
    * @param args the command-line arguments
    */
  def main(args: Array[String]): Unit = {
    val config = new Lwjgl3ApplicationConfiguration
    config.setTitle("Superposition")
    config.setWindowedMode(1536, 864)
    config.useOpenGL3(true, 3, 3)
    new Lwjgl3Application(new Superposition, config)
  }
}
