package com.example

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol with Requests {

  implicit val newGameResponseFormat = jsonFormat4(NewGameResponse)
  implicit val failedScenarioResponseFormat = jsonFormat4(FailedScenarioResponse)
  implicit val successScenarioResponseFormat = jsonFormat4(SuccessScenarioResponse)

  implicit lazy val matchRequest = jsonFormat2(MatchRequest)

}
