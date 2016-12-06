package com.xap4o.twony.processing

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import com.xap4o.twony.config.AppConfig
import com.xap4o.twony.http.HttpUtils
import com.xap4o.twony.twitter.TwitterModel.Tweet
import com.xap4o.twony.utils.Async._
import spray.json
import spray.json._

import scala.concurrent.Future
import scala.util.Try

trait AnalyzerClient {
  def analyze(tweet: Tweet): Future[Try[Boolean]]
}

class AnalyzerClientImpl(config: AppConfig) extends AnalyzerClient with json.DefaultJsonProtocol {
  override def analyze(tweet: Tweet): Future[Try[Boolean]] = {
    val req: HttpRequest = HttpRequest()
      .withUri(s"${config.processing.analyzeHost}/analyze")
      .withMethod(HttpMethods.POST)
      .withEntity(HttpEntity(ContentTypes.`application/json`, tweet.toJson.compactPrint))

    HttpUtils.getJson(req, config.processing.timeout).map(_.convertTo[Boolean]).toTry
  }
}
