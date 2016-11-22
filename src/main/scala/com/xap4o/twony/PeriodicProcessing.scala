package com.xap4o.twony

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.Materializer

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class PeriodicProcessing(config: AppConfig)(
  implicit ec: ExecutionContext, as: ActorSystem, m: Materializer) {

  def start(): Cancellable = {
    val scheduler = as.scheduler
    implicit val executor = as.dispatcher

    val task = new Runnable {
      override def run(): Unit = {
        val job = new AnalyzeJob(config)
        job.process("trump")
      }
    }
    scheduler.schedule(0.seconds, config.processing.interval, task)
  }
}
