package com.xap4o.twony

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, OAuth2BearerToken}
import com.xap4o.twony.AkkaStuff._
import com.xap4o.twony.model.TwitterModel.{AuthResponse, SearchResponse}
import spray.json._

import scala.concurrent.Future



class TwitterClient(config: AppConfig) extends StrictLogging {
  val contentType = ContentType(MediaType.applicationWithFixedCharset("x-www-form-urlencoded", HttpCharsets.`UTF-8`))

  def open(): Future[String] = {
    val req: HttpRequest = HttpRequest()
      .withUri("https://api.twitter.com/oauth2/token")
      .withMethod(HttpMethods.POST)
      .withHeaders(Authorization(BasicHttpCredentials(config.twitterKey, config.twitterSecret)))
      .withEntity(HttpEntity(contentType, "grant_type=client_credentials"))

    Http().singleRequest(req).flatMap { r =>
      r.entity.toStrict(config.timeout).map(body).map(_.parseJson).map(_.convertTo[AuthResponse].accessToken)
    }
  }

  def getTweets(token: String, keyword: String): Future[SearchResponse] = {
    val req: HttpRequest = HttpRequest()
      .withUri(Uri("https://api.twitter.com/1.1/search/tweets.json").withQuery(Query("q" -> keyword)))
      .withMethod(HttpMethods.GET)
      .withHeaders(Authorization(OAuth2BearerToken(token)))

    Http().singleRequest(req).flatMap { r =>
      r.entity.toStrict(config.timeout).map(body).map(_.parseJson).map(_.convertTo[SearchResponse])
    }
  }

  private def body(entity: HttpEntity.Strict): String =
    entity.data.decodeString(entity.contentType.charsetOption.getOrElse(HttpCharsets.`UTF-8`).value)
}

