package com.example

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object TradeDeskServer extends App with TradeRoutes {

  println("Hello World")

  implicit val system: ActorSystem = ActorSystem("tradedesk")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val tradeDeskActor: ActorRef = system.actorOf(TradeDeskActor.props, "tradeDeskActor")

  lazy val routes = tradeRoutes

  Http().bindAndHandle(routes, "localhost", 8090)

  println(s"Server online at http://localhost:8090/")

  Await.result(system.whenTerminated, Duration.Inf)
}