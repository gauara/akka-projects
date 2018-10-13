package com.example


trait ServerResponse

case class NewGameResponse(
                            gameId: Int,
                            words: Seq[String],
                            dict: Seq[String],
                            chances: Int
                          ) extends ServerResponse

case class FailedScenarioResponse(
                                   gameId: Int,
                                   status: String,
                                   words: Seq[String] ,
                                   chance: Int
                                 ) extends ServerResponse

case class SuccessScenarioResponse(
                                   gameId: Int,
                                   status: String,
                                   pairs: Map[String, String] ,
                                   chance: Int
                                 ) extends ServerResponse

case class MatchResultResponse(
                                    gameId: Int,
                                    status: String,
                                    pairs: Map[String, String] ,
                                    chance: Int
                                  ) extends ServerResponse

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

  def abortedScenarioResponse(g: Game) = {
    FailedScenarioResponse(
      gameId = g.gameId,
      status = "aborted",
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

  def matchResultResponse(g: Game, status: String, prevWords: Seq[String]) = {
    SuccessScenarioResponse(
      gameId = g.gameId,
      status = status,
      pairs = prevWords.map(w => w -> keysAndMatches.get(w).get).toMap,
      chance = g.chance
    )
  }
}
