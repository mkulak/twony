package com.xap4o.twony.processing

import com.xap4o.twony.twitter.TwitterClient
import com.xap4o.twony.utils.Async._
import com.xap4o.twony.utils.{StrictLogging, Timer}

import scala.concurrent.Future
import scala.util.Success

class AnalyzeJob(twitterClient: TwitterClient, analyzerClient: AnalyzerClient) extends StrictLogging {

  def process(query: String): Future[AnalyzeResult] = {
    val timer = new Timer
    twitterClient
      .open()
      .flatMap(token => twitterClient.search(token, query))
      .flatMap { searchResult =>
        Future.sequence(searchResult.tweets.map(analyzerClient.analyze)).map { results =>
          val success = results.collect {case Success(result) => result}
          val positiveCount = success.count(identity)
          val negativeCount = success.size - positiveCount
          val errorsCount = results.size - success.size
          val duration = timer.duration()
          AnalyzeResult(searchResult.metadata.query, results.size, positiveCount, negativeCount, errorsCount, duration)
        }
      }
  }
}

case class AnalyzeResult(query: String, total: Int, positive: Int, negative: Int, errors: Int, duration: Long)