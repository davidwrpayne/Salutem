package work.payne.salutem.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import work.payne.salutem.api.models.{Alarm, ApiError, Pin, Zone}


// collect your json format instances into a support trait:
trait JsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val pinFormat = new RootJsonFormat[Pin] {
    override def read(json: JsValue) = {
      val fields = json.asJsObject.fields


      val id = fields.getOrElse("id", throw new Exception("Missing id key")) match {
        case JsNumber(id) => if (id.isValidInt) id.intValue() else throw new Exception("Invalid value for key id")
        case _ => throw new Exception("Invalid type for key id")
      }

      val name = fields.get("name") match {
        case Some(JsString(name)) => Some(name)
        case None => None
        case _ => throw new Exception("invalid type for key name")
      }

      val gpioName = fields.getOrElse("gpioName", throw new Exception("Missing gpioName key")) match {
        case JsString(gpioName) => gpioName
        case _ => throw new Exception("Invalid type for key gpioName")
      }
      Pin(id,name, gpioName, None)
    }

    override def write(obj: Pin) = {
      val nonOptionalFields: Map[String, JsValue] = Map(
        "id" -> JsNumber(obj.id),
        "gpioName" -> JsString(obj.gpioName)
      )
      val optionalFieldName =  obj.name match {
        case Some(name: String) => Map("name" -> JsString(name))
        case None => Map.empty[String, JsValue]
      }

      val allFields = nonOptionalFields ++ optionalFieldName

      JsObject(allFields)
    }
  }
  implicit val zoneFormat = jsonFormat3(Zone)
  implicit val alarmFormat: RootJsonFormat[Alarm] = jsonFormat8(Alarm)
  implicit val errorForamt = jsonFormat3(ApiError)
}