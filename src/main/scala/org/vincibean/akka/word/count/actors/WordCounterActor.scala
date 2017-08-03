package org.vincibean.akka.word.count.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.vincibean.akka.word.count.actors.StringCounterActor.{
  ProcessStringMsg,
  StringProcessedMsg
}
import org.vincibean.akka.word.count.actors.WordCounterActor.StartProcessFileMsg

object WordCounterActor {

  case object StartProcessFileMsg

  def props(filePath: String): Props = Props(new WordCounterActor(filePath))

}

class WordCounterActor(filename: String) extends Actor with ActorLogging {

  private var running = false
  private var totalLines = 0
  private var linesProcessed = 0
  private var result = 0
  private var fileSender: Option[ActorRef] = None

  def receive: Receive = {
    case StartProcessFileMsg =>
      if (running) {
        log.warning("Duplicate start message received")
      } else {
        running = true
        fileSender = Some(sender) // save reference to process invoker
        import scala.io.Source._
        fromFile(filename).getLines.foreach { line =>
          context.actorOf(StringCounterActor.props) ! ProcessStringMsg(line)
          totalLines += 1
        }
      }
    case StringProcessedMsg(words) =>
      result += words
      linesProcessed += 1
      if (linesProcessed == totalLines) {
        fileSender.foreach(_ ! result) // provide result to process invoker
      }
    case msg => log.error(s"Unrecognized message $msg")
  }
}
