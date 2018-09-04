package com.example

import spray.json.DefaultJsonProtocol

import spray.json._

trait EventMarshalling extends DefaultJsonProtocol{

  import LogStreamProcessorUtils._
  import DefaultJsonProtocol._

  implicit val stateFormat = new JsonFormat[State] {

    def write(state: State) = JsString(State.norm(state))

    def read(value: JsValue) = value match {
      case JsString("ok") => Ok
      case JsString("warning") => Warning
      case JsString("error") => Error
      case JsString("critical") => Critical
      case js =>
        val msg = s"Could not deserialize $js to State."
        deserializationError(msg)
    }
  }

  implicit val eventFormat = jsonFormat4(LogEvent)
}
