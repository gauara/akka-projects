package com.example

import java.nio.file.Paths

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, IOResult}
import akka.http.scaladsl.model.StatusCodes.OK
import akka.util.ByteString
import akka.stream.scaladsl.{FileIO, Flow, Keep, RestartSource, Source}
import com.example.LogStreamProcessorUtils.LogEvent

import scala.concurrent.Future
import spray.json.DefaultJsonProtocol._
import spray.json._
import akka.http.scaladsl.server.Directives._

import scala.util.Failure
import akka.stream.scaladsl.Framing

/*

Client does following
(1) Post - Make an upload call, provides name of TEXT file to be uploaded to the Server, Server saves as JSON LogEvent file
(2) Get - Make a /get call to get a JSON LogEvent file and filters only ERROR message and copy locally


Tips and Hints -- https://github.com/novakov-alexey/meetup-akka-streams/blob/master/src/main/scala/meetup/streams/ex1.tweets/TweetsWordCount.scala
https://stackoverflow.com/questions/23714383/what-are-all-the-possible-values-for-http-content-type-header

 */

object StreamingClientApp extends App with EventMarshalling {


  implicit val system = ActorSystem("stream-client")
  implicit val materialzier = ActorMaterializer()

  implicit val ec = system.dispatcher

  val serverAddress = "http://localhost:8080/"

  def createHttpRequest(url: String, method: HttpMethod): HttpRequest = {
    // demonstrate a HTTP header use
    val headers: List[HttpHeader] = List(
      HttpHeader.parse("ACCEPT", "*/*") match {
        case ParsingResult.Ok(h, _) => Some(h)
        case _ => None
      }
    ).flatten

    HttpRequest(
      method = method,
      uri = serverAddress + url,
      headers = headers
      //      entity = HttpEntity(
      //        // content type is just for info to client/server, client and server can look and handle accordingly
      //        contentType = ContentType(MediaTypes.`application/x-www-form-urlencoded`, HttpCharsets.`UTF-8`),
      //        string = ""
      //      )
      // default HttpEntity and other properties
    )
  }

  def createHttpRequestToUploadContent(srcFile: String, url: String, method: HttpMethod): HttpRequest = {
    // demonstrate a HTTP header use
    val headers: List[HttpHeader] = List(
      HttpHeader.parse("ACCEPT", "*/*") match {
        case ParsingResult.Ok(h, _) => Some(h)
        case _ => None
      }
    ).flatten

    val data: Source[ByteString, Future[IOResult]] =
      FileIO.fromPath(Paths.get("/tmp/" + srcFile))

    HttpRequest(
      method = method,
      uri = serverAddress + url,
      headers = headers,
      entity = HttpEntity(
        contentType = ContentType(MediaTypes.`text/plain`, HttpCharsets.`UTF-8`), // or ContentTypes.`text/plain`
        data = data
      ).withoutSizeLimit() // IMPORTANT -- this would thrown an exception at server otherwise

    )

  }

  def createHttpRequestToDownloadContent(srcFile: String, url: String, method: HttpMethod): HttpRequest = {
    val headers: List[HttpHeader] = List(
      HttpHeader.parse("ACCEPT", "*/*") match {
        case ParsingResult.Ok(h, _) => Some(h)
        case _ => None
      }
    ).flatten

    HttpRequest(
      method = method,
      uri = serverAddress + url,
      headers = headers
    )

  }

  def postStreamCopyRequest(): Unit = {
    val fileName = "events.txt"
    val reqUri = s"copy/$fileName"

    println("Inside stream request")
    val uploadRequestResponse = Http(system).singleRequest(createHttpRequest(reqUri, HttpMethods.POST))

    println("Made the call - ")

    uploadRequestResponse.failed.foreach { throwable =>
      println(s"The request failed with following throwable: ${throwable.getMessage}")
    }

    // if success
    uploadRequestResponse.map { response =>
      response.status match {
        case OK =>
          println(response.entity.dataBytes.map(_.utf8String))
        case code =>
          // if its not 200 OK, lets check what message server sent
          val msg = response.entity.dataBytes.map(_.utf8String)
          val error = s"unexpected status code: $code msg: $msg"
          println(error)
      }
    }
  }


  def postUploadRequest(): Unit = {
    val fileToBeUploaded = "events.txt"
    val fileNameAtServer = "eventsUploaded.txt"

    val reqUri = s"upload/$fileNameAtServer"

    val uploadRequestResponse = Http(system).singleRequest(createHttpRequestToUploadContent(fileToBeUploaded, reqUri, HttpMethods.POST))

    uploadRequestResponse.failed.foreach { throwable =>
      println(s"The request failed with following throwable: ${throwable.getMessage}")
    }

    // if success
    uploadRequestResponse.map { response =>
      response.status match {
        case OK =>
          println(response.entity.dataBytes.map(_.utf8String).toString())
        case code =>
          // if its not 200 OK, lets check what message server sent
          val msg = response.entity.dataBytes.map(_.utf8String)
          val error = s"unexpected status code: $code msg: $msg"
          println(error)
      }
    }
  }

