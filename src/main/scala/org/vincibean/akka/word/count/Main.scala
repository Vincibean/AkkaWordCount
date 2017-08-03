package org.vincibean.akka.word.count

import akka.actor.{ActorSystem, Props}
import akka.dispatch.ExecutionContexts._
import akka.pattern.ask
import akka.util.Timeout
import org.vincibean.akka.word.count.actors.WordCounterActor
import org.vincibean.akka.word.count.actors.WordCounterActor.StartProcessFileMsg

import scala.concurrent.duration._
import scala.language.postfixOps

object Main {

  lazy val defaultFileName: String = "all-shakespeare.txt"
  lazy val defaultFilePath: String =
    this.getClass.getClassLoader.getResource(defaultFileName).getPath

  def main(args: Array[String]) {
    implicit val ec = global
    val system = ActorSystem()
    val filePath = args.headOption.getOrElse(defaultFilePath)
    val actor = system.actorOf(WordCounterActor.props(filePath))
    implicit val timeout = Timeout(25 seconds)
    val future = actor ? StartProcessFileMsg
    future.map { result =>
      println("Total number of words " + result)
      system.terminate()
    }
  }
}
