package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object TicTacToeServer extends App with TicTacToeApi {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()


  implicit lazy val ec = system.dispatcher

  println("Server starting: ....")

  Http().bindAndHandle(routes, "localhost", 8080)

  //system.terminate()

}
