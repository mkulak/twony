package com.xap4o.twony

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.xap4o.twony.config.{AppConfig, HttpConfig}
import com.xap4o.twony.db.{AnalyzeResultDb, Db, SearchKeywordsDb}
import com.xap4o.twony.http.{AnalizerServer, HttpUtils, KeywordsServer}
import com.xap4o.twony.processing.{AnalyzerClientImpl, PeriodicProcessing}
import com.xap4o.twony.twitter.TwitterClientImpl
import com.xap4o.twony.utils.Async._
import com.xap4o.twony.utils.StrictLogging
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
    val twitterClient = new TwitterClientImpl(config.processing)
    val analyzerClient = new AnalyzerClientImpl(config.processing)

    new PeriodicProcessing(config.processing, twitterClient, analyzerClient, resultsDb, keywordsDb).start()

    val keywordsServer = new KeywordsServer(keywordsDb)
    startServerAndBlock(config.http, AnalizerServer.route ~ keywordsServer.route)
  }

  def startServerAndBlock(config: HttpConfig, route: Route): Unit = {
    val future: Future[ServerBinding] = HttpUtils.startServer(config, route)
    LOG.info("Press Enter to terminate")
    StdIn.readLine()
    future.flatMap(_.unbind()).onComplete(_ => implicitly[ActorSystem].terminate())
  }
}

