package com.xap4o.twony

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.stream.Materializer
import com.xap4o.twony.http.HttpUtils
import com.xap4o.twony.model.TwitterModel.Tweet
import spray.json
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class AnalyzeJob(config: AppConfig)(
  implicit ec: ExecutionContext, as: ActorSystem, m: Materializer) extends StrictLogging with json.DefaultJsonProtocol {

  def process(query: String): Future[AnalyzeResult] = {
    val client = new TwitterClient(config)
    val timer = new Timer
    client
      .open()
      .flatMap(token => client.search(token, query))
      .flatMap { searchResult =>
        Future.sequence(searchResult.tweets.map(analyze)).map { results =>
          val success = results.collect {case Success(result) => result}
          val positiveCount = success.count(identity)
          val negativeCount = success.size - positiveCount
          val errorsCount = results.size - success.size
          val duration = timer.duration()
          AnalyzeResult(searchResult.metadata.query, results.size, positiveCount, negativeCount, errorsCount, duration)
        }
      }
  }

  def analyze(tweet: Tweet): Future[Try[Boolean]] = {
    val req: HttpRequest = HttpRequest()
      .withUri("http://localhost:8080/analyze")
      .withMethod(HttpMethods.POST)
      .withEntity(HttpEntity(ContentTypes.`application/json`, tweet.toJson.compactPrint))

    HttpUtils.getJson(req, config.processing.timeout).map(_.convertTo[Boolean]).toTry
  }

  implicit class TryFuture[T](f: Future[T]) {
    def toTry: Future[Try[T]] = f.map(Success(_)).recover{ case x => Failure(x) }
  }
}

case class AnalyzeResult(query: String, total: Int, positive: Int, negative: Int, errors: Int, duration: Long)