package com.xap4o.twony

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.FiniteDuration

case class AppConfig(host: String, port: Int, twitterKey: String, twitterSecret: String, timeout: FiniteDuration)

object AppConfig {
  implicit def asFiniteDuration(d: java.time.Duration): FiniteDuration =
    scala.concurrent.duration.Duration.fromNanos(d.toNanos)

  def load(): AppConfig = {
    val c: Config = ConfigFactory.load().getConfig("app")
    AppConfig(
      c getString "host",
      c getInt "port",
      c getString "twitterKey",
      c getString "twitterSecret",
      c getDuration "timeout"
    )
  }
}
