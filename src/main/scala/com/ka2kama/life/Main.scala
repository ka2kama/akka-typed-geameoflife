package com.ka2kama.life

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}
import com.ka2kama.life.supports.ConfigOps.RichConfig
import com.ka2kama.life.writer.ConsoleWriter
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationInt

object Main {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()

    ActorSystem[Nothing](Guardian(config), "game-of-life")
    ()
  }
}

object Guardian {
  def apply(config: Config): Behavior[Nothing] = Behaviors.setup[Nothing] {
    context => new Guardian(context, config).start()
  }
}
private final class Guardian(context: ActorContext[_], config: Config) {
  def start(): Behavior[Nothing] = {
    val gameSettings  = getGameSettings
    val recipientList = createRecipientList()
    val gameManager = context.spawn(
      GameManager(recipientList, gameSettings),
      "game-manager",
    )
    context.watch(gameManager)

    gameManager ! GameManager.Start

    Behaviors.receiveSignal[Nothing] { case (_, Terminated(_)) =>
      Behaviors.stopped
    }
  }

  def getGameSettings: GameSettings = {
    val height = config.getIntOption("game.height").getOrElse(100)
    val width  = config.getIntOption("game.width").getOrElse(100)
    val interval = config
      .getFiniteDurationOption("game.interval")
      .getOrElse(100.milliseconds)
    val maxGeneration = config.getBigIntOption("game.max-generation")

    GameSettings(
      height = height,
      width = width,
      interval = interval,
      maxGeneration,
    )
  }

  def createRecipientList(): ActorRef[Board] = {
    val buf = ListBuffer.empty[ActorRef[Board]]

    val consoleWriter = context.spawn(ConsoleWriter(), "console-writer")
    buf += consoleWriter

    context.spawn(RecipientList(buf.toList), "recipient-list")
  }
}
