package com.example

trait Requests {

  case class MatchRequest(
                         gameId: Int,
                         matches: Seq[String]
                         )

}
