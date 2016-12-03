package com.xap4o.twony

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.Materializer
import com.xap4o.twony.db.{AnalyzeResultDb, SearchKeywordsDb}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class PeriodicProcessing(config: AppConfig, resultDb: AnalyzeResultDb, keywordsDb: SearchKeywordsDb)(
  implicit ec: ExecutionContext, as: ActorSystem, m: Materializer) extends StrictLogging {

  def start(): Cancellable = {
    implicit val executor = as.dispatcher
    val task = new Runnable { override def run(): Unit = process()}
    as.scheduler.schedule(0.seconds, config.processing.interval, task)
  }

  def process(): Unit = {
    val job = new AnalyzeJob(config)
    val res = keywordsDb.getAll().flatMap(keywords => Future.sequence(keywords.map(keyword => job.process(keyword))))
    res.foreach { results =>
      results.foreach { result =>
        LOG.info(s"Query: '${result.query}' - ${result.total} results. Positive ${result.positive}, " +
          s"negative: ${result.negative}, fails: ${result.errors}")
        resultDb.persist(result)
      }
    }
  }
}
