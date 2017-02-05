package com.xap4o.twony.processing

import com.xap4o.twony.config.ProcessingConfig
import com.xap4o.twony.db.{AnalyzeResultDb, SearchKeywordsDb}
import com.xap4o.twony.twitter.TwitterClient
import com.xap4o.twony.utils.Async._
import com.xap4o.twony.utils.{StrictLogging, Timer}
import monix.eval.Task
import monix.execution.CancelableFuture
import monix.reactive.Observable

import scala.concurrent.duration._
import scala.util.{Failure, Success}
import com.xap4o.twony.utils.MonixSugar._

class PeriodicProcessing(
  job: AnalyzeJob,
  config: ProcessingConfig,
  resultDb: AnalyzeResultDb,
  keywordsDb: SearchKeywordsDb
) extends StrictLogging {

  def start(): CancelableFuture[Unit] = {
    Observable.intervalWithFixedDelay(0.seconds, config.interval).map(_ => process()).completedL.runAsync
  }

  private def process(): Unit = {
    keywordsDb.getAll().toObservable.rightFlatMap { keywords =>
      sequence(keywords.map(k => job.process(k)))
    }
    .foreach {
      case Success(res) =>
        resultDb.persist(res)
        LOG.info(res.toString)
      case Failure(error) =>
        LOG.error("Error while processing:", error)
    }
  }
}
