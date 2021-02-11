package com.ka2kama.life

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}

import scala.concurrent.duration.{DurationInt, FiniteDuration}

final case class GameSettings(
    height: Int,
    width: Int,
    interval: FiniteDuration,
    maxGeneration: Option[BigInt] = None,
)

object GameManager {
  sealed trait Command
  case object Start                     extends Command
  private case object AdvanceGeneration extends Command
  private final case class WrappedBoard(res: BoardFactory.Response)
      extends Command

  def apply(
      recipientList: ActorRef[Board],
      gameSettings: GameSettings,
  ): Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.withTimers { timer =>
        new GameManager(context, timer, recipientList, gameSettings).init()
      }
    }
}

final private class GameManager(
    context: ActorContext[GameManager.Command],
    timer: TimerScheduler[GameManager.Command],
    recipientList: ActorRef[Board],
    gameSettings: GameSettings,
) {
  import GameManager._

  private[this] lazy val boardResponseMapper: ActorRef[BoardFactory.Response] =
    context.messageAdapter(WrappedBoard.apply)

  private[this] lazy val factory: ActorRef[BoardFactory.Command] =
    context.spawn(
      BoardFactory(gameSettings.height, gameSettings.width),
      "board-factory",
    )

  def init(): Behavior[Command] = Behaviors.receiveMessage {
    case Start =>
      timer.startTimerAtFixedRate(AdvanceGeneration, gameSettings.interval)
      running()

    // runningに切り替わる前に受信してしまった場合は時間をおいて再送
    case msg =>
      timer.startSingleTimer(msg, 10.milliseconds)
      Behaviors.same
  }

  def running(): Behavior[Command] =
    Behaviors.receiveMessage {
      case AdvanceGeneration =>
        factory ! BoardFactory.AdvanceGeneration(boardResponseMapper)
        Behaviors.same

      case WrappedBoard(res) =>
        res match {
          case BoardFactory.CreatedBoard(board) =>
            recipientList ! board
            Behaviors.same

          case BoardFactory.NoChanged =>
            context.log.info("No more changes.")
            Behaviors.stopped
        }

      case Start =>
        context.log.warn("already started")
        Behaviors.same
    }
}
