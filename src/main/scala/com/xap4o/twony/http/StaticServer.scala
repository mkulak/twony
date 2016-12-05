package com.xap4o.twony.http

import akka.http.scaladsl.server.Directives._
import com.xap4o.twony.StrictLogging

object StaticServer extends StrictLogging {
  val route =
    pathPrefix("site") {
      encodeResponse {
        getFromResourceDirectory("webapp")
      }
    }
}

