package com.xap4o.twony

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import spray.json.BasicFormats

import scala.concurrent.Future
import scala.io.StdIn

object App extends StrictLogging with BasicFormats {
  implicit val system = ActorSystem("main")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = scala.concurrent.ExecutionContext.global

  def main(args: Array[String]): Unit = {
    val config = AppConfig.load()
    new PeriodicProcessing(config).start()
    startServerAndBlock(config, AnalizerServer.route ~ StaticServer.route)
  }

  def startServerAndBlock(config: AppConfig, route: Route): Unit = {
    val future: Future[ServerBinding] = HttpUtils.startServer(config.http, route)
    LOG.info("Press Enter to terminate")
    StdIn.readLine()
    future.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }
}

