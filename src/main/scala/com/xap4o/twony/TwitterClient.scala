package com.xap4o.twony

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials}
import com.xap4o.twony.AkkaStuff._
import com.xap4o.twony.TwitterClient.AuthResponse
import spray.json.{DefaultJsonProtocol, _}

import scala.concurrent.Future


object TwitterClient extends SprayJsonSupport with DefaultJsonProtocol {
  sealed trait Error

  case class AuthError(message: String) extends Error

  case class AuthResponse(bearer: String, authToken: String)

  implicit val responseFormat = jsonFormat2(AuthResponse)
}

class TwitterClient(config: AppConfig) {
  val contentType = ContentType(MediaType.applicationWithFixedCharset("x-www-form-urlencoded", HttpCharsets.`UTF-8`))

  object JsonSupport

  def open(): Future[Unit] = {
    val req: HttpRequest = HttpRequest()
      .withUri("https://api.twitter.com/oauth2/token")
      .withMethod(HttpMethods.POST)
      .withHeaders(Authorization(BasicHttpCredentials(config.twitterKey, config.twitterSecret)))
      .withEntity(HttpEntity(contentType, "grant_type=client_credentials"))

    Http().singleRequest(req).map { response =>
      println(s"Received: ${response.status}")
      response.entity.toStrict(config.timeout).map(body).map(_.parseJson.convertTo[AuthResponse].authToken)
      //      Either.catchNonFatal(wsResponse.json).leftMap(t => TwitterAuthError(t.toString)).map {
      //        case obj: JsObject => Right(obj.value("access_token"))
      //        case _ => Left(TwitterAuthError(wsResponse.body))
      //      }
    }
  }


  private def body(entity: HttpEntity.Strict): String =
    entity.data.decodeString(entity.contentType.charsetOption.getOrElse(HttpCharsets.`UTF-8`).value)

}

