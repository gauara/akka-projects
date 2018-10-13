package com.example

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport{
  import DefaultJsonProtocol._
  import Posse._

  final case class Message(msg: String)

  val msgJsonFormat = jsonFormat1(Message)

  implicit val cellJsonFormat = jsonFormat2(Cell)
  implicit val boardJsonFormat = jsonFormat1(Board)





}
