package work.payne.salutem.server

import akka.actor.ActorContext
import akka.event.Logging
import spray.httpx._
import spray.routing.{HttpServiceActor, RequestContext, Route}
import work.payne.salutem.models.HealthCheck
import spray.json._

import scala.concurrent.ExecutionContext

class AlarmApi(alarmController: AlarmController)(implicit ec: ExecutionContext) extends HttpServiceActor {
  val log = Logging(context.system, this)
  import work.payne.salutem.models.MyJsonProtocol._

  override def receive: Receive =
    runRoute(
      healthEndpoint() ~
      webpageResourcesEndpoint() ~
      alarmApiEndpoints()
    )


  def healthController(ctx: RequestContext): Unit = {
    val status = HealthCheck("healthy", api=true, alarm = false)
    ctx.complete(status.toJson.toString())
  }

  def healthEndpoint(): Route = {
    path("health") {
      (get & pathEndOrSingleSlash) { ctx =>
        healthController(ctx)
      }
    }
  }

  def webpageResourcesEndpoint(): Route = {
    path("webpage" / Rest ) { pathRest =>
      getFromResource(s"webpage/$pathRest")
    }
  }


  def alarmApiEndpoints(): Route = {
    pathPrefix("alarm") {
      get {
        path("status") { ctx =>
          ctx.complete(alarmController.getStatus())
        }
      } ~
      post {
        path("auth") { ctx =>
          
          ctx.complete(alarmController.auth("1123"))
        }
      }
    }
  }

}
