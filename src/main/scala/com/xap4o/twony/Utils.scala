package com.xap4o.twony

import akka.actor.ActorSystem
import akka.stream.Materializer

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class Timer {
  val start: Long = now()

  def now(): Long = System.currentTimeMillis()

  def duration(): Long = now() - start
}

object Utils {
  implicit class TryFuture[T](f: Future[T])(implicit ec: ExecutionContext, as: ActorSystem, m: Materializer) {
    def toTry: Future[Try[T]] = f.map(Success(_)).recover{ case x => Failure(x) }
  }
}

