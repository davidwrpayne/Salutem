package work.payne.salutem.server

import akka.actor.ActorRef
import work.payne.salutem.Alarm
import work.payne.salutem.Alarm.AlarmStatuses
import work.payne.salutem.models.ApiObjects.BaseStatusObject

import scala.concurrent.ExecutionContext

/**
  * wraps the up ask patterns in methods
  * @param alarmActor
  */
case class AlarmController(alarmActor: ActorRef) {

  def getStatus()(implicit executionContext: ExecutionContext): BaseStatusObject = {
      val status = Alarm.getStatus
      BaseStatusObject(None,None,Some(status.name),None,None,None)
  }

  def auth(userCode: String)(implicit executionContext: ExecutionContext): BaseStatusObject = {

    val auth = Alarm.authWithSaltedCode("saltedCode")
    var errors = Seq.empty[String]
    if (auth) {
      Alarm.setStatus(AlarmStatuses.Disarmed)
    } else {
      errors ++=  Seq("Unable to Authenticate")
    }
    val status = Alarm.getStatus
    BaseStatusObject(None,None,Some(status.name),None,None,Some(errors.mkString(",")))
  }

}
