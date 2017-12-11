package work.payne.salutem.server

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import work.payne.salutem.AlarmActor
import spray.json._
import work.payne.salutem.api.models.Pin
import work.payne.salutem.json.JsonProtocol

import scala.concurrent.ExecutionContext

class WebServer(alarmActor: ActorRef)(implicit ec: ExecutionContext) extends JsonProtocol {

  implicit val alarmActorRef = alarmActor

  def route(): Route = {
    path("test") {
      get { ctx =>
        val p = Pin(1, Some("frontDoor"),"GPIO-3",None)
        ctx.complete(p.toJson)
      }
    } ~
    pathPrefix("alarm") {
      get { ctx =>
        AlarmActor.getAlarm().flatMap { alarm =>
          ctx.complete(alarm.toJson)
        }
      } ~
      path("disarm") {
        post { ctx =>
          AlarmActor.disarm().flatMap { alarm =>
            ctx.complete(alarm.toJson)
          }
        }
      } ~
      path("arm") {
        post { ctx =>
          AlarmActor.arm().flatMap { alarm =>
            ctx.complete(alarm.toJson)
          }
        }
      }
    }
  }
}
