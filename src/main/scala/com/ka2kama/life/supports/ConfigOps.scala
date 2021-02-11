package com.ka2kama.life.supports

import com.typesafe.config.Config

import scala.concurrent.duration.{Duration, FiniteDuration}

object ConfigOps {
  implicit class RichConfig(val underlying: Config) extends AnyVal {
    private def getConfigOption[A](path: String, f: String => A): Option[A] =
      if (underlying.hasPath(path)) {
        Some(f(path))
      } else None

    def getIntOption(path: String): Option[Int] =
      getConfigOption(path, underlying.getInt)

    def getStringOption(path: String): Option[String] =
      getConfigOption(path, underlying.getString)

    def getBigIntOption(path: String): Option[BigInt] =
      getStringOption(path).map(BigInt.apply)

    def getDurationOption(path: String): Option[Duration] =
      getStringOption(path).map(Duration.apply)

    def getFiniteDurationOption(path: String): Option[FiniteDuration] =
      getDurationOption(path).map(_.asInstanceOf[FiniteDuration])
  }
}
