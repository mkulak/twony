package com.xap4o.twony.utils

import java.time.{Duration => JavaDuration}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.duration.{FiniteDuration, Duration => ScalaDuration}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}



object Async {
  implicit val system: ActorSystem = ActorSystem("main")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit class TryFuture[T](f: Future[T]) {
    def toTry: Future[Try[T]] = f.map(Success(_)).recover{ case x => Failure(x) }
  }

  implicit def asFiniteDuration(d: JavaDuration): FiniteDuration = ScalaDuration.fromNanos(d.toNanos)
}

