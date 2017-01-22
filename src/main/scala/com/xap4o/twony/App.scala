package com.xap4o.twony

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.xap4o.twony.config.{AppConfig, HttpConfig}
import com.xap4o.twony.db.{AnalyzeResultDb, Db, SearchKeywordsDb}
import com.xap4o.twony.http.{AnalizerServer, HttpClientImpl, KeywordsServer}
import com.xap4o.twony.processing.{AnalyzeJob, AnalyzerClientImpl, PeriodicProcessing}
import com.xap4o.twony.twitter.TwitterClientImpl
import com.xap4o.twony.utils.Async._
import com.xap4o.twony.utils.{StrictLogging, Timer}
import slick.jdbc.JdbcBackend._
import spray.json.BasicFormats

import scala.concurrent.Future
import scala.io.StdIn

object App extends StrictLogging with BasicFormats {
  def main(args: Array[String]): Unit = {
    val config = AppConfig.load()
    Db.init(config.db)

    val db = Database.forConfig(path = "", config.db.fullConfig)
    val resultsDb = new AnalyzeResultDb(db)
    val keywordsDb = new SearchKeywordsDb(db)
    val httpClient = new HttpClientImpl()
    val twitterClient = new TwitterClientImpl(config.processing, httpClient)
    val analyzerClient = new AnalyzerClientImpl(config.processing, httpClient)

    val job = new AnalyzeJob(twitterClient, analyzerClient, Timer.system)

    new PeriodicProcessing(job, config.processing, resultsDb, keywordsDb).start()

    val keywordsServer = new KeywordsServer(keywordsDb)
    startServerAndBlock(config.http, AnalizerServer.route ~ keywordsServer.route)
  }

  private def startServerAndBlock(config: HttpConfig, route: Route): Unit = {
    val future: Future[ServerBinding] = Http().bindAndHandle(route, config.host, config.port)
    future.foreach {s => LOG.info(s"Server started at http://${config.host}:${config.port}") }
    LOG.info("Press Enter to terminate")
    StdIn.readLine()
    future.flatMap(_.unbind()).onComplete(_ => implicitly[ActorSystem].terminate())
  }
}

