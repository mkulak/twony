package com.xap4o.twony

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.stream.Materializer
import com.xap4o.twony.model.TwitterModel.Tweet
import spray.json
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class AnalyzeJob(config: AppConfig)(
  implicit ec: ExecutionContext, as: ActorSystem, m: Materializer) extends StrictLogging with json.DefaultJsonProtocol {

  def process(query: String): Unit = {
    val client = new TwitterClient(config)
    val futures = client
      .open()
      .flatMap(token => client.search(token, query))
      .flatMap(result => Future.sequence(result.tweets.map(analyze)))

    futures.foreach { results =>
      val success = results.collect {case Success(result) => result}
      val (positive, negative) = success.partition(identity)
      LOG.info(s"received: ${results.size} results. Positive ${positive.size}, " +
        s"negative: ${negative.size}, fails: ${results.size - success.size}")
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