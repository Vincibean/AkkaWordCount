package org.vincibean.akka.word.count.actors

import akka.actor.{Actor, ActorRef, Props}
import org.vincibean.akka.word.count.actors.StringCounterActor.{ProcessStringMsg, StringProcessedMsg}
import org.vincibean.akka.word.count.actors.WordCounterActor.StartProcessFileMsg

object WordCounterActor {

  case class StartProcessFileMsg()

}

class WordCounterActor(filename: String) extends Actor {

  private var running = false
  private var totalLines = 0
  private var linesProcessed = 0
  private var result = 0
  private var fileSender: Option[ActorRef] = None

  def receive: Receive = {
    case StartProcessFileMsg() =>
      if (running) {
        // println just used for example purposes;
        // Akka logger should be used instead
        println("Warning: duplicate start message received")
      } else {
        running = true
        fileSender = Some(sender) // save reference to process invoker
        import scala.io.Source._
        fromFile(filename).getLines.foreach { line =>
          context.actorOf(Props[StringCounterActor]) ! ProcessStringMsg(line)
          totalLines += 1
        }
      }
    case StringProcessedMsg(words) =>
      result += words
      linesProcessed += 1
      if (linesProcessed == totalLines) {
        fileSender.foreach(_ ! result) // provide result to process invoker
      }
    case _ => println("message not recognized!")
  }
}