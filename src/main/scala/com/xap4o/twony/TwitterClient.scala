package com.xap4o.twony

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials}
import com.xap4o.twony.AkkaStuff._
import spray.json

import scala.util.{Failure, Success}


object TwitterClient extends SprayJsonSupport with json.DefaultJsonProtocol {
  sealed trait Error

  case class AuthError(message: String, t: Throwable) extends Error

  case class AuthResponse(bearer: String, authToken: String)

  implicit val responseFormat = jsonFormat2(AuthResponse)
}

class TwitterClient(config: AppConfig) extends StrictLogging {
  val contentType = ContentType(MediaType.applicationWithFixedCharset("x-www-form-urlencoded", HttpCharsets.`UTF-8`))

  def open(): Unit = {
    val req: HttpRequest = HttpRequest()
      .withUri("https://api.twitter.com/oauth2/token")
      .withHeaders(Authorization(BasicHttpCredentials(config.twitterKey, config.twitterSecret)))
      .withEntity(HttpEntity(contentType, "grant_type=client_credentials"))

    Http().singleRequest(req).onComplete {
      case Success(response) =>
        LOG.info(s"Received: ${response.status}")
        response.entity.toStrict(config.timeout).map(body).foreach(s => println(s"body is: `$s`"))
//        response.entity.toStrict(config.timeout).map(body).map(_.parseJson.convertTo[AuthResponse].authToken)
//          .onComplete {
//            case Success(token) => LOG.info(s"received token: $token")
//            case Failure(t) => LOG.error("Error getting body:", t)
//          }
      case Failure(t) =>
        LOG.error("Error while obtaining auth:", t)
    }
  }


  private def body(entity: HttpEntity.Strict): String =
    entity.data.decodeString(entity.contentType.charsetOption.getOrElse(HttpCharsets.`UTF-8`).value)

}