  def postUploadJsonRequest(): Unit = {
    val fileToBeUploaded = "events.txt"
    val fileNameAtServer = "eventsUploaded.json"

    val reqUri = s"uploadJson/$fileNameAtServer"

    val uploadRequestResponse = Http(system).singleRequest(createHttpRequestToUploadContent(fileToBeUploaded, reqUri, HttpMethods.POST))

    uploadRequestResponse.failed.foreach { throwable =>
      println(s"The request failed with following throwable: ${throwable.getMessage}")
    }

    // if success
    uploadRequestResponse.map { response =>
      response.status match {
        case OK =>
          println(response.entity.dataBytes.map(_.utf8String).toString())
        case code =>
          // if its not 200 OK, lets check what message server sent
          val msg = response.entity.dataBytes.map(_.utf8String)
          val error = s"unexpected status code: $code msg: $msg"
          println(error)
      }
    }
  }


  def requestToRestartableSource(fileNameAtServer: String, url: String, method: HttpMethod): HttpRequest = {
    val headers: List[HttpHeader] = List(
      HttpHeader.parse("ACCEPT", "*/*") match {
        case ParsingResult.Ok(h, _) => Some(h)
        case _ => None
      }
    ).flatten

    HttpRequest(
      method = method,
      uri = serverAddress + url,
      headers = headers
    )
  }

  def responseFromRestartableSource(fileNameAtServer: String, url: String, method: HttpMethod): Source[ByteString, _] = {
    import scala.concurrent.duration._

    RestartSource.withBackoff(
      minBackoff = 3000.seconds,
      maxBackoff = 30000.seconds,
      randomFactor = 0.2
    ) { () =>
      Source.fromFutureSource {
        val httpRequest = requestToRestartableSource(fileNameAtServer, url, method)

        val response = Http(system).singleRequest(httpRequest)
        response.failed.foreach(t => System.err.println(s"Request has been failed with $t"))
        response.map(res => {
          res.status match {
            case OK =>
              println("Response is returned from server for the Download request")
              res.entity.withoutSizeLimit().dataBytes
            case code =>
              val text = res.entity.dataBytes.map(_.utf8String)
              val error = s"Unexpected status code: $code, $text"
              System.err.println(error)
              Source.failed(new RuntimeException(error))
          }
        })
      }
    }
  }


  def getDownnloadJsonRequest(): Unit = {
    val fileNameAtServer = "eventsUploaded.json"

    val reqUri = s"downloadJson/$fileNameAtServer"

    val restartableSource = responseFromRestartableSource(fileNameAtServer, reqUri, HttpMethods.GET)

    import scala.concurrent.duration._

    val idleDuration = 300.seconds
    val docDelimiter = "\n"

    val warningFilter = Flow[LogEvent].filter(_.state == LogStreamProcessorUtils.Error)

    val parseToJson = Flow[LogEvent].map { event =>
      ByteString(event.toJson.compactPrint + "\n")
    }

    val inFile = "/tmp/eventsDownloaded.json"

    val fileSink = FileIO.toPath(Paths.get(inFile))

    val framing = Framing.delimiter(ByteString("\n"), 10240)
      .map(d => d.decodeString("UTF8"))
      .map(_.parseJson.convertTo[LogEvent])
      //.collect {case Some(e) => e}  // its not an option


    val flow = restartableSource
      .via(framing)


    val graph = flow
      .via(parseToJson)
      .toMat(fileSink)(Keep.right)

    val executeGraph = graph.run()
    onComplete(executeGraph) {
      case scala.util.Success(IOResult(count, scala.util.Success(Done))) =>
        complete(StatusCodes.OK, s"JSON Downloaded in FileName: $inFile and bytes read: $count")
      case scala.util.Success(IOResult(count, Failure(e))) =>
        complete(StatusCodes.BadRequest, s"Msg: ${e.getMessage}")
      case Failure(e) =>
        complete(StatusCodes.BadRequest, s"Msg: ${e.getMessage}")
    }

  }


  // MAKING CLIENT CALLS HERE

  // 1. POST - "/copy" - Stream Copy File, Client does not stream, server copies from specified file path
  //           events.txt -> eventsCopied.txt
  // 2. POST - "/upload" - Stream Upload File from Client to Server - client send the content
  //           events.txt -> eventsUploaded.txt
  // 3. POST - "uploadJson" - Parse uploaded content as in (2) but convert from TEXT to JSON
  //           events.txt -> eventsUploaded.json
  // 4. GET - "downloadJson" - Clinet Gets the Content and do some filtering and processing
  //           events.txt -> eventsDownloaded.json


  // UNCOMMENT to run specific request

  // 1.
  //postStreamCopyRequest()

  // 2.
  //postUploadRequest()

  // 3.
  //postUploadJsonRequest()

  // 4.
  getDownnloadJsonRequest()

  // ideally use below call, but OK for testing -- would throw dead letter queue akka error
  //system.terminate()

}