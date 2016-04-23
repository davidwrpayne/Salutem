package work.payne.salutem

import akka.actor.{ActorRef, Actor}
import akka.actor.Actor.Receive
import akka.event.Logging
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent


/**
  * Created by david.payne on 3/29/16.
  */
class Alarm(pinController: ActorRef, serverComms: ActorRef) extends Actor {
  val log = Logging(context.system, this)
  var status: String = Status.UnSecure
  var zones: Array[Zone] = Array()

  pinController !("registerHandler", self) // register the controller for listening
  serverComms !("registerHandler", self)


  override def receive: Receive = {

    // request zone heartbeat from pin controller
    case e @ "Heartbeat" => {
      pinController ! e
      serverComms ! e
    }

    // receive zone heartbeat from pin controller
    case ("Heartbeat", zones: Array[Zone]) => {
      //got a heartbeat of up to date zones from the pinController
      this.zones = zones
      serverComms ! Message(status, MsgType.HeartBeat, zones)
    }

    case "Status" =>
      log.info(s"Status Request received. Responding with Status:$status")
      sender ! status

    case "Arm" => {
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
        serverComms ! ServerCommMessage(s"Pin state changed ${e.getPin.getName}, state ${e.getState.getName}")
      }
    }

    case ("Code",code) => {
      // for handling of a valid code input
      if(code.equals("1373")) {
        log.info("Valid code input")
        if( status == Status.Alarmed) {
          log.info("reseting system")
          status = Status.UnSecure
        }
        if ( status == Status.Secure) {
          log.info("disarming system")
          status = Status.UnSecure
          sender ! status
        }}
      else { log.info(s"Invalid code: ${code}")
        sender ! "Invalid Code"
        serverComms ! ServerCommMessage(s"Invalid Code input.")
      }
    }

    case m@_ => log.error(s"received message dont know what to do with: $m")

  }
}
