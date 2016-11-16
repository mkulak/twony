package com.xap4o.twony.model

import java.net.URLEncoder
import java.util.Base64

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json
import spray.json.{JsObject, JsString, JsValue, RootJsonFormat, _}


object TwitterModel extends SprayJsonSupport with json.DefaultJsonProtocol {
  sealed trait Error

  case class AuthError(message: String, t: Throwable) extends Error

  case class AuthResponse(tokenType: String, accessToken: String)
  case class SearchResponse(statuses: Seq[Tweet], metadata: SearchMetadata)
  case class SearchMetadata(count: Int, query: String)
  case class Tweet(text: String, username: String)

  //  implicit val responseFormat = jsonFormat2(AuthResponse)

  implicit object AuthResponseJsonFormat extends RootJsonFormat[AuthResponse] {
    def write(r: AuthResponse) =
      JsObject("token_type" -> JsString(r.tokenType), "access_token" -> JsString(r.accessToken))

    def read(value: JsValue) = value match {
      case JsObject(values) =>
        AuthResponse(
          values("token_type").asInstanceOf[JsString].value,
          values("access_token").asInstanceOf[JsString].value
        )
      case _ => deserializationError(s"bad data $value")
    }
  }
  implicit object TweetJsonFormat extends RootJsonFormat[Tweet] {
    def write(t: Tweet) =
      JsObject("text" -> JsString(t.text), "user" -> JsObject("name" -> JsString(t.username)))

    def read(value: JsValue) = value match {
      case JsObject(values) =>
        Tweet(
          values("text").asInstanceOf[JsString].value,
          values("user").asJsObject.fields("name").asInstanceOf[JsString].value
        )
      case _ => deserializationError(s"bad data $value")
    }
  }

  implicit object SearchMetadataJsonFormat extends RootJsonFormat[SearchMetadata] {
    def write(t: SearchMetadata) =
      JsObject("count" -> JsNumber(t.count), "query" -> JsString(t.query))

    def read(value: JsValue) = value match {
      case JsObject(values) =>
        SearchMetadata(
          values("count").asInstanceOf[JsNumber].value.intValue(),
          values("query").asInstanceOf[JsString].value
        )
      case _ => deserializationError(s"bad data $value")
    }
  }


  implicit object SearchResponseJsonFormat extends RootJsonFormat[SearchResponse] {
    def write(t: SearchResponse) =
      JsObject("statuses" -> JsArray(t.statuses.map(_.toJson).toVector), "metadata" -> t.metadata.toJson)

    def read(value: JsValue) = value match {
      case JsObject(values) =>
        SearchResponse(
          values("statuses").asInstanceOf[JsArray].elements.map(_.convertTo[Tweet]),
          values("search_metadata").convertTo[SearchMetadata]
        )
      case _ => deserializationError(s"bad data $value")
    }
  }




  implicit class EncodableString(val s: String) extends AnyVal {
    def urlEncode() = URLEncoder.encode(s, "UTF-8")
    def base64Encode() = new String(Base64.getEncoder.encode(s.getBytes("UTF-8")), "UTF-8")
  }

  private def pt[A](a: A): A = {
    println(a)
    a
  }
}

