package work.payne.salutem

import java.util.Date

import akka.actor.{ActorRef, Actor}
import akka.event.Logging
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.metrics.internal.cloudwatch.spi.MetricData
import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient
import com.amazonaws.services.cloudwatch.model.{GetMetricStatisticsRequest, MetricDatum, PutMetricDataRequest}
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.PublishRequest
import spray.json._
import JsonFormats._
import sun.plugin2.message.HeartbeatMessage
import work.payne.salutem.Boot.SalutemConfig
import work.payne.salutem.InternalActorMessages.{ServerCommMessage, RegisterHandler, Heartbeat}
import collection.JavaConverters._

class ServerComms extends Actor {
  val log = Logging(context.system, this)
  var eventListeners: Array[ActorRef] = Array()

  val awsCreds = new BasicAWSCredentials(SalutemConfig.aws_key_id, SalutemConfig.aws_secret_key)
  val awsCloudWatchClient = new AmazonCloudWatchClient(awsCreds)
  val snsClient = new AmazonSNSClient(awsCreds)
  snsClient.setRegion(Region.getRegion(SalutemConfig.aws_region))


  def receive = {
    case RegisterHandler(listener) => eventListeners = eventListeners :+ listener
    case msg: Message => {
      log.debug(msg.toJson.toString())
    }
    case Heartbeat(None)  =>
      val msg = "heartbeat"
      val topicArn = SalutemConfig.aws_sns_heartbeat_arn
      try {
        val metricData = new PutMetricDataRequest().withNamespace("Salutem")
        val metricDatum = new MetricDatum()
        metricDatum.setMetricName("Heartbeat")
        metricDatum.setValue(1.0)
        metricDatum.setUnit("Count")
        metricDatum.setTimestamp(new Date())
        metricData.withMetricData(metricDatum)
        awsCloudWatchClient.putMetricData(metricData)


//        val request = new GetMetricStatisticsRequest()
//        request.setMetricName("HeartBeat")
//        request.setNamespace("Salutem")
//        request.setStatistics(List("Maximum").asJava)
//        request.setEndTime(new Date())
//        val startTime = new Date()
//
//        startTime.setTime(startTime.getTime() - 3600 * 40)
//        request.setStartTime(startTime)
//
//        request.setPeriod(60)
//        val result = awsCloudWatchClient.getMetricStatistics(request)
//        log.info(result.toString)
      } catch {
        case e: Throwable=> log.error(s"There is an error sending heartbeat to server. ${e.getClass.getName} ${e.getMessage}")
      }
    case ServerCommMessage(message) => {
      val msg = "Security Alert. Zone Breached!: "
      val topicArn = SalutemConfig.aws_sns_arn
      try {
        val publishRequest = new PublishRequest(topicArn, msg + message)
        val publishResult = snsClient.publish(publishRequest)
      } catch {
        case e => log.error(s"There is an error sending security alert to server. ${e.getMessage}")
      }
    }

    case _  => log.info("received unknown message")
  }


}
