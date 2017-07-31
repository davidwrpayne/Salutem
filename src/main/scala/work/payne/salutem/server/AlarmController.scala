package work.payne.salutem.server

import akka.actor.ActorContext
import spray.routing.RequestContext

import scala.concurrent.ExecutionContext


case class AlarmController()(implicit context: ActorContext, ec: ExecutionContext) {


  def getStatus(ctx: RequestContext) = {

    for {
      alarmActor <- context.system.actorSelection("/user/AlarmActor").resolveOne()

    }



  }
}
