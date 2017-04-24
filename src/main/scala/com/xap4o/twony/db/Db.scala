package com.xap4o.twony.db

import com.xap4o.twony.config.DbConfig
import com.xap4o.twony.processing.AnalyzeResult
import org.flywaydb.core.Flyway
import slick.driver.PostgresDriver.api._
import slick.jdbc.JdbcBackend.DatabaseDef

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import com.xap4o.twony.utils.AkkaSugar._
import com.xap4o.twony.utils.Async._

object Db {
  def init(config: DbConfig): Unit = {
    val flyway = new Flyway()
    flyway.setDataSource(config.url, config.user, config.password)
    flyway.migrate()
  }
}

class AnalyzeResultDb(db: DatabaseDef) {
  def persist(r: AnalyzeResult): Future[Try[Int]] =
    db.run(sqlu"""insert into analyze_result(total, positive, negative, errors, duration)
           values (${r.total}, ${r.positive}, ${r.negative}, ${r.errors}, ${r.duration})""").materialize
}

class SearchKeywordsDb(db: DatabaseDef) {
  def getAll(): Future[Try[Seq[String]]] =
    db.run(sql"""select value from search_keyword order by id""".as[String]).materialize

  def persist(keyword: String): Future[Try[Int]] =
    db.run(sqlu"""insert into search_keyword(value) values ($keyword)""").materialize

  def delete(keyword: String): Future[Try[Int]] =
    db.run(sqlu"""delete from search_keyword where value=$keyword""").materialize
}

