package com.example

trait Validattions extends Requests {

  import Responses._

  def validateRequest(req: MatchRequest)(implicit gameLogic: GameLogic): Option[ServerResponse] = {

    val gameId = req.gameId
    val matches = req.matches

    gameLogic.games.filter(_.gameId == gameId).headOption match {
      case Some(g) =>
        val w = g.words
        val m = g.words.map(k => GameLogic.keysAndMatches.getOrElse(k, ""))
        if (m == matches) {
          Some(successScenarioResponse(g, g.words))
        } else {
          Some(matchResultResponse(g, "failed", w))
        }
      case _ => None
    }
  }
}
