package work.payne.salutem

import java.util.logging.Logger


import akka.io.IO
import com.pi4j.io.gpio.{GpioPinDigitalInput, PinPullResistance, RaspiPin, GpioFactory}
import spray.can.Http
import akka.actor.{ActorSystem, Props}
import work.payne.salutem.server.WebPage
import scala.concurrent.duration._

/**
  * Created by david.payne on 3/28/16.
  */
object Boot extends App {
  implicit val sys = ActorSystem("SecuritySystem")

  val startWebPage: Boolean = true
  val fakePinController: Boolean = true

  import sys.dispatcher

  val log = Logger.getGlobal

  log.info("Booting Salutem")

  if (startWebPage) {
    launchWebPage()
  }



  var pinProps: Option[Props] = None

  if (!fakePinController) {
    val pins = setupPins()
    def pinControllerProps = Props(classOf[PinController], pins)
    pinProps = Some(pinControllerProps)
  } else {
    def fakePinControllerProps() = Props(classOf[FakePinController])
    pinProps = Some(fakePinControllerProps())
  }
  val pinController = sys.actorOf(pinProps.get)


  def serverCommsProps = Props(classOf[ServerComms])

  val serverComms = sys.actorOf(serverCommsProps)

  def alarmProps = Props(classOf[Alarm], pinController, serverComms)

  val alarm = sys.actorOf(alarmProps, "AlarmActor")
  sys.scheduler.schedule(200 milliseconds, 1000 milliseconds, alarm, "Heartbeat")

  def setupPins() = {
    val controller = GpioFactory.getInstance()
    val pins: Array[GpioPinDigitalInput] = Array(
      controller.provisionDigitalInputPin(RaspiPin.GPIO_01, "Front Door", PinPullResistance.PULL_DOWN)
    )
    pins
  }

  def launchWebPage() = {
    val handler = sys.actorOf(Props[WebPage], name = "webpage")
    IO(Http) ! Http.Bind(handler, interface = "localhost", port = 8080)
  }

}
