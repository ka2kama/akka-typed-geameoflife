package com.ka2kama.life

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.ka2kama.life.model.Board

import java.time.LocalDateTime
import scala.collection.immutable.ArraySeq

object BoardFactory {

  sealed trait Command
  final case class AdvanceGeneration(replyTo: ActorRef[Board]) extends Command

  def apply(): Behavior[Command] = init

  private def init: Behavior[Command] = {
    Behaviors.receiveMessage { case AdvanceGeneration(replyTo) =>
      val board = initBoard()
      replyTo ! board
      receive(board)
    }
  }

  def receive(board: Board): Behavior[Command] =
    Behaviors.receiveMessage { case AdvanceGeneration(replyTo) =>
      val newBoard = advance(board)
      replyTo ! newBoard
      receive(newBoard)
    }

  private def initBoard(): Board = Board(ArraySeq.empty, 0, LocalDateTime.now())

  private def advance(board: Board): Board = {
    board.copy(
      generation = board.generation + 1,
      createdAt = LocalDateTime.now(),
    )
  }
}
