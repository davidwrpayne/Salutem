package work.payne.salutem.models
import spray.httpx.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsonFormat}
import work.payne.salutem.models.ApiObjects.{BaseStatusObject, ZoneObject}

object MyJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val healthCheckFormat = jsonFormat3(HealthCheck)
  implicit val zoneObjectFormat = jsonFormat2(ZoneObject)
  implicit val baseStatusObjectFormat = jsonFormat5(BaseStatusObject)
}
