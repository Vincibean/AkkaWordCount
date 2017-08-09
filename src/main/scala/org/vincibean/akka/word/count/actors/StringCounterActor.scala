package org.vincibean.akka.word.count.actors

import akka.actor.{Actor, ActorLogging, Props}
import org.vincibean.akka.word.count.actors.StringCounterActor.{
  ProcessLine,
  LineProcessed
}

object StringCounterActor {

  case class ProcessLine(string: String)
  case class LineProcessed(wordCountInLine: Map[String, Int])

  def props: Props = Props[StringCounterActor]
}

class StringCounterActor extends Actor with ActorLogging {

  def receive: Receive = {
    case ProcessLine(line) =>
      val wordCountInLine = line.toLowerCase
        .split("""\W+""")
        .filter(_.nonEmpty)
        .groupBy(identity)
        .mapValues(_.length)
      sender ! LineProcessed(wordCountInLine)
    case msg => log.error(s"Unrecognized message $msg")

  }

}
