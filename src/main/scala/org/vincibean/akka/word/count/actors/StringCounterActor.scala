package org.vincibean.akka.word.count.actors

import akka.actor.{Actor, ActorLogging, Props}
import org.vincibean.akka.word.count.actors.StringCounterActor.{
  ProcessString,
  StringProcessed
}

object StringCounterActor {

  case class ProcessString(string: String)

  case class StringProcessed(words: Integer)

  def props: Props = Props[StringCounterActor]
}

class StringCounterActor extends Actor with ActorLogging {

  def receive: Receive = {
    case ProcessString(string) =>
      val wordsInLine = string.split(" ").length
      sender ! StringProcessed(wordsInLine)
    case msg => log.error(s"Unrecognized message $msg")

  }

}
