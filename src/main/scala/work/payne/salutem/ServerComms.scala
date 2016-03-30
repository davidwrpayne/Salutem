package work.payne.salutem

import akka.actor.{ActorRef, Actor}
import akka.event.Logging
import spray.json._
import JsonFormatProtocal._

class ServerComms extends Actor {
  val log = Logging(context.system, this)

  var eventListeners: Array[ActorRef] = Array()

  def receive = {
    case ("registerHandler",listener: ActorRef) => eventListeners = eventListeners :+ listener
    case msg: Message => {
      log.info( msg.toJson.toString() )
    }
    case _  => log.info("received unknown message")
  }


}
