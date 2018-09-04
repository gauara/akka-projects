package com.example

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import com.example.TradeDeskActor.ActionPerformed

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat2(User)
  implicit val tradeJsonFormat = jsonFormat7(Trade)
  implicit val tradesJsonFormat = jsonFormat1(Trades)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}