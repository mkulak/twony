package com.xap4o.twony.http

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpCharsets, HttpEntity, HttpRequest}
import com.xap4o.twony.utils.Async._
import com.xap4o.twony.utils.StrictLogging
import fs2.Task
import spray.json._

import scala.concurrent.duration._
import scala.util.Try
import com.xap4o.twony.utils.Fs2Sugar._

trait HttpClient {
  def make[T : JsonReader](req: HttpRequest, timeout: FiniteDuration): Task[Try[T]]
}

class HttpClientImpl extends HttpClient with StrictLogging {
  override def make[T : JsonReader](req: HttpRequest, timeout: FiniteDuration): Task[Try[T]] =
    Http().singleRequest(req).flatMap { r =>
      r.entity.toStrict(timeout).map(body).map(_.parseJson.convertTo[T])
    }.toTask

  private def body(entity: HttpEntity.Strict): String =
    entity.data.decodeString(entity.contentType.charsetOption.getOrElse(HttpCharsets.`UTF-8`).value)
}
