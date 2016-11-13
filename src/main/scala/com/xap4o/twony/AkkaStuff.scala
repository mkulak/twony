package com.xap4o.twony

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object AkkaStuff {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
}
