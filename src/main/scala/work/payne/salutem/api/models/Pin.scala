package work.payne.salutem.api.models

import com.pi4j.io.gpio.GpioPinDigitalInput

/**
  * Created by dpayne on 11/24/17.
  */
case class Pin(id: Int,
               name: Option[String],
               gpioName: String,
               gpioDigitalInput: Option[GpioPinDigitalInput]
) extends Product {

}
