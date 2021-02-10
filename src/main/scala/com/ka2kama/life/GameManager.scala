package com.ka2kama.life

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import com.ka2kama.life.model.Board

import scala.concurrent.duration.FiniteDuration

object GameManager {
  sealed trait Command
  case object Start                                   extends Command
  private case object AdvanceGeneration               extends Command
  private final case class WrappedBoard(board: Board) extends Command

  def apply(
      recipientList: ActorRef[Board],
      interval: FiniteDuration,
  ): Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.withTimers { timer =>
        new GameManager(context, timer, recipientList, interval).receive()
      }
    }
}

final class GameManager private (
    context: ActorContext[GameManager.Command],
    timer: TimerScheduler[GameManager.Command],
    recipientList: ActorRef[Board],
    interval: FiniteDuration,
) {
  import GameManager._

  private[this] lazy val boardResponseMapper: ActorRef[Board] =
    context.messageAdapter(WrappedBoard.apply)

  private[this] lazy val factory: ActorRef[BoardFactory.Command] =
    context.spawn(BoardFactory(), "board-factory")

  private def receive(): Behavior[Command] =
    Behaviors.receiveMessage {
      case Start =>
        timer.startTimerAtFixedRate(AdvanceGeneration, interval)
        Behaviors.same

      case AdvanceGeneration =>
        factory ! BoardFactory.AdvanceGeneration(boardResponseMapper)
        Behaviors.same

      case WrappedBoard(board) =>
        recipientList ! board
        Behaviors.same
    }
}
