package com.xap4o.twony

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.FiniteDuration

case class AppConfig(http: HttpConfig, processing: ProcessingConfig)
case class HttpConfig(host: String, port: Int)
case class ProcessingConfig(interval: FiniteDuration, twitterKey: String, twitterSecret: String, timeout: FiniteDuration)

object AppConfig {
  implicit def asFiniteDuration(d: java.time.Duration): FiniteDuration =
    scala.concurrent.duration.Duration.fromNanos(d.toNanos)

  def load(): AppConfig = {
    val c = ConfigFactory.load().getConfig("app")
    val http = c getConfig "http"
    val proc = c getConfig "processing"
    AppConfig(
      HttpConfig(
        http getString "host",
        http getInt "port"
      ),
      ProcessingConfig(
        proc getDuration "interval",
        proc getString "twitterKey",
        proc getString "twitterSecret",
        proc getDuration "timeout"
      )
    )
  }
}
