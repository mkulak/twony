package com.xap4o.twony

import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.xap4o.twony.AkkaStuff._

import scala.concurrent.Future

class AnalizerServer(config: AppConfig) extends StrictLogging {
  val route =
    path("/") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }

  def start(): Future[ServerBinding] = {
    Http().bindAndHandle(route, config.host, config.port).map {s =>
      LOG.info(s"Server started at http://${config.host}:${config.port}")
      s
    }
  }
}

