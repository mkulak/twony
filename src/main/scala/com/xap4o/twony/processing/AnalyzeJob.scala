package com.xap4o.twony.processing

import com.xap4o.twony.twitter.TwitterClient
import com.xap4o.twony.utils.{Async, StrictLogging}
import com.xap4o.twony.utils.Timer.CreateTimer

import scala.util.{Success, Try}
import rx.lang.scala.Observable

import Async._

class AnalyzeJob(
  twitterClient: TwitterClient,
  analyzerClient: AnalyzerClient,
  createTimer: CreateTimer) extends StrictLogging {

  def process(query: String): Observable[Try[AnalyzeResult]] = {
    val timer = createTimer()
    twitterClient
      .open()
      .rightFlatMapAsync(token => twitterClient.search(token, query))
      .rightFlatMapAsync { searchResult =>
        Async.sequence(searchResult.tweets.map(analyzerClient.analyze)).flatMap { results =>
          val success = results.collect { case Success(result) => result }
          val positiveCount = success.count(identity)
          val negativeCount = success.size - positiveCount
          val errorsCount = results.size - success.size
          val duration = timer()
          val realQuery = searchResult.metadata.query
          lift(AnalyzeResult(realQuery, results.size, positiveCount, negativeCount, errorsCount, duration))
        }
      }
  }
}

case class AnalyzeResult(query: String, total: Int, positive: Int, negative: Int, errors: Int, duration: Long)