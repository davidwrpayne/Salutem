package work.payne.salutem.models

object ApiObjects {
  case class ZoneObject(
    id: Int,
    name: String
  )

  case class BaseStatusObject(
    salt: Option[String],
    saltedCode: Option[String],
    alarmStatus: Option[String],
    zones: Option[List[ZoneObject]],
    requiredZones: Option[List[ZoneObject]],
    error: Option[String]
  )


}
