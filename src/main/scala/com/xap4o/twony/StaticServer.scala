package com.xap4o.twony

import akka.http.scaladsl.server.Directives._

object StaticServer extends StrictLogging {
  val route =
//    pathSingleSlash {
    pathPrefix("site") {
      encodeResponse {
        getFromResourceDirectory("webapp")
      }
    }
}

