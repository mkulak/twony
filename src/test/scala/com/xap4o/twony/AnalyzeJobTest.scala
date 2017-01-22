package com.xap4o.twony
import com.xap4o.twony.processing.{AnalyzeJob, AnalyzeResult, AnalyzerClient}
import com.xap4o.twony.twitter.TwitterModel.{SearchMetadata, SearchResponse, Tweet}
import com.xap4o.twony.twitter.{Token, TwitterClient}
import com.xap4o.twony.utils.Async._
import com.xap4o.twony.utils.Timer.CreateTimer
import monix.eval.Task
import org.scalatest.FunSuite

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Success, Try}

class AnalyzeJobTest extends FunSuite {
  test("Analyze job produces AnalyzeResult") {
    val keyword = "test_keyword"
    val tweets = Seq(Tweet("text1", "user1"), Tweet("text2", "user2"))
    val searchResponse = SearchResponse(tweets, SearchMetadata(tweets.size, keyword))
    val twitterClient = new TestTwitterClient(searchResponse)
    val analyzerClient = new TestAnalyzerClient(_.text == "text1")
    val job = new AnalyzeJob(twitterClient, analyzerClient, MockTimer.zero)
    val result = Await.result(job.process(keyword).runAsync, 10 seconds)
    assertResult(Success(AnalyzeResult(keyword, 2, 1, 1, 0, 0)))(result)
  }
}

class TestTwitterClient(response: SearchResponse) extends TwitterClient {
  override def open(): Task[Try[Token]] = Task.now(Success(Token("test_token")))

  override def search(token: Token, keyword: String): Task[Try[SearchResponse]] = Task.now(Success(response))
}


class TestAnalyzerClient(f: Tweet => Boolean) extends AnalyzerClient {
  def analyze(tweet: Tweet): Task[Try[Boolean]] = Task.now(Success(f(tweet)))
}

object MockTimer {
  val zero: CreateTimer = () => () => 0
}