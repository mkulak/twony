package com.xap4o.twony.utils

import java.time.{Duration => JavaDuration}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{FiniteDuration, Duration => ScalaDuration}
import MonixSugar._


object Async {
  implicit val system: ActorSystem = ActorSystem("main")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val scheduler: Scheduler = monix.execution.Scheduler.Implicits.global

  implicit def asFiniteDuration(d: JavaDuration): FiniteDuration = ScalaDuration.fromNanos(d.toNanos)

  def sequence[A](tasks: Seq[Task[A]]): Observable[A] = Observable.merge(tasks.map(_.toObservable.fork): _*)
}

