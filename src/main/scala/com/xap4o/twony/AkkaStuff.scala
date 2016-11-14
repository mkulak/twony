package com.xap4o.twony

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object AkkaStuff {
  implicit val system = ActorSystem("main")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = scala.concurrent.ExecutionContext.global
}
