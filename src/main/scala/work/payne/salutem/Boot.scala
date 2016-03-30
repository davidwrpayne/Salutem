package work.payne.salutem

import java.util.logging.Logger

import akka.event.Logging
import com.pi4j.io.gpio.{GpioPinDigitalInput, PinPullResistance, RaspiPin, GpioFactory}
import work.payne.salutem.example.ControlGpioExample
import java.util.concurrent.Executor

import akka.actor.{ActorSystem, Props}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by david.payne on 3/28/16.
  */
object Boot extends App {
  implicit val sys = ActorSystem("SecuritySystem")
  import sys.dispatcher
  val log = Logger.getGlobal

  log.info("Booting Salutem")

  val pins = setupPins()

  def pinControllerProps = Props(classOf[PinController], pins)
  def serverCommsProps = Props(classOf[ServerComms])

  val pinController = sys.actorOf(pinControllerProps)
  val serverComms = sys.actorOf(serverCommsProps)

  def alarmProps = Props(classOf[Alarm],pinController,serverComms)

  val alarm = sys.actorOf(alarmProps)
  sys.scheduler.schedule(200 milliseconds, 1000 milliseconds, alarm, "Heartbeat")



  def setupPins() = {
    val controller = GpioFactory.getInstance()
    val pins: Array[GpioPinDigitalInput] = Array(
      controller.provisionDigitalInputPin(RaspiPin.GPIO_01, "Front Door", PinPullResistance.PULL_DOWN)
    )
    pins
  }
}
