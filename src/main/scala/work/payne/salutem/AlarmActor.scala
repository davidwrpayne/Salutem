package work.payne.salutem

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import work.payne.salutem.AlarmActor.Methods._
import work.payne.salutem.api.models._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by dpayne on 11/24/17.
  */
class AlarmActor(initialZones: List[Zone], initialPins: List[Pin]) extends Actor {

  var alarm = Alarm(
    alarmStatus = AlarmStatuses.Disarmed,
    alarmTime = None,
    zonesRequired = initialZones,
    allZones = initialZones,
    openPins = List.empty[Pin],
    closedPins = List.empty[Pin],
    allPins = List.empty[Pin],
    timestamp = System.currentTimeMillis()
  )

  override def receive: Receive = {
    case GetAlarm => sender ! alarm
    case SetAlarm(a) => this.alarm = a; sender ! this.alarm
    case Disarm => this.alarm = this.alarm.copy(alarmStatus = AlarmStatuses.Disarmed); sender ! this.alarm
    case Arm => this.alarm = this.alarm.copy(alarmStatus = AlarmStatuses.Armed); sender ! this.alarm
    case e@_ => println(s"AlarmActor received an unknown message ${e.toString}"); sender ! this.alarm // TODO send error object instead?
  }
}


object AlarmActor {
  implicit val timeout = akka.util.Timeout(300, TimeUnit.MILLISECONDS)

  def getAlarm()(implicit ec: ExecutionContext, actorRef: ActorRef): Future[Alarm] = {
    actorRef.ask(GetAlarm).map{_.asInstanceOf[Alarm]}
  }

  def setAlarm(alarm:Alarm)(implicit ec: ExecutionContext, actorRef: ActorRef): Future[Alarm] = {
    actorRef.ask(SetAlarm(alarm)).map{_.asInstanceOf[Alarm]}
  }

  def disarm()(implicit  ec: ExecutionContext, actorRef: ActorRef): Future[Alarm] = {
    actorRef.ask(Disarm).map{_.asInstanceOf[Alarm]}
  }

  def arm()(implicit  ec: ExecutionContext, actorRef: ActorRef): Future[Alarm] = {
    actorRef.ask(Arm).map{_.asInstanceOf[Alarm]}
  }

  object Methods {
    sealed trait AlarmActorMethods

    case class GetAlarm() extends AlarmActorMethods
    case class SetAlarm(alarm: Alarm) extends AlarmActorMethods
    case class Disarm() extends AlarmActorMethods
    case class Arm() extends AlarmActorMethods
  }


}
