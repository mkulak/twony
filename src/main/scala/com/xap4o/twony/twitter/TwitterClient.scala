package com.xap4o.twony.twitter

import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, OAuth2BearerToken}
import com.xap4o.twony.config.ProcessingConfig
import com.xap4o.twony.http.HttpClient
import com.xap4o.twony.twitter.TwitterModel.{AuthResponse, SearchResponse}
import com.xap4o.twony.utils.Async._
import com.xap4o.twony.utils.StrictLogging

import scala.concurrent.Future



trait TwitterClient {
  def open(): Future[Token]
  def search(token: Token, keyword: String): Future[SearchResponse]
}

class TwitterClientImpl(config: ProcessingConfig, http: HttpClient) extends TwitterClient with StrictLogging {
  val contentType = ContentType(MediaType.applicationWithFixedCharset("x-www-form-urlencoded", HttpCharsets.`UTF-8`))

  def open(): Future[Token] = {
    val req: HttpRequest = HttpRequest()
      .withUri(s"${config.twitterHost}/oauth2/token")
      .withMethod(HttpMethods.POST)
      .withHeaders(Authorization(BasicHttpCredentials(config.twitterKey, config.twitterSecret)))
      .withEntity(HttpEntity(contentType, "grant_type=client_credentials"))

    http.make[AuthResponse](req, config.timeout).map(r => Token(r.accessToken))
  }

  def search(token: Token, keyword: String): Future[SearchResponse] = {
    val req: HttpRequest = HttpRequest()
      .withUri(Uri(s"${config.twitterHost}/1.1/search/tweets.json").withQuery(Query("q" -> keyword)))
      .withMethod(HttpMethods.GET)
      .withHeaders(Authorization(OAuth2BearerToken(token.value)))

    http.make[SearchResponse](req, config.timeout)
  }
}

case class Token(value: String)

