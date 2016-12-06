package com.xap4o.twony.processing

import akka.actor.{ActorSystem, Cancellable}
import com.xap4o.twony.config.AppConfig
import com.xap4o.twony.db.{AnalyzeResultDb, SearchKeywordsDb}
import com.xap4o.twony.twitter.TwitterClient
import com.xap4o.twony.utils.Async._
import com.xap4o.twony.utils.{StrictLogging, Timer}

import scala.concurrent.Future
import scala.concurrent.duration._

class PeriodicProcessing(
  config: AppConfig,
  twitterClient: TwitterClient,
  analyzerClient: AnalyzerClient,
  resultDb: AnalyzeResultDb,
  keywordsDb: SearchKeywordsDb
) extends StrictLogging {

  def start(): Cancellable = {
    val task = new Runnable { override def run(): Unit = process()}
    implicitly[ActorSystem].scheduler.schedule(0.seconds, config.processing.interval, task)
  }

  def process(): Unit = {
    val job = new AnalyzeJob(twitterClient, analyzerClient, Timer.system)
    val resultsFuture = keywordsDb.getAll().flatMap { keywords =>
      Future.sequence(keywords.map(job.process))
    }
    resultsFuture.foreach { results =>
      results.foreach(resultDb.persist)
      LOG.info(results.mkString("\n"))
    }
  }
}
