package org.vincibean.akka.word.count.actors

import akka.actor.{Actor, ActorLogging, Props}
import org.vincibean.akka.word.count.actors.StringCounterActor.{
  ProcessString,
  StringProcessed
}

object StringCounterActor {

  case class ProcessString(string: String)
  case class StringProcessed(wordCountInLine: Map[String, Int])

  def props: Props = Props[StringCounterActor]
}

class StringCounterActor extends Actor with ActorLogging {

  def receive: Receive = {
    case ProcessString(string) =>
      val wordCountInLine = string.toLowerCase
        .split("""\W+""")
        .filter(_.nonEmpty)
        .groupBy(identity)
        .mapValues(_.length)
      sender ! StringProcessed(wordCountInLine)
    case msg => log.error(s"Unrecognized message $msg")

  }

}
