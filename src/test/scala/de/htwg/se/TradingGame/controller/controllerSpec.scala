package de.htwg.se.TradingGame.controller

import com.google.inject.Inject
import de.htwg.se.TradingGame.controller.Controllerimplementation.Controller
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.util.Command
import de.htwg.se.TradingGame.util.UndoManager
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito._
import org.mockito.MockitoAnnotations
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ControllerSpec extends AnyFlatSpec with Matchers {
  @Mock var interpreter: Interpreter = _
  @Mock var gameStateManager: IGameStateManager = _
  @Mock var undoManager: UndoManager = _

  override def withFixture(test: NoArgTest) = { // Define a setup method
    MockitoAnnotations.openMocks(this) // Initialize the mocks
    super.withFixture(test) // Continue with the test
  }

  "A Controller" should "compute input correctly" in {
    val controller = new Controller(interpreter, undoManager)
  
    controller.computeInput("undo")
    verify(undoManager).undoStep

    controller.computeInput("redo")
    verify(undoManager).redoStep

    controller.computeInput("any other input")
    verify(undoManager).doStep(any[Command])
  }

  it should "do step correctly" in {
    val controller = new Controller(interpreter,undoManager)

    controller.doStep("any input")
    verify(undoManager).doStep(any[Command])
  }

  it should "undo correctly" in {
    val controller = new Controller(interpreter, undoManager)
    controller.undo()
    verify(undoManager).undoStep
  }

  it should "redo correctly" in {
    val controller = new Controller(interpreter,undoManager)
    controller.redo()
    verify(undoManager).redoStep
  }

  it should "print descriptor correctly" in {
    val controller = new Controller(interpreter, undoManager)
    when(interpreter.descriptor).thenReturn("descriptor")
    controller.printDescriptor()
    controller.output should be ("descriptor")
  }
}