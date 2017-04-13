package work.payne.salutem

import java.util.logging.Logger


import akka.io.IO
import com.amazonaws.regions.{Regions, Region}
import com.pi4j.io.gpio.{GpioPinDigitalInput, PinPullResistance, RaspiPin, GpioFactory}
import spray.can.Http
import akka.actor.{ActorSystem, Props}
import work.payne.salutem.InternalActorMessages.Heartbeat
import work.payne.salutem.server.WebPage
import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by david.payne on 3/28/16.
  */
object Boot extends App {
  implicit val sys = ActorSystem("SecuritySystem")
//  implicit val heartbeatScheduler = sys.actorOf(Props(classOf[te]),"HeartBeatScheduler")

  val startWebPage: Boolean = true
  val fakePinController: Boolean = false

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


  // use either fake or real pin controller
  val pinController = sys.actorOf(pinProps.get,"PinControllerActor")

  def alarmProps = Props(classOf[Alarm], pinController)
  val alarm = sys.actorOf(alarmProps, "AlarmActor")
  sys.scheduler.schedule(200 milliseconds, 1000 milliseconds, alarm, Heartbeat(None))


  def setupPins() = {
    val controller = GpioFactory.getInstance()
    val pins: Array[GpioPinDigitalInput] = Array(
      controller.provisionDigitalInputPin(RaspiPin.GPIO_27, "Front Door", PinPullResistance.PULL_DOWN)
    )
    pins
  }

  def launchWebPage() = {
    val handler = sys.actorOf(Props[WebPage], name = "webpage")
    IO(Http) ! Http.Bind(handler, interface = "localhost", port = 8010)
  }

  object SalutemConfig {
  }

}
