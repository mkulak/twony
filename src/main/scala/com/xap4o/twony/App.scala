package com.xap4o.twony

import akka.http.scaladsl.Http.ServerBinding
import com.xap4o.twony.AkkaStuff._

import scala.concurrent.Future
import scala.io.StdIn

object App extends StrictLogging {

  def main(args: Array[String]): Unit = {
    val config = AppConfig.load()

    val client = new TwitterClient(config)
    client.open().map { token =>
      println(token)
    }

    val server = new AnalizerServer(config)
    val future: Future[ServerBinding] = server.start()
    future.flatMap(_.unbind()).onComplete(_ => system.terminate())
    LOG.info("Press Enter to terminate")
    StdIn.readLine()
  }
}

