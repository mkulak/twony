package com.xap4o.twony.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.xap4o.twony.db.SearchKeywordsDb
import com.xap4o.twony.twitter.TwitterModel._
import com.xap4o.twony.utils.StrictLogging
import spray.json._
import com.xap4o.twony.utils.Async._

import scala.util.Success

class KeywordsServer(db: SearchKeywordsDb) extends StrictLogging {
  val route: Route =
    path("search_keywords") {
      get {
        complete(db.getAll().toList.map(_.collect { case Success(v) => v })
          .map(v => HttpResponse(entity = HttpEntity(v.toJson.compactPrint))).toBlocking.toFuture)
      } ~
      post {
        parameters('keyword) { keyword =>
          complete(db.persist(keyword).map(v => HttpResponse(entity = HttpEntity("done"))).toBlocking.toFuture)
        }
      } ~
      delete {
        parameters('keyword) { keyword =>
          complete(db.delete(keyword).map(v => HttpResponse(entity = HttpEntity("deleted"))).toBlocking.toFuture)
        }
      }
    }
}

