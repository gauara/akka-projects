package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}

import scala.concurrent.{Await, Future}
import akka.http.scaladsl.model.StatusCodes._
import akka.stream.scaladsl.Source
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer

import scala.concurrent.duration._
import spray.json._

import scala.util.{Failure, Random}

object TicTacToeClient extends App with JsonSupport with ClientLogic {


  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  var loop = true
  while (loop) {
    val resp = newGameRequestAndResponse
    val dict = resp.dict
    val words = resp.words

    // create match request
    val mr = MatchRequest(
      resp.gameId,
      matches = getMatches(words.size, dict)
    )



    loop = false

  }

  system.terminate()


}
