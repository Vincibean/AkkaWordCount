package org.vincibean.akka.word.count

import akka.actor.ActorSystem
import akka.dispatch.ExecutionContexts._
import akka.pattern.ask
import akka.util.Timeout
import org.vincibean.akka.word.count.actors.WordCounterActor
import org.vincibean.akka.word.count.actors.WordCounterActor.{
  FileProcessed,
  StartProcessingFile
}

import scala.concurrent.duration._
import scala.language.postfixOps

object Main {
  lazy val defaultFileName: String = "hamlet.txt"
  lazy val defaultFilePath: String =
    this.getClass.getClassLoader.getResource(defaultFileName).getPath

  def main(args: Array[String]): Unit = {
    implicit val ec = global
    implicit val timeout = Timeout(25 seconds)
    val system = ActorSystem()
    val actor = system.actorOf(WordCounterActor.props)
    val filePath = args.headOption.getOrElse(defaultFilePath)
    val future = actor ? StartProcessingFile(filePath)
    future.foreach {
      case FileProcessed(result) =>
        println("Total number of words " + result.toSeq.sortBy(-_._2))
        system.terminate()
    }
  }
}
