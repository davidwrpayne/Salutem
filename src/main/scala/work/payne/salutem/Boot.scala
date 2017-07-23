package work.payne.salutem

import java.util.logging.Logger

import akka.io.IO
import com.typesafe.config.ConfigFactory
//import com.amazonaws.regions.{Regions, Region}
import akka.actor.{ActorSystem, Props}
import com.pi4j.io.gpio.{GpioFactory, GpioPinDigitalInput, PinPullResistance, RaspiPin}
import spray.can.Http
import work.payne.salutem.InternalActorMessages.Heartbeat
import work.payne.salutem.server.WebPage

import scala.concurrent.duration._

object Boot extends App {
  implicit val sys = ActorSystem("SecuritySystem")
//  implicit val heartbeatScheduler = sys.actorOf(Props(classOf[te]),"HeartBeatScheduler")

  val startWebPage: Boolean = true

  import sys.dispatcher

  val log = Logger.getGlobal

  log.info("Booting Salutem")

  if (startWebPage) {
    launchWebPage()
  }



  var pinProps: Option[Props] = if (!SalutemConfig.fakePinController) {
    val pins = setupPins()
    def pinControllerProps = Props(classOf[PinController], pins)
    Some(pinControllerProps)
  } else {
    def fakePinControllerProps() = Props(classOf[FakePinController])
    Some(fakePinControllerProps())
  }


  // use either fake or real pin controller
  val pinController = sys.actorOf(pinProps.get,"PinControllerActor")

  def alarmProps = Props(classOf[AlarmActor], pinController)

  val alarm = sys.actorOf(alarmProps, "AlarmActor")
  sys.scheduler.schedule(200 milliseconds, 10000 milliseconds, alarm, Heartbeat(None))


  def setupPins() = {
    val controller = GpioFactory.getInstance()
    val pins: Array[GpioPinDigitalInput] = Array(
      controller.provisionDigitalInputPin(RaspiPin.GPIO_27, "Front Door", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_01, "GPIO_01", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_02, "GPIO_02", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_03, "GPIO_03", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_04, "GPIO_04", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_05, "GPIO_05", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_06, "GPIO_06", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_07, "GPIO_07", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_08, "GPIO_08", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_09, "GPIO_09", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_10, "GPIO_10", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_11, "GPIO_11", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_12, "GPIO_12", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_13, "GPIO_13", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_14, "GPIO_14", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_15, "GPIO_15", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_16, "GPIO_16", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_17, "GPIO_17", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_18, "GPIO_18", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_19, "GPIO_19", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_20, "GPIO_20", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_21, "GPIO_21", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_22, "GPIO_22", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_23, "GPIO_23", PinPullResistance.PULL_DOWN)
    )
    pins
  }

  def launchWebPage() = {
    val handler = sys.actorOf(Props[WebPage], name = "webpage")
    IO(Http) ! Http.Bind(handler, interface = "localhost", port = 8010)
  }

  object SalutemConfig {
    val salutem = ConfigFactory.load().getConfig("salutem")
    val fakePinController = salutem.getBoolean("fake-pin-controller")
  }

}
