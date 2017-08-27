package work.payne.salutem

import work.payne.salutem.Alarm.AlarmStatuses.{Armed, Disarmed}


object Alarm {
  var alarmStatus: AlarmStatus = Armed
  var requiredZones: Seq[Zone] = Seq.empty[Zone]  // need to initialize the alarm system or turn it into a class


  def getStatus = synchronized {
    alarmStatus
  }

  def setStatus(status: AlarmStatus):AlarmStatus = synchronized {
    if ( alarmStatus != status) {
      println(s"SetStatus() Called changing status from ${alarmStatus.name} to ${status.name}")
      alarmStatus = status
    }
    alarmStatus
  }


  def setRequiredZones(newZones: Seq[Zone]): Seq[Zone] = synchronized {
    println(s"setRequiredZones() called: changing zones from ${requiredZones} to ${newZones}")
    requiredZones = newZones
    requiredZones
  }

  def getRequiredZones():Seq[Zone] = synchronized {
    requiredZones
  }


  def authWithSaltedCode(saltedCode: String): Boolean = synchronized {
    saltedCode == "1234"
  }


  abstract case class AlarmStatus(name: String)
  object AlarmStatuses {
    object Armed extends AlarmStatus("armed")
    object Disarmed extends AlarmStatus("disarmed")
    object Timing extends AlarmStatus("timing")
    object Alarmed extends AlarmStatus("alarmed")
  }


}



