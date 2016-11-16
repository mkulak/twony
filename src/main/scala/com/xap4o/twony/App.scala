package com.xap4o.twony

import akka.http.scaladsl.Http.ServerBinding
import com.xap4o.twony.AkkaStuff._

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.{Failure, Success}

object App extends StrictLogging {
  def main(args: Array[String]): Unit = {
    val config = AppConfig.load()

    val client = new TwitterClient(config)
    client
      .open()
      .flatMap(t => client.getTweets(t, "trump"))
      .onComplete {
        case Success(response) =>
          LOG.info(s"received: ${response.statuses.size}")
          LOG.info(s"${response.statuses.map(t => "@" + t.username + ": " + t.text).mkString("\n")}")
        case Failure(t) => LOG.error("Error while obtaining auth:", t)
      }

    val server = new AnalizerServer(config)
    val future: Future[ServerBinding] = server.start()
    LOG.info("Press Enter to terminate")
    StdIn.readLine()
    future.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }
}

