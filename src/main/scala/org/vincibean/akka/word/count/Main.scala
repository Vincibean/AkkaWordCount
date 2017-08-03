package org.vincibean.akka.word.count

import akka.actor.{ActorSystem, Props}
import akka.dispatch.ExecutionContexts._
import akka.pattern.ask
import akka.util.Timeout
import org.vincibean.akka.word.count.actors.WordCounterActor
import org.vincibean.akka.word.count.actors.WordCounterActor.StartProcessFileMsg

import scala.concurrent.duration._
import scala.language.postfixOps

object Main extends App {

  override def main(args: Array[String]) {
    implicit val ec = global
    val system = ActorSystem()
    val actor = system.actorOf(Props(new WordCounterActor(args(0))))
    implicit val timeout = Timeout(25 seconds)
    val future = actor ? StartProcessFileMsg()
    future.map { result =>
      println("Total number of words " + result)
      system.terminate()
    }
  }
}