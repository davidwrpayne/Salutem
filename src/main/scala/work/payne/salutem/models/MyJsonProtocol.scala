package work.payne.salutem.models
import spray.json._

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val healthCheckFormat = jsonFormat3(HealthCheck)
}
