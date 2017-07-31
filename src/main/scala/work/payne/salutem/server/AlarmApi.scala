package work.payne.salutem.server

import akka.event.Logging
import spray.routing.{HttpServiceActor, RequestContext, Route}
import work.payne.salutem.models.HealthCheck
import spray.json._

class AlarmApi() extends HttpServiceActor {
  val log = Logging(context.system, this)

  val alarmController = AlarmController()



  override def receive: Receive =
    runRoute(

      healthEndpoint() ~
      webpageResourcesEndpoint() ~

      alarmApiEndpoints()
    )


  def healthController(ctx: RequestContext): Unit = {
    val status = HealthCheck("healthy", api=true, alarm = false)
    import work.payne.salutem.models.MyJsonProtocol._
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
    path("alarm" / Rest ) { pathRest =>
      getFromResource(s"webpage/$pathRest")
    }
  }




  def alarmApiEndpoints(): Route = {
    pathPrefix("api") {

      get {
        path("status") { ctx =>
          alarmController.getStatus(ctx)
        }
      }
    }
  }








}
