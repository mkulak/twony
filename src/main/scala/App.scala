import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.Base64

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.data.Xor
import play.api.libs.json.JsObject
import play.api.libs.ws.WSAuthScheme.BASIC
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val twitterKey = "PJbMe73pgnT4P6s7KqqALZwJk"
  val twitterSecret = "bad1X1hUb9vzvNvwoPqrkmIkqW6qSOaKoNbcgjqN6lyXHkyRyr"

  def main(args: Array[String]): Unit = {
    println("hello")
    val encodedToken = (twitterKey.urlEncode() + ":" + twitterSecret.urlEncode()).base64Encode()
    val wsClient = AhcWSClient()
    val tokenFuture = wsClient
      .url("https://api.twitter.com/oauth2/token")
      .withHeaders("Content-Type" -> "application/x-www-form-urlencoded;charset=UTF-8")
      .withAuth(twitterKey, twitterSecret, BASIC)
      .post("grant_type=client_credentials")
      .map { wsResponse =>
        println(s"Received: ${wsResponse.status} ${wsResponse.body}")
        Xor.catchNonFatal(wsResponse.json).leftMap(t => TwitterAuthError(t.toString)).map {
          case obj: JsObject => Xor.Right(obj.value("access_token"))
          case _ => Xor.Left(TwitterAuthError(wsResponse.body))
        }
      }
    val token = Await.result(tokenFuture, Duration.Inf)
    println(token)
    wsClient.close()
  }

  sealed trait Error

  case class TwitterAuthError(message: String) extends Error

  implicit class EncodableString(val s: String) extends AnyVal {
    def urlEncode() = URLEncoder.encode(s, "UTF-8")
    def base64Encode() = new String(Base64.getEncoder.encode(s.getBytes("UTF-8")), "UTF-8")
  }
}

//AAAAAAAAAAAAAAAAAAAAABgwxwAAAAAAubrPCusIihZN3aTQyoCdKOfq%2Bss%3DTWSmSuCldZT2fH3XMkDN91Z5ZhvVsAC3IIInYYfqZgsYqIvqQH

//AAAAAAAAAAAAAAAAAAAAABgwxwAAAAAAubrPCusIihZN3aTQyoCdKOfq%2Bss%3DTWSmSuCldZT2fH3XMkDN91Z5ZhvVsAC3IIInYYfqZgsYqIvqQH

