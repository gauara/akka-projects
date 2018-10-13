package com.example


import akka.http.javadsl.model.RequestEntity
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.Await
import scala.util.Random
import scala.concurrent.duration._

trait ClientLogic {

  def newGameRequestAndResponse(): NewGameResponse = {
    val httpRequest = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:8080/tictactoe/newgame",
    )

    val httpResponse = Http().singleRequest(httpRequest)

    httpResponse.failed.map { e =>
      println(e.getMessage)
    }

    val r = httpResponse.flatMap { res =>
      res.status match {
        case OK =>
          Unmarshal(res.entity).to[NewGameResponse] // similar to ret.parseJson.convertTo[NewGameResponse]
        //Unmarshal(res.entity).to[String]
        // case _ => "" // THIS IS WEIRD BUT UNCommenting it will not be a compatible ret type
      }
    }
    val ret = Await.result(r, 3.seconds)
    //ret.parseJson.convertTo[NewGameResponse]
    ret
  }


  def matchRequestAndResponse(body: String): ServerResponse = {
    val httpRequest = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:8080/tictactoe/newgame",
      entity = HttpEntity(string = body).withoutSizeLimit() // take default contentType and data is passed as string
    )

    val httpResponse = Http().singleRequest(httpRequest)

    httpResponse.failed.map { e =>
      println(e.getMessage)
    }

    val ans = httpResponse.flatMap { res =>
      res.status match {
        case OK =>
          val r = Unmarshal(res.entity).to[String]
          r
      }
    }
    val a = Await.result(ans, 1.second)

    if (a.contains("failed")){
      a.asInstanceOf[FailedScenarioResponse]
    } else {
      a.asInstanceOf[SuccessScenarioResponse]
    }

  }

  def getMatches(count: Int, dict: Seq[String]): Seq[String] = {
    Random.shuffle(dict.take(count))
  }


}
