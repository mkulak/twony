package com.xap4o.twony.utils

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import Async._
import akka.stream.scaladsl.Source

import scala.concurrent.duration.FiniteDuration

object AkkaSugar {

  implicit class SourceTryOps[A, M](t: Source[Try[A], M]) {
    def rightMap[B](f: A => B): Source[Try[B], M] = t.map(_ map f)

    def rightFlatMap[B](f: A => Source[Try[B], M]): Source[Try[B], M] = t.flatMapConcat {
      case Success(a) => f(a)
      case Failure(e) => Source.failed(e)
    }
  }

  implicit class FutureOps[A](f: Future[A]) {
    def materialize: Future[Try[A]] = f map Try.apply recover { case t => Failure(t) }
  }

  implicit class FutureTryOps[A](f: Future[Try[A]]) {
    def rightMap[B](g: A => B): Future[Try[B]] = f.map(_ map g)

    def rightFlatMap[B](g: A => Future[Try[B]]): Future[Try[B]] = f.flatMap {
      case Success(a) => g(a)
      case Failure(e) => Future.successful(Failure(e))
    }
  }
}
