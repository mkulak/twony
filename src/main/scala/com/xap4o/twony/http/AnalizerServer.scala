package com.xap4o.twony.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.xap4o.twony.StrictLogging
import com.xap4o.twony.model.TwitterModel._

object AnalizerServer extends StrictLogging {
  val route =
    path("analyze") {
      post {
        entity(as[Tweet]) { tweet =>
          complete(HttpEntity(ContentTypes.`application/json`, (tweet.text.length % 2 == 0).toString))
        }
      }
    }
}

