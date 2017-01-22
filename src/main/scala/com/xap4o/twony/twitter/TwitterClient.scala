package com.xap4o.twony.twitter

import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, OAuth2BearerToken}
import com.xap4o.twony.config.ProcessingConfig
import com.xap4o.twony.http.HttpClient
import com.xap4o.twony.twitter.TwitterModel.{AuthResponse, SearchResponse}
import com.xap4o.twony.utils.StrictLogging
import monix.eval.Task

import scala.util.Try
import com.xap4o.twony.utils.MonixSugar._



trait TwitterClient {
  def open(): Task[Try[Token]]
  def search(token: Token, keyword: String): Task[Try[SearchResponse]]
  def searchAll(token: Token, keyword: String): Task[Try[SearchResponse]]
}

class TwitterClientImpl(config: ProcessingConfig, http: HttpClient) extends TwitterClient with StrictLogging {
  val contentType = ContentType(MediaType.applicationWithFixedCharset("x-www-form-urlencoded", HttpCharsets.`UTF-8`))

  def open(): Task[Try[Token]] = {
    val req: HttpRequest = HttpRequest()
      .withUri(s"${config.twitterHost}/oauth2/token")
      .withMethod(HttpMethods.POST)
      .withHeaders(Authorization(BasicHttpCredentials(config.twitterKey, config.twitterSecret)))
      .withEntity(HttpEntity(contentType, "grant_type=client_credentials"))

    http.make[AuthResponse](req, config.timeout).mapT(r => Token(r.accessToken))
  }

  def search(token: Token, keyword: String): Task[Try[SearchResponse]] = {
    val req: HttpRequest = HttpRequest()
      .withUri(Uri(s"${config.twitterHost}/1.1/search/tweets.json").withQuery(Query("q" -> keyword)))
      .withMethod(HttpMethods.GET)
      .withHeaders(Authorization(OAuth2BearerToken(token.value)))

    http.make[SearchResponse](req, config.timeout)
  }

  override def searchAll(token: Token, keyword: String): Task[Try[SearchResponse]] = ???
}

case class Token(value: String)

