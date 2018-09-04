package com.example

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCode, StatusCodes}
import akka.util.Timeout

import scala.concurrent.duration._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.example.TradeDeskActor._

import scala.concurrent.Future
import akka.pattern.ask


trait TradeRoutes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[TradeRoutes])

  val tradeDeskActor: ActorRef

  implicit lazy val timeout = Timeout(5.seconds)

  lazy val tradeRoutes: Route =
    concat(
      pathPrefix("trades") {
        concat(
          pathEnd {
            concat(
              get {
                val trades: Future[Trades] =
                  (tradeDeskActor ? GetTrades).mapTo[Trades]
                complete(trades)
              },
              post {
                entity(as[Trade]) { trade =>
                  val tradeAdded: Future[ActionPerformed] =
                    (tradeDeskActor ? AddTrade(trade)).mapTo[ActionPerformed]

                  onSuccess(tradeAdded) { msg =>
                    complete((StatusCodes.OK, msg.description))
                  }
                }
              })
          },

          pathPrefix("users") {
            path(Segment) { user =>
              get {
                val   userTrades: Future[Trades] =
                  (tradeDeskActor ? GetUserTrades(user.toLong)).mapTo[Trades]
                onSuccess(userTrades) { ut =>
                  //complete((StatusCodes.OK, userTrades))
                  complete(userTrades)
                }
              }
            }
          })
      },

      path("erase") {
        val erase = (tradeDeskActor ? Erase).mapTo[ActionPerformed]
        onSuccess(erase)(msg => {
          complete((StatusCodes.OK, msg.description))
        })
      },

      path("health") {
        complete(
          HttpResponse(
            StatusCodes.OK,
            entity = HttpEntity(
              "OK"
            )
          )
        )
      },

      pathPrefix("stocks") {
        // NOTE This -- need to attach price with segment here, nesting another separate path for price wont work
        path(Segment/"price") { stock =>
          get {
            parameters('start.as[String], 'end.as[String]) { (start, end) =>
              get
              complete((StatusCodes.OK, s"Hit the stocks end point with stock id: $stock , start: $start and end: $end" ))
            }
          }
        }
      }
    )
}



/*
curl -H "Content-type: application/json" -X POST -d '{"name": "MrX", "age": 31, "countryOfResidence": "Canada"}' http://localhost:8080/users
curl -H "Content-type: application/json" -X POST -d '{
   "id": 1,
   "type": "buy",
   "user": {
      "id": 1,
      "name": "David"
   },
   "symbol": "AC",
   "shares": 28,
   "price": 162.17,
   "timestamp": "2014-06-14 13:13:13"
}' http://localhost:8090/trades
curl -H "Content-type: application/json" -X POST -d '{
   "id": 2,
   "type": "buy",
   "user": {
      "id": 3,
      "name": "Brandon"
   },
   "symbol": "ACC",
   "shares": 25,
   "price": 146.09,
   "timestamp": "2014-06-25 13:40:13"
}' http://localhost:8090/trades
curl -H "Content-type: application/json" -X POST -d '{
   "id": 3,
   "type": "buy",
   "user": {
      "id": 2,
      "name": "Omar"
   },
   "symbol": "AC",
   "shares": 13,
   "price": 146.09,
   "timestamp": "2014-06-25 13:40:13"
}' http://localhost:8090/trades
curl -H "Content-type: application/json" -X POST -d '{
   "id": 4,
   "type": "buy",
   "user": {
      "id": 1,
      "name": "David"
   },
   "symbol": "AC",
   "shares": 12,
   "price": 137.39,
   "timestamp": "2014-06-25 13:44:13"
}' http://localhost:8090/trades
curl -H "Content-type: application/json" -X POST -d '{
   "id": 5,
   "type": "buy",
   "user": {
      "id": 3,
      "name": "Brandon"
   },
   "symbol": "AC",
   "shares": 15,
   "price": 161.35,
   "timestamp": "2014-06-26 13:15:18"
}' http://localhost:8090/trades
curl -H "Content-type: application/json" -X POST -d '{
   "id": 6,
   "type": "sell",
   "user": {
      "id": 3,
      "name": "Brandon"
   },
   "symbol": "AC",
   "shares": 10,
   "price": 162.37,
   "timestamp": "2014-06-26 15:15:18"
}' http://localhost:8090/trades
curl -H "Content-type: application/json" -X POST -d '{
   "id": 7,
   "type": "buy",
   "user": {
      "id": 3,
      "name": "Brandon"
   },
   "symbol": "ACC",
   "shares": 17,
   "price": 146.08,
   "timestamp": "2014-06-27 10:10:31"
}' http://localhost:8090/trades
curl -H "Content-type: application/json" -X POST -d '{
   "id": 8,
   "type": "buy",
   "user": {
      "id": 3,
      "name": "Brandon"
   },
   "symbol": "ACC",
   "shares": 15,
   "price": 146.11,
   "timestamp": "2014-06-27 11:08:23"
}' http://localhost:8090/trades
curl -H "Content-type: application/json" -X POST -d '{
   "id": 9,
   "type": "buy",
   "user": {
      "id": 3,
      "name": "Brandon"
   },
   "symbol": "ACC",
   "shares": 25,
   "price": 146.09,
   "timestamp": "2014-06-27 12:17:17"
}' http://localhost:8090/trades
curl -H "Content-type: application/json" -X POST -d '{
   "id": 10,
   "type": "buy",
   "user": {
      "id": 1,
      "name": "David"
   },
   "symbol": "ABR",
   "shares": 10,
   "price": 136.27,
   "timestamp": "2014-06-28 13:11:13"
}' http://localhost:8090/trades
Another API
// /trades/users/{userID}
 */
