package com.xap4o.twony

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.FiniteDuration

case class AppConfig(http: HttpConfig, twitterKey: String, twitterSecret: String, timeout: FiniteDuration)
case class HttpConfig(host: String, port: Int)

object AppConfig {
  implicit def asFiniteDuration(d: java.time.Duration): FiniteDuration =
    scala.concurrent.duration.Duration.fromNanos(d.toNanos)

  def load(): AppConfig = {
    val c = ConfigFactory.load().getConfig("app")
    val http = c getConfig "http"
    AppConfig(
      HttpConfig(http getString "host", http getInt "port"),
      c getString "twitterKey",
      c getString "twitterSecret",
      c getDuration "timeout"
    )
  }
}
