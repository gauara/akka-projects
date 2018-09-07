package com.example

import akka.http.scaladsl.model.{HttpEntity, HttpResponse, ResponseEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, concat, get, pathEndOrSingleSlash, pathPrefix, post}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.Segments._
import com.example.GameLogic.Game

trait TicTacToeApi extends JsonSupport {

  val gl = GameLogic()

  val routes: Route =
    concat(
      pathPrefix("tictactoe") {
        pathPrefix("newgame") { // use multiple path prefixes if the depth is > 1
          pathEndOrSingleSlash {
            println("got a request ...")
            post {
              val response = gl.getNewGameResponse()
              complete(response)
            }
          }
        }
      },

      // segment = gameId
      pathPrefix("tictactoe" / Segment) { gameId =>
          pathEndOrSingleSlash {
            post {

              val msg = gl.games.filter(_.gameId == gameId.toInt).headOption match {
                case Some(g: Game) => "Valid Game"
                case _ => "Invalid Game"
              }
              complete((StatusCodes.OK, msg))
            }
          }
      },

        path("health") {
        pathEndOrSingleSlash {
          get {
            complete((StatusCodes.OK, "Healthy Here"))
          }
        }
      }


    )
}
