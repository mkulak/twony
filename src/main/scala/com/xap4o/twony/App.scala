package com.xap4o.twony

import java.net.URLEncoder
import java.util.Base64

import akka.http.scaladsl.Http.ServerBinding
import com.xap4o.twony.AkkaStuff._

import scala.concurrent.Future
import scala.io.StdIn

object App extends StrictLogging {
  val twitterKey = "PJbMe73pgnT4P6s7KqqALZwJk"
  val twitterSecret = "bad1X1hUb9vzvNvwoPqrkmIkqW6qSOaKoNbcgjqN6lyXHkyRyr"

  def main(args: Array[String]): Unit = {
    val server = new AnalizerServer()
    val future: Future[ServerBinding] = server.start()
    future.map { f =>
      LOG.info("Press Enter to terminate")
      StdIn.readLine()
      f
    }.flatMap(_.unbind()).onComplete(_ => system.terminate())

    //    val encodedToken = (twitterKey.urlEncode() + ":" + twitterSecret.urlEncode()).base64Encode()
//    val wsClient = AhcWSClient()
//    val tokenFuture = wsClient
//      .url("https://api.twitter.com/oauth2/token")
//      .withHeaders("Content-Type" -> "application/x-www-form-urlencoded;charset=UTF-8")
//      .withAuth(twitterKey, twitterSecret, BASIC)
//      .post("grant_type=client_credentials")
//      .map { wsResponse =>
//        println(s"Received: ${wsResponse.status} ${wsResponse.body}")
//        Xor.catchNonFatal(wsResponse.json).leftMap(t => TwitterAuthError(t.toString)).map {
//          case obj: JsObject => Right(obj.value("access_token"))
//          case _ => Left(TwitterAuthError(wsResponse.body))
//        }
//      }
//    val token = Await.result(tokenFuture, Duration.Inf)
//    println(token)
//    wsClient.close()
  }

  sealed trait Error

  case class TwitterAuthError(message: String) extends Error

  implicit class EncodableString(val s: String) extends AnyVal {
    def urlEncode() = URLEncoder.encode(s, "UTF-8")
    def base64Encode() = new String(Base64.getEncoder.encode(s.getBytes("UTF-8")), "UTF-8")
  }
}

