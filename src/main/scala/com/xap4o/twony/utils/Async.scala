package com.xap4o.twony.utils

import java.time.{Duration => JavaDuration}

import akka.actor.{ActorSystem, Scheduler}
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.{FiniteDuration, Duration => ScalaDuration}
import AkkaSugar._

import scala.util.{Failure, Success, Try}


object Async {
  implicit val system: ActorSystem = ActorSystem("main")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit def asFiniteDuration(d: JavaDuration): FiniteDuration = ScalaDuration.fromNanos(d.toNanos)
}

