package com.xap4o.twony.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.xap4o.twony.db.SearchKeywordsDb
import com.xap4o.twony.twitter.TwitterModel._
import com.xap4o.twony.utils.StrictLogging
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global

class KeywordsServer(db: SearchKeywordsDb) extends StrictLogging {
  val route: Route =
    path("search_keywords") {
      get {
        complete(db.getAll().map(v => HttpResponse(entity = HttpEntity(v.toJson.compactPrint))))
      } ~
      post {
        parameters('keyword) { keyword =>
          complete(db.persist(keyword).map(v => HttpResponse(entity = HttpEntity("done"))))
        }
      } ~
      delete {
        parameters('keyword) { keyword =>
          complete(db.delete(keyword).map(v => HttpResponse(entity = HttpEntity("deleted"))))
        }
      }
    }
}

