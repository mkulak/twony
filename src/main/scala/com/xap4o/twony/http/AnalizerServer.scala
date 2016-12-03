package com.xap4o.twony.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.xap4o.twony.StrictLogging
import com.xap4o.twony.model.TwitterModel._

object AnalizerServer extends StrictLogging {
  val route =
    pathPrefix("akka") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    } ~
    path("analyze") {
      post {
        entity(as[Tweet]) { tweet =>
          complete(HttpEntity(ContentTypes.`application/json`, (tweet.text.length % 2 == 0).toString))
        }
      }
//      get {
//        parameters('text, 'foo ?) { (text, foo) =>
//          complete(s"text=$text foo=$foo")
//        }
//      }
//      get {
//        parameters(('text.as[String], 'color ?, 'dangerous ? "no")).as[Tweet] { orderItem =>
//          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
//        }
//      }
      // ~
    }

}

