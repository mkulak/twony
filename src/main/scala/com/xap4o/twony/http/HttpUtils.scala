package com.xap4o.twony.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpCharsets, HttpEntity, HttpRequest}
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.xap4o.twony.{HttpConfig, StrictLogging}
import spray.json.{JsValue, _}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

object HttpUtils extends StrictLogging {
  def body(entity: HttpEntity.Strict): String =
    entity.data.decodeString(entity.contentType.charsetOption.getOrElse(HttpCharsets.`UTF-8`).value)

  def getJson(req: HttpRequest, timeout: FiniteDuration)(
    implicit ec: ExecutionContext, as: ActorSystem, m: Materializer): Future[JsValue] =
    Http().singleRequest(req).flatMap { r => r.entity.toStrict(timeout).map(body).map(_.parseJson) }

  def startServer(config: HttpConfig, route: Route)(
    implicit ec: ExecutionContext, as: ActorSystem, m: Materializer): Future[ServerBinding] = {
    Http().bindAndHandle(route, config.host, config.port).map {s =>
      LOG.info(s"Server started at http://${config.host}:${config.port}")
      s
    }
  }
}
