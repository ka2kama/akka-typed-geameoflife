package com.ka2kama.life

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import com.ka2kama.life.model.Board
import com.ka2kama.life.writer.ConsoleWriter
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.util.chaining.scalaUtilChainingOps

object Main {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()

    ActorSystem[Nothing](Guardian(config), "game-of-life")
    ()
  }
}

object Guardian {
  def apply(config: Config): Behavior[Nothing] = Behaviors.setup[Nothing] {
    context =>
      val interval = config
        .getString("game.interval")
        .pipe(Duration.apply)
        .pipe(d => FiniteDuration(d.length, d.unit))

      context.log.info(s"interval is $interval")

      val recipientList = {
        val list: Seq[ActorRef[Board]] = createRecipientList(context, config)
        context.spawn(RecipientList(list), "recipient-list")
      }

      val gameManager =
        context.spawn(GameManager(recipientList, interval), "game-manager")

      gameManager ! GameManager.Start

      Behaviors.empty
  }

  private def createRecipientList(
      context: ActorContext[Nothing],
      config: Config,
  ): Seq[ActorRef[Board]] = {
    val buf = ListBuffer.empty[ActorRef[Board]]

    val consoleWriter = context.spawn(ConsoleWriter(), "console-writer")
    buf += consoleWriter

    buf.toList
  }
}
