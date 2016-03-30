package work.payne.salutem

import akka.actor.{ActorRef, Actor}
import akka.event.Logging
import com.pi4j.io.gpio.GpioPinDigitalInput
import com.pi4j.io.gpio.event.{GpioPinListenerDigital, GpioPinDigitalStateChangeEvent, GpioPinListener}

/**
  * Created by david.payne on 3/29/16.
  */
case class PinController(pins: Array[GpioPinDigitalInput]) extends Actor {
  val log = Logging(context.system, this)
  var eventListeners: Array[ActorRef] = Array()

  pins.foreach { x =>
    x.addListener(new GpioPinListenerDigital() {
      def handleGpioPinDigitalStateChangeEvent(event: GpioPinDigitalStateChangeEvent) {
        self ! event
      }
    });
  }


  override def receive: Receive = {
    case "Heartbeat" => {
      val zones = pins.map({ x =>
        Zone(x.getPin.getAddress(),x.getName,x.getState.isHigh)
      })
      sender() ! ("Heartbeat",zones)
    }

    case ("registerHandler",listener: ActorRef) => eventListeners = eventListeners :+ listener

    case e: GpioPinDigitalStateChangeEvent => {   // inform eventlisteners
      eventListeners.foreach({ x =>
        x ! e
      })
    }
    case _ => log.error("received message I don't know how to handle")
  }

}
