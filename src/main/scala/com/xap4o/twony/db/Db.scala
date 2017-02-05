package com.xap4o.twony.db

import com.xap4o.twony.config.DbConfig
import com.xap4o.twony.processing.AnalyzeResult
import fs2.Task
import org.flywaydb.core.Flyway
import slick.driver.PostgresDriver.api._
import slick.jdbc.JdbcBackend.DatabaseDef

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}
import com.xap4o.twony.utils.Fs2Sugar._
import com.xap4o.twony.utils.Async._

object Db {
  def init(config: DbConfig): Unit = {
    val flyway = new Flyway()
    flyway.setDataSource(config.url, config.user, config.password)
    flyway.migrate()
  }
}

class AnalyzeResultDb(db: DatabaseDef) {
  def persist(r: AnalyzeResult): Task[Int] =
    Task.fromFuture(db.run(sqlu"""insert into analyze_result(total, positive, negative, errors, duration)
           values (${r.total}, ${r.positive}, ${r.negative}, ${r.errors}, ${r.duration})"""))
}

class SearchKeywordsDb(db: DatabaseDef) {
  def getAll(): Task[Try[Seq[String]]] =
    db.run(sql"""select value from search_keyword order by id""".as[String]).toTask

  def persist(keyword: String): Task[Try[Int]] =
    db.run(sqlu"""insert into search_keyword(value) values ($keyword)""").toTask

  def delete(keyword: String): Task[Try[Int]] =
    db.run(sqlu"""delete from search_keyword where value=$keyword""").toTask
}

