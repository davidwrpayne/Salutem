package work.payne.salutem.api.models

/**
  * Created by dpayne on 11/24/17.
  */
case class Pin(id: Int,
               name: Option[String],
               gpioName: String) extends Product {

}
