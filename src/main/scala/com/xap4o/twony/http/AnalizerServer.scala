package com.xap4o.twony.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.xap4o.twony.twitter.TwitterModel._
import com.xap4o.twony.utils.StrictLogging

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

