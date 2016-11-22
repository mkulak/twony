package com.xap4o.twony

import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpCharsets, HttpEntity, HttpRequest}
import akka.http.scaladsl.server.Route
import com.xap4o.twony.AkkaStuff._
import spray.json.{JsValue, _}

import scala.concurrent.Future
import scala.concurrent.duration._

object HttpUtils extends StrictLogging {
  def body(entity: HttpEntity.Strict): String =
    entity.data.decodeString(entity.contentType.charsetOption.getOrElse(HttpCharsets.`UTF-8`).value)

  def getJson(req: HttpRequest): Future[JsValue] =
    Http().singleRequest(req).flatMap { r => r.entity.toStrict(5.seconds).map(body).map(_.parseJson) }

  def startServer(config: HttpConfig, route: Route): Future[ServerBinding] = {
    Http().bindAndHandle(route, config.host, config.port).map {s =>
      LOG.info(s"Server started at http://${config.host}:${config.port}")
      s
    }
  }
}
