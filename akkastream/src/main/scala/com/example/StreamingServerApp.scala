package com.example

import java.nio.file.Paths
import java.nio.file.Files

import akka.Done
import akka.actor.ActorSystem
import akka.actor.Status.Success
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.stream.{ActorMaterializer, IOResult}
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{FileIO, Flow, Framing, Keep, RunnableGraph, Sink, Source}
import akka.util.ByteString

import scala.concurrent.Future
import scala.util.Failure


object StreamingServerApp extends App {

  implicit val system = ActorSystem("stream-server")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher


  val route: Route =
    concat(

      // simple streaming copy file from source to destination
      pathPrefix("copy"/ Segment) { f =>
        pathEndOrSingleSlash {
          post {
            val fileName = f
            println(s"Server got the request to copy file name: $f")

            val source: Source[ByteString, Future[IOResult]] =
              FileIO.fromPath(Paths.get("/tmp/" + fileName))

            val sink: Sink[ByteString, Future[IOResult]] =
              FileIO.toPath(Paths.get("/tmp/eventsCopied.text"))

            val graph: RunnableGraph[Future[IOResult]] =
              source.to(sink)

            val executeGraph = graph.run()
            onComplete(executeGraph) {
              case scala.util.Success(IOResult(count, scala.util.Success(Done))) =>
                complete(StatusCodes.OK, s"FileName: $f and bytes read: $count")
              case scala.util.Success(IOResult(count, Failure(e))) =>
                complete(StatusCodes.BadRequest, s"Msg: ${e.getMessage}")
              case Failure(e) =>
                complete(StatusCodes.BadRequest, s"Msg: ${e.getMessage}")
            }
          }
        }
      },

      pathPrefix("upload"/ Segment) { f =>
        println(s"Will be uploading file : $f")
        val fileSink: Sink[ByteString, Future[IOResult]] =
          FileIO.toPath(Paths.get("/tmp/" + f))

        pathEndOrSingleSlash {
          post {
            // extract body from the request
            entity(as[HttpEntity]) { entity =>

              // This onComplete is wrapped in a Try
              onComplete(
                entity
                  .withoutSizeLimit()
                  .dataBytes
                  .toMat(fileSink)(Keep.right)
                  .run()
              ) {
                case scala.util.Success(IOResult(count, scala.util.Success(Done))) =>
                  complete(StatusCodes.OK, s"FileName: $f and bytes written: $count")
                case scala.util.Success(IOResult(count, Failure(e))) =>
                  complete(StatusCodes.BadRequest, s"Msg: ${e.getMessage}")
                case Failure(e) =>
                  complete(StatusCodes.BadRequest, s"Msg: ${e.getMessage}")
              }
            }
          }
        }
      },

      pathPrefix("uploadJson"/ Segment) { f =>
        println(s"Will be uploading file : $f as JSON format")

        import LogStreamProcessorUtils._

        val maxLines = 10240
        val framing = Framing.delimiter(ByteString("\n"), maxLines)
          .map(_.decodeString("UTF8"))
          .map(LogStreamProcessorUtils.parseLineEx)
          .collect {case Some(e) => e}

        val errorFilterFlow = Flow[LogEvent].filter(_.state == Error) // equivalent to Flow[Event].filter

        val eventToJsonStringFlow = Flow[LogEvent].map { event =>
          ByteString(event.toJson.compactPrint + "\n")
        }

        val fileSink: Sink[ByteString, Future[IOResult]] =  FileIO.toPath(Paths.get("/tmp/" + f))

        pathEndOrSingleSlash {
          post {
            // extract body from the request
            entity(as[HttpEntity]) { entity =>

              // This onComplete is wrapped in a Try
              onComplete(
                entity
                  .withoutSizeLimit()
                  .dataBytes
                  .via(framing)
                  .via(errorFilterFlow)
                  .via(eventToJsonStringFlow)
                  .toMat(fileSink)(Keep.right)
                  .run()
              ) {
                case scala.util.Success(IOResult(count, scala.util.Success(Done))) =>
                  complete(StatusCodes.OK, s"FileName: $f and bytes written: $count")
                case scala.util.Success(IOResult(count, Failure(e))) =>
                  complete(StatusCodes.BadRequest, s"Msg: ${e.getMessage}")
                case Failure(e) =>
                  complete(StatusCodes.BadRequest, s"Msg: ${e.getMessage}")
              }
            }
          }
        }
      },

      pathPrefix("downloadJson" / Segment) { logFile =>
        println("Server downLoad path requested")
        pathEndOrSingleSlash {
          get {
            val fileWithPath = "/tmp/" + logFile
            if(Files.exists(Paths.get(fileWithPath))) {
              val src = FileIO.fromPath(Paths.get(fileWithPath)) // create a file Source
              complete(
                HttpEntity(ContentTypes.`application/json`, src)
              )
            } else {
              complete(StatusCodes.NotFound)
            }
          }
        }
      }
    )

  // build the App
  val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(route, "localhost", 8080)
  bindingFuture.map { serverBinding =>
    println(s"Bound to server at localhost and port 8080 and binding: $serverBinding")
  }.onFailure {
    case e: Exception =>
      println(s"Failed to bind with following message: ${e.getMessage}")
      //system.terminate()
  }

}
