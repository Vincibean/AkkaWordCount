package org.vincibean.akka.word.count.actors

import akka.actor.{Actor, Props}
import org.vincibean.akka.word.count.actors.StringCounterActor.{
  ProcessStringMsg,
  StringProcessedMsg
}

object StringCounterActor {

  case class ProcessStringMsg(string: String)

  case class StringProcessedMsg(words: Integer)

  def props: Props = Props[StringCounterActor]
}

class StringCounterActor extends Actor {

  def receive: Receive = {
    case ProcessStringMsg(string) =>
      val wordsInLine = string.split(" ").length
      sender ! StringProcessedMsg(wordsInLine)
    case _ => println("Error: message not recognized")

  }

}
