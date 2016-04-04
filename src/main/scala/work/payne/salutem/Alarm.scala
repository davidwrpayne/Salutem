package work.payne.salutem

import akka.actor.{ActorRef, Actor}
import akka.actor.Actor.Receive
import akka.event.Logging
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent


/**
  * Created by david.payne on 3/29/16.
  */
class Alarm(pinController: ActorRef, serverComms: ActorRef) extends Actor{
  val log = Logging(context.system, this)
  var status: String = Status.DisArmed
  var zones: Array[Zone] = Array()

  pinController ! ("registerHandler",self)  // register the controller for listening
  serverComms ! ("registerHandler",self)


  override def receive: Receive = {

    case "Status" => sender ! status
    case e @ "Heartbeat" => pinController ! e
    case ("Heartbeat",zones: Array[Zone]) => {

      //got a heartbeat of up to date zones from the pinController
      this.zones = zones
      serverComms ! Message(status, MsgType.HeartBeat, zones)
    }

    case e : GpioPinDigitalStateChangeEvent => {
      log.info(s"got state change for pin ${e.getPin.getName}, state ${e.getState.getName}")
    }
    case _ => log.error("received message dont know what to do with")

  }
}
