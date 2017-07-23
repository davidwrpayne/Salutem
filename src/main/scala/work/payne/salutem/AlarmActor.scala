package work.payne.salutem

import akka.actor.Status.Status
import akka.actor.{ActorRef, Actor}
import akka.actor.Actor.Receive
import akka.event.Logging
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent
import work.payne.salutem.InternalActorMessages._


/**
  * Created by david.payne on 3/29/16.
  */
class AlarmActor(pinController: ActorRef) extends Actor {
  val log = Logging(context.system, this)
  var status: String = Status.UnSecure
  var zones: List[Zone] = List()

  pinController ! RegisterHandler(self) // register the controller for listening
//  serverComms ! RegisterHandler(self)


  override def receive: Receive = {

    // got heart beat  now need to request a zone heartbeat from pin controller.
    case Heartbeat(None) => {
      pinController ! Heartbeat(None)

    }

    // receive zone heartbeat from pin controller
    case Heartbeat(Some(zones)) => {
      //got a heartbeat of up to date zones from the pinController
      this.zones = zones
//      serverComms ! Message(status, MsgType.HeartBeat, zones)
    }

    case _: StatusRequest =>
      log.info(s"Status Request received. Responding with Status:$status")
      sender ! status

    case _:Arm => {
      if (status == Status.UnSecure) {
        status = Status.Secure
      }
      sender ! status
    }

    case e: GpioPinDigitalStateChangeEvent => {
      log.info(s"got state change for pin ${e.getPin.getName}, state ${e.getState.getName}")
      if (status != Status.UnSecure) {
        status = Status.Alarmed
        log.info("Breach occured")
//        serverComms ! ServerCommMessage(s"Pin state changed ${e.getPin.getName}, state ${e.getState.getName}")
      }
    }

    case Code(code) => {
      // for handling of a valid code input
      if(code.equals("1373")) {
        log.info("Valid code input")
        if( status == Status.Alarmed) {
          log.info("reseting system")
          status = Status.UnSecure
        }
        else if ( status == Status.Secure) {
          log.info("disarming system")
          status = Status.UnSecure
          sender ! status
        }
        else if ( status == Status.UnSecure) {
          log.info("code input while system unsecure")
          sender ! status
        }

      }
      else { log.info(s"Invalid code: ${code}")
        sender ! "Invalid Code"
//        serverComms ! ServerCommMessage(s"Invalid Code input.")
      }
    }

    case m@_ => log.error(s"received message dont know what to do with: $m which came from ${sender().path}")

  }
}
