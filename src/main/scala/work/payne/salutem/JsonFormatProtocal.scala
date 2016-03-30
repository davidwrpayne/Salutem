package work.payne.salutem

import spray.json._
import spray.json.DefaultJsonProtocol._

/**
  * Created by david.payne on 3/30/16.
  */
object JsonFormatProtocal extends DefaultJsonProtocol {

  implicit val zoneFormat = jsonFormat3(Zone)
  implicit val messageFormat = jsonFormat3(Message)


}
