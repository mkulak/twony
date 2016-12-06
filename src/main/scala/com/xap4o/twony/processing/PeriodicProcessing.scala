package com.xap4o.twony.processing

import akka.actor.{ActorSystem, Cancellable}
import com.xap4o.twony.config.AppConfig
import com.xap4o.twony.db.{AnalyzeResultDb, SearchKeywordsDb}
import com.xap4o.twony.twitter.TwitterClient
import com.xap4o.twony.utils.Async._
import com.xap4o.twony.utils.StrictLogging

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
    val job = new AnalyzeJob(twitterClient, analyzerClient)
    val res = keywordsDb.getAll().flatMap(keywords => Future.sequence(keywords.map(keyword => job.process(keyword))))
    res.foreach { results =>
      results.foreach { result =>
        LOG.info(result.toString)
        resultDb.persist(result)
      }
    }
  }
}
