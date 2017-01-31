package com.xap4o.twony.utils

import java.time.{Duration => JavaDuration}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import rx.lang.scala.Observable

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.{FiniteDuration, Duration => ScalaDuration}
import scala.util.{Failure, Success, Try}



object Async {
  implicit val system: ActorSystem = ActorSystem("main")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit def asFiniteDuration(d: JavaDuration): FiniteDuration = ScalaDuration.fromNanos(d.toNanos)

  def sequence[T](seq: Seq[Observable[T]]): Observable[Vector[T]] =
    Observable.from(seq).flatten.toIterator.map(_.toVector)

  implicit class TryFuture[T](f: Future[T]) {
    def materialize: Future[Try[T]] = f.map(Success(_)).recover{ case x => Failure(x) }
  }

  implicit class ObservableTryOps[A](t: Observable[Try[A]]) {
    def rightMap[B](f: A => B): Observable[Try[B]] = t.map(_ map f)

    def rightFlatMap[B](f: A => Try[B]): Observable[Try[B]] = t.map(_ flatMap f)

    def rightFlatMapAsync[B](f: A => Observable[Try[B]]): Observable[Try[B]] = t flatMap {
      case Success(a) => f(a)
      case Failure(e) => Observable.just(Failure(e))
    }
  }

  def lift[A](a: A): Observable[Try[A]] = Observable.just(Success(a))
}

