package org.vincibean.akka.word.count.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.vincibean.akka.word.count.actors.StringCounterActor.{
  ProcessLine,
  LineProcessed
}
import org.vincibean.akka.word.count.actors.WordCounterActor.{
  FileProcessed,
  StartProcessingFile
}

import scala.io.Source

object WordCounterActor {

  case class StartProcessingFile(filename: String)
  case class FileProcessed(wordCount: Map[String, Int])

  def props: Props = Props[WordCounterActor]

}

class WordCounterActor extends Actor with ActorLogging {
  private var totalLines = 0
  private var linesProcessed = 0
  private var lineResults: Seq[Map[String, Int]] = Seq.empty[Map[String, Int]]
  private var fileSender: Option[ActorRef] = None

  def receive: Receive = {
    case StartProcessingFile(filename) =>
      fileSender = Some(sender) // save reference to process invoker
      val lines = Source.fromFile(filename).getLines.toStream
      totalLines = lines.size
      lines.foreach { line =>
        val stringCounter = context.actorOf(StringCounterActor.props)
        stringCounter ! ProcessLine(line)
      }

    case LineProcessed(wordCountsInLine) =>
      lineResults = lineResults :+ wordCountsInLine
      linesProcessed += 1
      if (linesProcessed >= totalLines) {
        val res = reduce(lineResults)
        fileSender.foreach(_ ! FileProcessed(res)) // provide result to process invoker
      }
    case msg => log.error(s"Unrecognized message $msg")
  }

  private def merge(m1: Map[String, Int],
                    m2: Map[String, Int]): Map[String, Int] =
    (m1.keySet ++ m2.keySet)
      .map(k => k -> (m1.getOrElse(k, 0) + m2.getOrElse(k, 0)))
      .toMap

  private def reduce(seq: Seq[Map[String, Int]]): Map[String, Int] = {
    seq.reduce(merge)
  }

}
