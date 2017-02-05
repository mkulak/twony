package com.xap4o.twony.processing

import com.xap4o.twony.twitter.TwitterClient
import com.xap4o.twony.utils.StrictLogging
import com.xap4o.twony.utils.Timer.CreateTimer
import fs2.Task

import scala.util.{Success, Try}
import com.xap4o.twony.utils.Fs2Sugar._
import com.xap4o.twony.utils.Async._

class AnalyzeJob(
  twitterClient: TwitterClient,
  analyzerClient: AnalyzerClient,
  createTimer: CreateTimer) extends StrictLogging {

  def process(query: String): Task[Try[AnalyzeResult]] = {
    val timer = createTimer()
    twitterClient
      .open()
      .rightFlatMap(token => twitterClient.search(token, query))
      .rightFlatMap { searchResult =>
        Task.parallelTraverse(searchResult.tweets)(analyzerClient.analyze).map { results =>
          val success = results.collect { case Success(result) => result }
          val positiveCount = success.count(identity)
          val negativeCount = success.size - positiveCount
          val errorsCount = results.size - success.size
          val duration = timer()
          val realQuery = searchResult.metadata.query
          Success(AnalyzeResult(realQuery, results.size, positiveCount, negativeCount, errorsCount, duration))
        }
      }
  }
}

case class AnalyzeResult(query: String, total: Int, positive: Int, negative: Int, errors: Int, duration: Long)