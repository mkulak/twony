package com.xap4o.twony.processing

import com.xap4o.twony.config.ProcessingConfig
import com.xap4o.twony.db.{AnalyzeResultDb, SearchKeywordsDb}
import com.xap4o.twony.twitter.TwitterClient
import com.xap4o.twony.utils.Async._
import com.xap4o.twony.utils.{StrictLogging, Timer}
import fs2.Task
import fs2.Stream

import scala.concurrent.duration._
import scala.util.{Failure, Success}
import com.xap4o.twony.utils.Fs2Sugar._

import scala.concurrent.Future

class PeriodicProcessing(
  job: AnalyzeJob,
  config: ProcessingConfig,
  resultDb: AnalyzeResultDb,
  keywordsDb: SearchKeywordsDb
) extends StrictLogging {

  def start(): Future[Unit] = {
    process().scheduleWithFixedDelay(config.interval).run.unsafeRunAsyncFuture()
  }

  private def process(): Task[Unit] = {
    Stream.eval(keywordsDb.getAll())
      .rightFlatMap { keywords =>
        sequence(keywords.map(k => job.process(k)))
      }
      .evalMap {
        case Success(res) =>
          LOG.info(res.toString)
          resultDb.persist(res).map(Success.apply)
        case Failure(error) =>
          LOG.error("Error while processing:", error)
          Task.now(Failure(error))
      }
      .run
  }
}
