package com.xap4o.twony

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.FiniteDuration

case class AppConfig(http: HttpConfig, processing: ProcessingConfig, db: DbConfig)
case class HttpConfig(host: String, port: Int)
case class DbConfig(url: String, user: String, password: String, fullConfig: Config)

case class ProcessingConfig(
  interval: FiniteDuration, 
  twitterHost: String, 
  twitterKey: String, 
  twitterSecret: String, 
  timeout: FiniteDuration,
  analyzeHost: String
)

object AppConfig {
  implicit def asFiniteDuration(d: java.time.Duration): FiniteDuration =
    scala.concurrent.duration.Duration.fromNanos(d.toNanos)

  def load(): AppConfig = {
    val c = ConfigFactory.load().getConfig("app")
    val http = c getConfig "http"
    val proc = c getConfig "processing"
    val db = c getConfig "db"
    AppConfig(
      HttpConfig(
        http getString "host",
        http getInt "port"
      ),
      ProcessingConfig(
        proc getDuration "interval",
        proc getString "twitterHost",
        proc getString "twitterKey",
        proc getString "twitterSecret",
        proc getDuration "timeout",
        proc getString "analyzeHost"
      ),
      DbConfig(
        db getString "url",
        db getString "user",
        db getString "password",
        db
      )
    )
  }
}
