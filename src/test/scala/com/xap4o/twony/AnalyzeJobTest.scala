package com.xap4o.twony
import com.xap4o.twony.processing.{AnalyzeJob, AnalyzeResult, AnalyzerClient}
import com.xap4o.twony.twitter.TwitterModel.{SearchMetadata, SearchResponse, Tweet}
import com.xap4o.twony.twitter.{Token, TwitterClient}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Success, Try}

class AnalyzeJobTest extends org.scalatest.FunSuite {
  test("Analyze job produces AnalyzeResult") {
    val keyword = "test_keyword"
    val tweets = Seq(Tweet("text1", "user1"), Tweet("text2", "user2"))
    val searchResponse = SearchResponse(tweets, SearchMetadata(tweets.size, keyword))
    val tc = new TestTwitterClient(searchResponse)
    val ac = new TestAnalyzerClient(_.text.drop(4) == "1")
    val job = new AnalyzeJob(tc, ac)
    val result = Await.result(job.process(keyword), 10 seconds).copy(duration = 0)
    assertResult(AnalyzeResult(keyword, 2, 1, 1, 0, 0))(result)
  }
}

class TestTwitterClient(response: SearchResponse) extends TwitterClient {
  override def open(): Future[Token] = Future.successful(Token("test_token"))

  override def search(token: Token, keyword: String): Future[SearchResponse] = Future.successful(response)
}


class TestAnalyzerClient(f: Tweet => Boolean) extends AnalyzerClient {
  def analyze(tweet: Tweet): Future[Try[Boolean]] = {
    Future.successful(Success(f(tweet)))
  }
}