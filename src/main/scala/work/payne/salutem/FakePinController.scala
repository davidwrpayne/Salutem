package work.payne.salutem

import akka.actor.{ActorRef, Actor}
import akka.event.Logging

/**
  * Created by david.payne on 4/4/16.
*/
case class FakePinController() extends Actor {
  val log = Logging(context.system, this)
  var eventListeners: Array[ActorRef] = Array()



  override def receive: Receive = {
    case "Heartbeat" => {
      val zones = Array(
        Zone(0,"Front Door", true)
      )
      sender() ! ("Heartbeat",zones)
    }

    case ("registerHandler",listener: ActorRef) => eventListeners = eventListeners :+ listener

    case _ => log.error("received message I don't know how to handle")
  }

}