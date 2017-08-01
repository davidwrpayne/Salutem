package work.payne.salutem.server

import akka.actor.{ActorContext, ActorRef}
import akka.pattern._
import akka.util.Timeout
import spray.http.MediaTypes.`text/html`
import spray.http.{HttpData, HttpEntity, HttpResponse}
import spray.routing.RequestContext
import work.payne.salutem.AlarmActor
import work.payne.salutem.InternalActorMessages.{Code, StatusRequest}
import work.payne.salutem.models.ApiObjects.BaseStatusObject

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * wraps the up ask patterns in methods
  * @param alarmActor
  */
case class AlarmController(alarmActor: ActorRef) {
  implicit val timeout = Timeout(Duration(1,"second"))
  def getStatus()(implicit executionContext: ExecutionContext): Future[BaseStatusObject] = {
    for {
      askResponse <- alarmActor ? StatusRequest()
      response = askResponse.asInstanceOf[String]
    } yield {
      BaseStatusObject(None,None,Some(response),None,None)
    }
  }

  def auth(userCode: String)(implicit executionContext: ExecutionContext): Future[HttpResponse] = {
    for {
      askResponse <- alarmActor ? Code(userCode)
      response = askResponse.asInstanceOf[String]
    }
      yield {
        HttpResponse(entity = HttpEntity(`text/html`, HttpData(s"$response")))
      }
  }

}
