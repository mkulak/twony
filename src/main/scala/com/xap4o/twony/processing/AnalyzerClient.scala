package com.xap4o.twony.processing

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import com.xap4o.twony.config.ProcessingConfig
import com.xap4o.twony.http.HttpClient
import com.xap4o.twony.twitter.TwitterModel.Tweet
import com.xap4o.twony.utils.Async._
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.concurrent.Future
import scala.util.Try

trait AnalyzerClient {
  def analyze(tweet: Tweet): Future[Try[Boolean]]
}

class AnalyzerClientImpl(config: ProcessingConfig, http: HttpClient) extends AnalyzerClient {

  override def analyze(tweet: Tweet): Future[Try[Boolean]] = {
    val req: HttpRequest = HttpRequest()
      .withUri(s"${config.analyzeHost}/analyze")
      .withMethod(HttpMethods.POST)
      .withEntity(HttpEntity(ContentTypes.`application/json`, tweet.toJson.compactPrint))

    http.make[Boolean](req, config.timeout).toTry
  }
}
