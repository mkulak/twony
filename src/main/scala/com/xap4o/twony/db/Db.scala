package com.xap4o.twony.db

import com.xap4o.twony.config.DbConfig
import com.xap4o.twony.processing.AnalyzeResult
import org.flywaydb.core.Flyway
import slick.driver.PostgresDriver.api._
import slick.jdbc.JdbcBackend.DatabaseDef

import scala.concurrent.{ExecutionContext, Future}

object Db {
  def init(config: DbConfig): Unit = {
    val flyway = new Flyway()
    flyway.setDataSource(config.url, config.user, config.password)
    flyway.migrate()
  }
}

class AnalyzeResultDb(db: DatabaseDef)(implicit ec: ExecutionContext) {
  def persist(r: AnalyzeResult): Future[Int] = {
    db.run(sqlu"""insert into analyze_result(total, positive, negative, errors, duration)
             values (${r.total}, ${r.positive}, ${r.negative}, ${r.errors}, ${r.duration})""")
  }
}

class SearchKeywordsDb(db: DatabaseDef)(implicit ec: ExecutionContext) {
  def getAll(): Future[Seq[String]] = {
    db.run(sql"""select value from search_keywords order by id""".as[String])
  }

  def persist(keyword: String): Future[Int] = {
    db.run(sqlu"""insert into search_keywords(value) values ($keyword)""")
  }

  def delete(keyword: String): Future[Int] = {
    db.run(sqlu"""delete from search_keywords where value=$keyword""")
  }
}

