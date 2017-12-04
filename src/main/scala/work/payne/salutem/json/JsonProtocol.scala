package work.payne.salutem.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import work.payne.salutem.api.models.{Alarm, ApiError, Pin, Zone}


// collect your json format instances into a support trait:
trait JsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val pinFormat = jsonFormat3(Pin)
  implicit val zoneFormat = jsonFormat3(Zone)
  implicit val alarmFormat: RootJsonFormat[Alarm] = jsonFormat8(Alarm)
  implicit val errorForamt = jsonFormat3(ApiError)
}