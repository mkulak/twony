package com.xap4o.twony.processing

import akka.actor.{ActorSystem, Cancellable, Scheduler}
import akka.stream.scaladsl.Source
import com.xap4o.twony.config.ProcessingConfig
import com.xap4o.twony.db.{AnalyzeResultDb, SearchKeywordsDb}
import com.xap4o.twony.twitter.TwitterClient
import com.xap4o.twony.utils.Async._
import com.xap4o.twony.utils.{StrictLogging, Timer}

import scala.concurrent.duration._
import scala.util.{Failure, Success}
import com.xap4o.twony.utils.AkkaSugar._

import scala.concurrent.Future

class PeriodicProcessing(
  job: AnalyzeJob,
  config: ProcessingConfig,
  resultDb: AnalyzeResultDb,
  keywordsDb: SearchKeywordsDb
) extends StrictLogging {

  def start(): Cancellable = {
    val task = new Runnable { override def run(): Unit = process()}
    implicitly[ActorSystem].scheduler.schedule(0.seconds, config.interval, task)
  }

  private def process(): Unit = {
    Source.fromFuture(keywordsDb.getAll()).
      .rightFlatMap { keywords =>
        keywords.map(k => job.process(k))
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
