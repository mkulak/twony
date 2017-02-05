package com.xap4o.twony.utils

import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object MonixSugar {
  implicit class TaskOps[A](t: Task[A]) {
    def toObservable: Observable[A] = Observable.fromTask(t)
  }

  implicit class TaskTryOps[A](t: Task[Try[A]]) {
    def rightMap[B](f: A => B): Task[Try[B]] = t.map(_ map f)

    def rightFlatMap[B](f: A => Task[Try[B]]): Task[Try[B]] = t flatMap {
      case Success(a) => f(a)
      case Failure(e) => Task.now(Failure(e))
    }
  }

  implicit class ObservableOps[A](t: Observable[A]) {
    def fork(implicit s: Scheduler): Observable[A] = Observable.fork(t)
  }

  implicit class ObservableTryOps[A](t: Observable[Try[A]]) {
    def rightMap[B](f: A => B): Observable[Try[B]] = t.map(_ map f)

    def rightFlatMap[B](f: A => Observable[Try[B]]): Observable[Try[B]] = t flatMap {
      case Success(a) => f(a)
      case Failure(e) => Observable(Failure(e))
    }
  }
  
  implicit class FutureOps[A](f: Future[A]) {
    def toTask: Task[A] = Task.fromFuture(f)
  }
}
