package com.xap4o.twony

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.Materializer
import com.xap4o.twony.db.AnalyzeResultDb

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class PeriodicProcessing(config: AppConfig, resultDb: AnalyzeResultDb)(
  implicit ec: ExecutionContext, as: ActorSystem, m: Materializer) extends StrictLogging {

  def start(): Cancellable = {
    implicit val executor = as.dispatcher
    val task = new Runnable { override def run(): Unit = process()}
    as.scheduler.schedule(0.seconds, config.processing.interval, task)
  }

  def process(): Unit = {
    val job = new AnalyzeJob(config)
    val res = job.process("trump")
    res.foreach { result =>
      LOG.info(s"received: ${result.total} results. Positive ${result.positive}, " +
        s"negative: ${result.negative}, fails: ${result.errors}")
      resultDb.persist(result)
    }
  }
}
