package com.example

case class NewGameResponse(
                            gameId: Int,
                            words: Seq[String],
                            dict: Seq[String],
                            chances: Int
                          )

case class FailedScenarioResponse(
                                   gameId: Int,
                                   status: String,
                                   words: Seq[String] ,
                                   chance: Int

                                 )

case class SuccessScenarioResponse(
                                   gameId: Int,
                                   status: String,
                                   pairs: Map[String, String] ,
                                   chance: Int

                                 )

object Responses {

  import GameLogic._

  def newGameResponse(g: Game): NewGameResponse = {
    NewGameResponse(
      gameId = g.gameId,
      words = g.words,
      dict = keysAndMatches.values.toSeq,
      chances = g.chance
    )
  }

 def failedScenarioResponse(g: Game) = {
    FailedScenarioResponse(
      gameId = g.gameId,
      status = "failed",
      words = getNewWords(),
      chance = g.chance // game counter should be handled somewhere else
    )
  }

  def successScenarioResponse(g: Game, prevWords: Seq[String]) = {
    SuccessScenarioResponse(
      gameId = g.gameId,
      status = "success",
      pairs = prevWords.map(w => w -> keysAndMatches.get(w).get).toMap,
      chance = g.chance
    )
  }
}
