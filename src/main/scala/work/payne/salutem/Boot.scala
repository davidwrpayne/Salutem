package work.payne.salutem

import java.util.logging.Logger

import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import work.payne.salutem.server.WebServer
import akka.actor.{ActorSystem, Props}
import com.pi4j.io.gpio.{GpioFactory, GpioPinDigitalInput, PinPullResistance, RaspiPin}
import work.payne.salutem.api.models.{Pin, Zone}

object Boot extends App {
  implicit val sys = ActorSystem("SecuritySystem")
  implicit val mat = ActorMaterializer()
  implicit val ec  = sys.dispatcher
  val log = Logger.getGlobal

  log.info("Booting Security System")

  val DeployedOnPi: Boolean = false


  val allPins = if( DeployedOnPi) setupPins() else List.empty[Pin]
  val allPinsZone = Zone(1, "AllPins", pins = List.empty[Pin])

  val allZones = List(
    allPinsZone
  )

  val alarmProps = Props(new AlarmActor(allZones, allPins))
  val alarmActor = sys.actorOf(alarmProps, name = "AlarmActor")

  log.info("Starting WebServer")
  val port = 8080
  val webserver = new WebServer(alarmActor)
  Http().bindAndHandle(webserver.route(),"localhost", port)







  def setupPins() = {
    val controller = GpioFactory.getInstance()
    val pins: List[GpioPinDigitalInput] = List(
      //      controller.provisionDigitalInputPin(RaspiPin.GPIO_01, "GPIO_01", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_02, "GPIO_02", PinPullResistance.PULL_DOWN), // I2C
      controller.provisionDigitalInputPin(RaspiPin.GPIO_03, "GPIO_03", PinPullResistance.PULL_DOWN), // I2C
      controller.provisionDigitalInputPin(RaspiPin.GPIO_04, "GPIO_04", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_05, "GPIO_05", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_06, "GPIO_06", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_07, "GPIO_07", PinPullResistance.PULL_DOWN), // SPI_CE1_n
      controller.provisionDigitalInputPin(RaspiPin.GPIO_08, "GPIO_08", PinPullResistance.PULL_DOWN), // SPI_CE0_N
      controller.provisionDigitalInputPin(RaspiPin.GPIO_09, "GPIO_09", PinPullResistance.PULL_DOWN), // SPI_MISO
      controller.provisionDigitalInputPin(RaspiPin.GPIO_10, "GPIO_10", PinPullResistance.PULL_DOWN), // SPI_MOSI
      controller.provisionDigitalInputPin(RaspiPin.GPIO_11, "GPIO_11", PinPullResistance.PULL_DOWN), // SPI_CLK
      controller.provisionDigitalInputPin(RaspiPin.GPIO_12, "GPIO_12", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_13, "GPIO_13", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_14, "GPIO_14", PinPullResistance.PULL_DOWN), // TXD0
      controller.provisionDigitalInputPin(RaspiPin.GPIO_15, "GPIO_15", PinPullResistance.PULL_DOWN), // RXD0
      controller.provisionDigitalInputPin(RaspiPin.GPIO_16, "GPIO_16", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_17, "GPIO_17", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_18, "GPIO_18", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_19, "GPIO_19", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_20, "GPIO_20", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_21, "GPIO_21", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_22, "GPIO_22", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_23, "GPIO_23", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_24, "GPIO_24", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_25, "GPIO_25", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_26, "GPIO_26", PinPullResistance.PULL_DOWN),
      controller.provisionDigitalInputPin(RaspiPin.GPIO_27, "GPIO_27", PinPullResistance.PULL_DOWN)
    )
    pins
  }


  object SalutemConfig {
    val salutem = ConfigFactory.load().getConfig("salutem")
    val fakePinController = salutem.getBoolean("fake-pin-controller")
  }

}
