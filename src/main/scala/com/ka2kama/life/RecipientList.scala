package com.ka2kama.life

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object RecipientList {
  def apply[T](recipientList: Seq[ActorRef[T]]): Behavior[T] =
    Behaviors.receiveMessage { msg =>
      recipientList.foreach(_ ! msg)
      Behaviors.same
    }
}
