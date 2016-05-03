package work.payne.salutem

import akka.actor.{ActorRef, Actor}
import akka.event.Logging
import work.payne.salutem.InternalActorMessages.{RegisterHandler, Heartbeat}

/**
  * Created by david.payne on 4/4/16.
*/
case class FakePinController() extends Actor {
  val log = Logging(context.system, this)
  var eventListeners: Array[ActorRef] = Array()



  override def receive: Receive = {
    case Heartbeat(None) => {
      val zones = List(
        Zone(0,"Front Door", true)
      ).toList
      sender() ! Heartbeat(Some(zones))
    }

    case RegisterHandler(listener) => eventListeners = eventListeners :+ listener

    case m => log.error(s"received message I don't know how to handle $m from ${sender()}")
  }

}