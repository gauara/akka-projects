package com.example

import com.example.GameLogic.Game

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.Random
import Responses._

object GameLogic {

  val keysAndMatches: Map[String, String] = Map(
    "hello" -> "motto",
    "foo" -> "bar",
    "tic" -> "tac",
    "this" -> "that",
    "face" -> "book",
    "thunder" -> "bolt",
    "mix" -> "match",
  )

  case class Game(
                   gameId: Int,
                   words: Seq[String],
                   chance: Int
                 )

  def getNewWords(): Seq[String] = {
    val MinWords = 3 // min 3 numbers are returned
    val rand = MinWords + Random.nextInt((keysAndMatches.keys.size - MinWords) + 1)
    Random.shuffle(keysAndMatches.keys.toSeq.take(rand))
  }
}

case class GameLogic(
                    var games: ListBuffer[Game] = ListBuffer.empty,
                    var nextGameId: Int = 1
                    ) {

  private def newGame(): Game = {
    import GameLogic._

    val g = Game(
      gameId = this.nextGameId,
      words = getNewWords(),
      chance = 0
    )
    // increament the counter
    // add new game to the cache
    this.nextGameId = this.nextGameId + 1
    this.games.append(g)
    g
  }

  def getNewGameResponse(): NewGameResponse = {
    newGameResponse(newGame())
  }

}