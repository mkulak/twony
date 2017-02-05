package com.xap4o.twony.utils

import fs2.Task
import fs2.Stream

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import Async._

import scala.concurrent.duration.FiniteDuration

object Fs2Sugar {
  implicit class TaskOps[A](t: Task[A]) {
    def toStream: Stream[Task, A] = Stream.eval(t)

    def scheduleWithFixedDelay(delay: FiniteDuration): Stream[Task, A] =
      Stream.eval(t) ++ Stream.repeatEval(t.schedule(delay))
  }

  implicit class TaskTryOps[A](t: Task[Try[A]]) {
    def rightMap[B](f: A => B): Task[Try[B]] = t.map(_ map f)

    def rightFlatMap[B](f: A => Task[Try[B]]): Task[Try[B]] = t flatMap {
      case Success(a) => f(a)
      case Failure(e) => Task.now(Failure(e))
    }
  }

  implicit class StreamTryOps[A](t: Stream[Task, Try[A]]) {
    def rightMap[B](f: A => B): Stream[Task, Try[B]] = t.map(_ map f)

    def rightFlatMap[B](f: A => Stream[Task, Try[B]]): Stream[Task, Try[B]] = t flatMap {
      case Success(a) => f(a)
      case Failure(e) => Stream(Failure(e))
    }
  }

  implicit class FutureOps[A](f: Future[A]) {
    def toTask: Task[Try[A]] = Task.fromFuture(f).attempt.map(_.fold(Failure.apply, Success.apply))
  }
}
