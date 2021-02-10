package com.ka2kama.life.writer

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.ka2kama.life.model.Board

object ConsoleWriter {
  def apply()(implicit formatter: BoardFormatter[this.type]): Behavior[Board] =
    Behaviors.receiveMessage { board =>
      println(formatter.format(board))
      Behaviors.same
    }
}
