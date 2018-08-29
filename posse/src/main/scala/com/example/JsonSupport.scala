package com.example

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport{
  import DefaultJsonProtocol._
  final case class Message(msg: String)

  val msgJsonFormat = jsonFormat1(Message)
}
