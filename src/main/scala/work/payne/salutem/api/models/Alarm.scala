package work.payne.salutem.api.models

case class Alarm(alarmStatus: String,
                 alarmTime: Option[Int],
                 zonesRequired: List[Zone],
                 allZones: List[Zone],
                 openPins: List[Pin],
                 closedPins: List[Pin],
                 allPins: List[Pin],
                 timestamp: Long// might not be neccessary?  how could a pin be in a unclosed unopen state?
                ) extends Product {
}


object AlarmStatuses {
  val Alarmed: String = "alarmed"
  val Disarmed: String = "disarmed"
  val Armed: String = "armed"
  val AlarmedCountdown: String = "alarmed_countdown"
}

