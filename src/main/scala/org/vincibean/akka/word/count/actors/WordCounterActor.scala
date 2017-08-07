package org.vincibean.akka.word.count.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.vincibean.akka.word.count.actors.StringCounterActor.{
  ProcessString,
  StringProcessed
}
import org.vincibean.akka.word.count.actors.WordCounterActor.{
  FileProcessed,
  StartProcessingFile
}

import scala.io.Source

object WordCounterActor {

  case object StartProcessingFile
  case class FileProcessed(wordCount: Int)

  def props(filePath: String): Props = Props(new WordCounterActor(filePath))

}

class WordCounterActor(filename: String) extends Actor with ActorLogging {
  private var totalLines = 0
  private var linesProcessed = 0
  private var result = 0
  private var fileSender: Option[ActorRef] = None

  def receive: Receive = {
    case StartProcessingFile =>
      fileSender = Some(sender) // save reference to process invoker
      Source.fromFile(filename).getLines.foreach { line =>
        val stringCounter = context.actorOf(StringCounterActor.props)
        stringCounter ! ProcessString(line)
        totalLines += 1
      }

    case StringProcessed(words) =>
      result += words
      linesProcessed += 1
      if (linesProcessed == totalLines) {
        fileSender.foreach(_ ! FileProcessed(result)) // provide result to process invoker
      }
    case msg => log.error(s"Unrecognized message $msg")
  }
}
