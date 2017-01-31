package com.xap4o.twony.processing

import com.xap4o.twony.config.ProcessingConfig
import com.xap4o.twony.db.{AnalyzeResultDb, SearchKeywordsDb}
import com.xap4o.twony.twitter.TwitterClient
import com.xap4o.twony.utils.Async._
import com.xap4o.twony.utils.{Async, StrictLogging, Timer}
import rx.lang.scala.Observable

import scala.concurrent.duration._
import scala.util.{Failure, Success}

class PeriodicProcessing(
  job: AnalyzeJob,
  config: ProcessingConfig,
  resultDb: AnalyzeResultDb,
  keywordsDb: SearchKeywordsDb
) extends StrictLogging {

  def start(): Unit = {
    Observable.interval(0.seconds, config.interval).foreach(_ => process())
  }

  private def process(): Unit =
    keywordsDb.getAll().rightFlatMapAsync(job.process).foreach {
      case Success(res) =>
        resultDb.persist(res)
        LOG.info(res.toString)
      case Failure(error) =>
        LOG.error(s"Error while processing: $error")
    }
}
