package work.payne.salutem

import akka.actor.ActorRef

/**
  * Created by david.payne on 5/2/16.
  */
object InternalActorMessages {

  case class Heartbeat(zones: Option[List[Zone]])

  case class Arm()
  case class StatusRequest()
  case class Code(code:String)
  case class RegisterHandler(listener: ActorRef)
  case class ServerCommMessage(message: String) {
  }
}
