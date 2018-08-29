package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.PathDirectives.path

import scala.concurrent.duration._
import akka.stream.ActorMaterializer
import spray.json.DefaultJsonProtocol

import scala.concurrent.{Await, Future}
import scala.util.Random

object PosseServerApp extends App  with JsonSupport {

  import DefaultJsonProtocol._

  implicit val system = ActorSystem("PosseServer")

  implicit val materializer = ActorMaterializer()

  val tokens = Array(
    "tic",
    "toe",
    "foo",
    "bar",
    "hello",
    "motto",
    "mickey",
    "mouse"
  )

  lazy val routes: Route =
    concat(
      path("" | "/") {
        get {
          val r = tokens(Random.nextInt(tokens.size-1))
          complete(StatusCodes.OK, r) // one signature type
        }
      },
      path("try") {
        get {
          val r = tokens(Random.nextInt(tokens.size-1))

          implicit val ec = system.dispatcher
          val entity: Future[Message] = Future.apply(Message(r))

          onSuccess(entity) { e =>
            complete(StatusCodes.OK, e.msg) // Not Another signature but another way using Future
          }
        }
      }
    )

  Http().bindAndHandle(routes, "localhost", 1111)
  println("Server is online at localhost and port 1111")
  Await.result(system.whenTerminated, Duration.Inf)
}
