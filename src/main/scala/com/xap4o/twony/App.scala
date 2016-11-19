package com.xap4o.twony

import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import com.xap4o.twony.AkkaStuff._
import com.xap4o.twony.model.TwitterModel.Tweet
import spray.json.BasicFormats

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object App extends StrictLogging with BasicFormats {
  def main(args: Array[String]): Unit = {
    val config = AppConfig.load()

    val client = new TwitterClient(config)
    val futures = client.open().flatMap(token => client.search(token, "trump"))
      .flatMap(result => Future.sequence(result.tweets.map(tweet => analyze(tweet))))

    futures.foreach { results =>
      val success = results.collect {case Success(result) => result}
      val (positive, negative) = success.partition(identity)
      LOG.info(s"received: ${results.size} results. Positive ${positive.size}, " +
        s"negative: ${negative.size}, fails: ${results.size - success.size}")
    }
//        LOG.info(s"${response.tweets.map(t => "@" + t.username + ": " + t.text).mkString("\n")}")
    val server = new AnalizerServer(config)
    val future: Future[ServerBinding] = server.start()
    LOG.info("Press Enter to terminate")
    StdIn.readLine()
    future.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }

  def analyze(tweet: Tweet): Future[Try[Boolean]] = {
    val req: HttpRequest = HttpRequest()
      .withUri("http://localhost:8080/analyze")
      .withMethod(HttpMethods.POST)
      .withEntity(HttpEntity(ContentTypes.`application/json`, tweet.toJson.compactPrint))

    HttpUtils.getJson(req).map(_.convertTo[Boolean]).toTry
  }

  implicit class TryFuture[T](f: Future[T]) {
    def toTry: Future[Try[T]] = f.map(Success(_)).recover{ case x => Failure(x) }
  }
}

