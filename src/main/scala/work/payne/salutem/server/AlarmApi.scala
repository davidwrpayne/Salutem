package work.payne.salutem.server

import spray.routing.{HttpServiceActor, RequestContext, Route}
import work.payne.salutem.models.HealthCheck
import spray.json._

class AlarmApi() extends HttpServiceActor {
  override def receive: Receive =
    runRoute(
      healthEndpoint()
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
//        ctx.complete("healthy")
      }
    }
  }
}
