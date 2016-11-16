package com.xap4o.twony

import java.net.URLEncoder
import java.util.Base64

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, OAuth2BearerToken}
import com.xap4o.twony.AkkaStuff._
import com.xap4o.twony.TwitterClient.AuthResponse
import spray.json
import spray.json._

import scala.concurrent.Future
import scala.util.{Failure, Success}


object TwitterClient extends SprayJsonSupport with json.DefaultJsonProtocol {
  sealed trait Error

  case class AuthError(message: String, t: Throwable) extends Error

  case class AuthResponse(tokenType: String, accessToken: String)

//  implicit val responseFormat = jsonFormat2(AuthResponse)

  implicit object AuthResponseJsonFormat extends RootJsonFormat[AuthResponse] {
    def write(r: AuthResponse) =
      JsObject("token_type" -> JsString(r.tokenType), "access_token" -> JsString(r.accessToken))

    def read(value: JsValue) = value match {
      case JsObject(values) =>
        AuthResponse(
          values("token_type").asInstanceOf[JsString].value,
          values("access_token").asInstanceOf[JsString].value
        )
      case _ => deserializationError(s"bad data $value")
    }
  }

  implicit class EncodableString(val s: String) extends AnyVal {
    def urlEncode() = URLEncoder.encode(s, "UTF-8")
    def base64Encode() = new String(Base64.getEncoder.encode(s.getBytes("UTF-8")), "UTF-8")
  }

  private def pt[A](a: A): A = {
    println(a)
    a
  }
}

class TwitterClient(config: AppConfig) extends StrictLogging {
  val contentType = ContentType(MediaType.applicationWithFixedCharset("x-www-form-urlencoded", HttpCharsets.`UTF-8`))

  def open(): Future[String] = {
    val req: HttpRequest = HttpRequest()
      .withUri("https://api.twitter.com/oauth2/token")
      .withMethod(HttpMethods.POST)
      .withHeaders(Authorization(BasicHttpCredentials(config.twitterKey, config.twitterSecret)))
      .withEntity(HttpEntity(contentType, "grant_type=client_credentials"))

    val f = Http().singleRequest(req).flatMap { r =>
      r.entity.toStrict(config.timeout).map(body).map(_.parseJson).map(_.convertTo[AuthResponse].accessToken)
    }
    f.onComplete {
      case Success(token) => LOG.info(s"received token: $token"); token
      case Failure(t) =>
        LOG.error("Error while obtaining auth:", t)
    }
    f
  }

  def getTweets(token: String, keyword: String): Future[String] = {
    val req: HttpRequest = HttpRequest()
      .withUri(Uri("https://api.twitter.com/1.1/search/tweets.json").withQuery(Query("q" -> keyword)))
      .withMethod(HttpMethods.GET)
      .withHeaders(Authorization(OAuth2BearerToken(token)))

    val f = Http().singleRequest(req).flatMap { r =>
      r.entity.toStrict(config.timeout).map(body).map(_.parseJson).map(_.prettyPrint)//_.convertTo[AuthResponse].accessToken)
    }
    f.onComplete {
      case Success(json) => LOG.info(s"received: $json"); json
      case Failure(t) =>
        LOG.error("Error while obtaining auth:", t)
    }
    f
  }


  private def body(entity: HttpEntity.Strict): String =
    entity.data.decodeString(entity.contentType.charsetOption.getOrElse(HttpCharsets.`UTF-8`).value)

}

