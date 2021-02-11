package com.ka2kama.life.writer

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import com.ka2kama.life.Board

object ConsoleWriter {
  def apply()(implicit formatter: BoardFormatter[this.type]): Behavior[Board] =
    Behaviors.setup { context =>
      new ConsoleWriter(context, formatter).receive()
    }
}

private final class ConsoleWriter(
    context: ActorContext[Board],
    formatter: BoardFormatter[ConsoleWriter.type],
) {
  def receive(): Behavior[Board] = Behaviors.receiveMessage { board =>
    printBoard(board)
    Behaviors.same
  }

  def printBoard(board: Board): Unit = {
    print("\u001B[0;0H")
    print("\u001B[2J")
    print(formatter.format(board))
  }
}
