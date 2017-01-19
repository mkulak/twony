package com.xap4o.twony.utils

import java.time.{Duration => JavaDuration}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import monix.execution.Scheduler

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{FiniteDuration, Duration => ScalaDuration}



object Async {
  implicit val system: ActorSystem = ActorSystem("main")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val scheduler: Scheduler = monix.execution.Scheduler.Implicits.global

  implicit def asFiniteDuration(d: JavaDuration): FiniteDuration = ScalaDuration.fromNanos(d.toNanos)
}

