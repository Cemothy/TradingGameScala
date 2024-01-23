package de.htwg.se.TradingGame.util

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UndoManagerSpec extends AnyFlatSpec with Matchers {
  "An UndoManager" should "execute doStep correctly" in {
    val command: Command = mock(classOf[Command])
    val undoManager = new UndoManager

    undoManager.doStep(command)
    verify(command).doStep
  }

  it should "execute undoStep correctly" in {
    val command: Command = mock(classOf[Command])
    val undoManager = new UndoManager

    undoManager.doStep(command)
    undoManager.undoStep
    verify(command).undoStep
  }

  it should "execute redoStep correctly" in {
    val command: Command = mock(classOf[Command])
    val undoManager = new UndoManager

    undoManager.doStep(command)
    undoManager.undoStep
    undoManager.redoStep
    verify(command).redoStep
  }
}