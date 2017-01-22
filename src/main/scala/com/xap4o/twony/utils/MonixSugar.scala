package com.xap4o.twony.utils

import monix.eval.Task

import scala.util.{Failure, Success, Try}

object MonixSugar {
  implicit class TaskTryOps[A](t: Task[Try[A]]) {
    def mapT[B](f: A => B): Task[Try[B]] = t.map(_ map f)

    def flatMapT[B](f: A => Try[B]): Task[Try[B]] = t.map(_ flatMap f)

    def flatMapTAsync[B](f: A => Task[Try[B]]): Task[Try[B]] = t flatMap {
      case Success(a) => f(a)
      case Failure(e) => Task.now(Failure(e))
    }
  }
}
