package com.xap4o.twony

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.stream.Materializer
import com.xap4o.twony.Utils._
import com.xap4o.twony.http.HttpUtils
import com.xap4o.twony.model.TwitterModel.Tweet
import spray.json
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class AnalyzerClient(config: AppConfig) extends json.DefaultJsonProtocol {
  def analyze(tweet: Tweet)(implicit ec: ExecutionContext, as: ActorSystem, m: Materializer): Future[Try[Boolean]] = {
    val req: HttpRequest = HttpRequest()
      .withUri(s"${config.processing.analyzeHost}/analyze")
      .withMethod(HttpMethods.POST)
      .withEntity(HttpEntity(ContentTypes.`application/json`, tweet.toJson.compactPrint))

    HttpUtils.getJson(req, config.processing.timeout).map(_.convertTo[Boolean]).toTry
  }
}
