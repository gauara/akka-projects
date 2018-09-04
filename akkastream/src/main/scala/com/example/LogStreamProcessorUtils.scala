package com.example

object LogStreamProcessorUtils extends EventMarshalling {

  sealed trait State
  case object Critical extends State
  case object Error  extends State
  case object Ok extends State
  case object Warning extends State

  case class LogEvent(
                       host: String,
                       service: String,
                       state: State,
                       description: String
                     )

  object State {
    def norm(str: String): String = str.toLowerCase
    def norm(state: State): String = norm(state.toString)

    val ok = norm(Ok)
    val warning = norm(Warning)
    val error = norm(Error)
    val critical = norm(Critical)

    def unapply(str: String): Option[State] = {
      val normalized = norm(str)
      if(normalized == norm(Ok)) Some(Ok)
      else if(normalized == norm(Warning)) Some(Warning)
      else if(normalized == norm(Error)) Some(Error)
      else if(normalized == norm(Critical)) Some(Critical)
      else None
    }
  }


  def parseLineEx(line: String): Option[LogEvent] = {
    if(!line.isEmpty) {
      line.split("\\|") match {
        case Array(host, service, state, time, desc, tag, metric) =>
          val t = tag.trim
          val m = metric.trim
          Some(LogEvent(
            host.trim,
            service.trim,
            state.trim match {
              case State(s) => s
              case _        => throw new Exception(s"Unexpected state: $line")
            },
            desc.trim,
          ))
        case Array(host, service, state, time, desc) =>
          Some(LogEvent(
            host.trim,
            service.trim,
            state.trim match {
              case State(s) => s
              case _        => throw new Exception(s"Unexpected state: $line")
            },
            desc.trim
          ))
        case x =>
          throw new LogParseException(s"Failed on line: $line")
      }
    } else None
  }

  case class LogParseException(msg:String) extends Exception(msg)
}
