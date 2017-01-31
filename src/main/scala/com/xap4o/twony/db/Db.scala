package com.xap4o.twony.db

import com.xap4o.twony.config.DbConfig
import com.xap4o.twony.processing.AnalyzeResult
import org.flywaydb.core.Flyway
import rx.lang.scala.Observable
import slick.driver.PostgresDriver.api._
import slick.jdbc.JdbcBackend.DatabaseDef

import scala.util.{Failure, Success, Try}
import com.xap4o.twony.utils.Async._

object Db {
  def init(config: DbConfig): Unit = {
    val flyway = new Flyway()
    flyway.setDataSource(config.url, config.user, config.password)
    flyway.migrate()
  }
}

class AnalyzeResultDb(db: DatabaseDef) {
  def persist(r: AnalyzeResult): Observable[Int] = {
    Observable.from(db.run(sqlu"""insert into analyze_result(total, positive, negative, errors, duration)
             values (${r.total}, ${r.positive}, ${r.negative}, ${r.errors}, ${r.duration})"""))
  }
}

class SearchKeywordsDb(db: DatabaseDef) {
  def getAll(): Observable[Try[String]] =
    Observable.from(db.run(sql"""select value from search_keywords order by id""".as[String]).materialize).flatMap {
      case Success(values) => Observable.from(values.map(Success(_)))
      case Failure(t) => Observable.just(Failure(t))
    }

  def persist(keyword: String): Observable[Try[Int]] =
    Observable.from(db.run(sqlu"""insert into search_keywords(value) values ($keyword)""").materialize)

  def delete(keyword: String): Observable[Try[Int]] =
    Observable.from(db.run(sqlu"""delete from search_keywords where value=$keyword""").materialize)
}

