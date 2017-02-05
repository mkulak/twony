package com.xap4o.twony.utils

import java.time.{Duration => JavaDuration}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import fs2.{Scheduler, Strategy, Stream, Task}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{FiniteDuration, Duration => ScalaDuration}
import Fs2Sugar._


object Async {
  implicit val system: ActorSystem = ActorSystem("main")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val strategy: Strategy = Strategy.fromExecutionContext(executionContext)
  implicit val scheduler: Scheduler = Scheduler.fromFixedDaemonPool(1)

  implicit def asFiniteDuration(d: JavaDuration): FiniteDuration = ScalaDuration.fromNanos(d.toNanos)

  def sequence[A](tasks: Seq[Task[A]]): Stream[Task, A] = tasks.map(Stream.eval).reduce(_ merge _)
}

