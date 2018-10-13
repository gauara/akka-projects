package com.example

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.JavaFlowSupport.Source
import akka.stream.scaladsl.{FileIO, Keep}
import com.example.Posse.{Board, Cell}

import scala.concurrent.{Await, Future}
import scala.util.{Random, Success}
import spray.json._
import akka.util._

import DefaultJsonProtocol._

import spray.json.DefaultJsonProtocol._


object PosseClientApp extends App with JsonSupport {

  implicit val system = ActorSystem("PosseServer")

  implicit val materializer = ActorMaterializer()

  implicit val ex = system.dispatcher

  val host = "https://enigmatic-hamlet-4927.herokuapp.com/"

  def createHttpRequest(url: String, method: HttpMethod): HttpRequest = {
    val headers: List[HttpHeader] = List(
      HttpHeader.parse("ACCEPT", "*/*") match {
        case ParsingResult.Ok(h, _) => Some(h)
        case _ => None
      }
    ).flatten

    HttpRequest(
      method = method,
      uri = host + url
      //headers = headers
    )
  }



  def getBoard(): Board = {
    println("Getting the posse from server")


    val reqUri = host + "posse"
    val getBoardRequestResponse = Http(system).singleRequest(createHttpRequest(reqUri, HttpMethods.POST))

//    getBoardRequestResponse.failed.foreach { throwable =>
//      println(s"The request failed with following throwable: ${throwable.getMessage}")
//    }

    // if success
    val board = getBoardRequestResponse.map { response =>
      response.status match {
        case OK =>

          println("Http call")
          val entity = response.entity

          //val boardString = response.entity.dataBytes.map(d => d.utf8String)
          val boardString = response.entity.dataBytes

          val file = "/tmp/board"
          val fileSink = FileIO.toPath(Paths.get(file))
          val boardStringJson = boardString.toMat(fileSink)(Keep.right)

          val json = io.Source.fromFile(file).mkString

          val board = json.parseJson.convertTo[Board]
          board

        case code =>
          println(s"Http call failed, $code")
          // if its not 200 OK, lets check what message server sent
          val msg = response.entity.dataBytes.map(_.utf8String)
          val error = s"unexpected status code: $code msg: $msg"
          Board(List.empty[Cell])
      }
    }

    import scala.concurrent.duration._
    val b = Await.result(board, 3.seconds)
    b
  }

  var loop = true
  while(loop) {
    val board: Board = getBoard()
    Posse.validateBoard(board)
    loop = false
  }

  system.terminate()

}
