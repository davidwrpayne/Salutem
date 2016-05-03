package work.payne.salutem

import spray.json.DefaultJsonProtocol
import spray.json._
/**
  * Created by david.payne on 3/29/16.
  */


case class Message(status: String, msgType: String, zones: List[Zone])





object Status {
  val Secure = "Secure"
  val UnSecure = "UnSecure"
  val Alarmed = "Alarmed"
}

object MsgType {
  val ZoneChange = "ZoneChange"
  val HeartBeat = "HeartBeat"
}

case class Zone(pin: Int, zoneName: String, high: Boolean)

