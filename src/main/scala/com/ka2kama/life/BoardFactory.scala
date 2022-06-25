package com.ka2kama.life

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}

import java.time.Instant
import scala.collection.immutable.ArraySeq
import scala.util.Random

object BoardFactory {

  sealed trait Command
  final case class AdvanceGeneration(replyTo: ActorRef[Response])
      extends Command

  sealed trait Response
  final case class CreatedBoard(board: Board) extends Response
  case object NoChanged                       extends Response

  def apply(height: Int, width: Int): Behavior[Command] = Behaviors.setup {
    context => new BoardFactory(context, height, width).init()
  }
}

private final class BoardFactory(
    context: ActorContext[BoardFactory.Command],
    height: Int,
    width: Int,
) {
  import BoardFactory._

  def init(): Behavior[Command] = {
    Behaviors.receiveMessage { case AdvanceGeneration(replyTo) =>
      val board = initBoard()
      replyTo ! CreatedBoard(board)
      receive(board)
    }
  }

  def receive(board: Board): Behavior[Command] =
    Behaviors.receiveMessage { case AdvanceGeneration(replyTo) =>
      val (newBoard, hasChanged) = advance(board)
      if (hasChanged) {
        replyTo ! CreatedBoard(newBoard)
      }
      else {
        replyTo ! NoChanged
      }
      receive(newBoard)
    }

  private def initBoard(): Board = {
    val table = Array.ofDim[CellState](height + 2, width + 2)
    for {
      row <- 0 until height + 2
      col <- 0 until width + 2
    } {
      val state =
        // 番兵
        if (col == 0 || row == 0 || col == width + 1 || row == height + 1) {
          CellState.Dead
        }
        else {
          if (Random.nextBoolean()) CellState.Alive else CellState.Dead
        }
      table(row)(col) = state
    }

    Board(
      ArraySeq.unsafeWrapArray(table.map(ArraySeq.unsafeWrapArray)),
      0,
      Instant.now(),
    )
  }

  private def advance(board: Board): (Board, Boolean) = {
    val table      = board.table
    val newTable   = Array.ofDim[CellState](height + 2, width + 2)
    var hasChanged = false
    for {
      row <- 0 until height + 2
      col <- 0 until width + 2
    } {
      val currentState = table(row)(col)
      val newState     =
        // 番兵
        if (col == 0 || row == 0 || col == width + 1 || row == height + 1) {
          CellState.Dead
        }
        else {
          // 8方向のうち、生きているセルをカウント
          val count = Iterator(
            // 左上、真上、右上
            table(row - 1)(col - 1),
            table(row - 1)(col),
            table(row - 1)(col + 1),
            // 左、右
            table(row)(col - 1),
            table(row)(col + 1),
            // 左下、真下、右下
            table(row + 1)(col - 1),
            table(row + 1)(col),
            table(row + 1)(col + 1),
          ).count(_ == CellState.Alive)

          count match {
            case 3                                    => CellState.Alive
            case 2 if currentState == CellState.Alive => CellState.Alive
            case _                                    => CellState.Dead
          }
        }

      newTable(row)(col) = newState

      if (newState != currentState) {
        hasChanged = true
      }
    }

    val newBoard = Board(
      ArraySeq.unsafeWrapArray(newTable.map(ArraySeq.unsafeWrapArray)),
      board.generation + 1,
      Instant.now(),
    )

    (newBoard, hasChanged)
  }
}
