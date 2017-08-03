package org.vincibean.akka.word.count.actors

import akka.actor.{Actor, ActorLogging, Props}
import org.vincibean.akka.word.count.actors.StringCounterActor.{
  ProcessStringMsg,
  StringProcessedMsg
}

object StringCounterActor {

  case class ProcessStringMsg(string: String)

  case class StringProcessedMsg(words: Integer)

  def props: Props = Props[StringCounterActor]
}

class StringCounterActor extends Actor with ActorLogging {

  def receive: Receive = {
    case ProcessStringMsg(string) =>
      val wordsInLine = string.split(" ").length
      sender ! StringProcessedMsg(wordsInLine)
    case msg => log.error(s"Unrecognized message $msg")

  }

}
