package work.payne.salutem.server

import java.io.File

import akka.event.Logging

import scala.concurrent.Await
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import spray.can.Http
import spray.can.server.Stats
import spray.util._
import spray.http._
import HttpMethods._
import MediaTypes._
import scala.io._
import spray.can.Http.RegisterChunkHandler

/**
  * Created by david.payne on 3/30/16.
  */
class WebPage extends Actor {
  implicit val timeout: Timeout = 1.second // for the actor 'asks'
  import context.dispatcher

  val log = Logging(context.system, this)
  // ExecutionContext for the futures and scheduler

  def receive = {
    // when a new connection comes in we register ourselves as the connection handler
    case _: Http.Connected => sender ! Http.Register(self)



    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      sender ! getRespFromFile("/html/keypad.html",`text/html`)
    case HttpRequest(GET, Uri.Path("/css/keypad.css"), _, _, _) =>
      sender ! getRespFromFile("/css/keypad.css",`text/css`)
    case HttpRequest(GET, Uri.Path("/controllers/keypad.js"),_,_,_) =>
      sender ! getRespFromFile("/controllers/keypad.js",`application/javascript`)
    case HttpRequest(GET, Uri.Path("/vendor/angular.min.js"),_,_,_) =>
      sender ! getRespFromFile("/vendor/angular.min.js",`application/javascript`)


    case HttpRequest(GET, Uri.Path(path), _, _, _) if path startsWith "/timeout" =>
      log.info("Dropping request, triggering a timeout")

    case HttpRequest(GET, Uri.Path("/alarm/status"),_,_,_) => {
      val actorRef = Await.result(context.system.actorSelection("/user/AlarmActor").resolveOne(1 second),2 second)
      val response = Await.result(actorRef ? "Status",2 seconds)
      sender ! HttpResponse(entity = HttpEntity(`text/html`,HttpData(s"$response")))

    }



    case HttpRequest(POST, Uri.Path("/alarm/auth"), _,entity,_) => {
//      if( entity.data.toString)
      val userCode: String = entity.data.asString(HttpCharsets.`UTF-8`)
      log.info(s"received code for auth: ${userCode}")

      val actorRef = Await.result(context.system.actorSelection("/user/AlarmActor").resolveOne(1 second),2 second)
      val response = Await.result(actorRef ? ("Code",userCode),2 seconds)
      sender ! HttpResponse(entity = HttpEntity(`text/html`,HttpData(response.toString)))
    }
    case HttpRequest(POST, Uri.Path("/alarm/arm"), _, _, _) => {
      log.info(s"received arm request ")
      val actorRef = Await.result(context.system.actorSelection("/user/AlarmActor").resolveOne(1 second),2 second)
      val response = Await.result(actorRef ? "Arm",2 seconds)
      sender ! HttpResponse(entity = HttpEntity(`text/html`,HttpData(response.toString)))

    }
//

    case _: HttpRequest => sender ! HttpResponse(status = 404, entity = "Unknown resource!")

    case Timedout(HttpRequest(_, Uri.Path("/timeout/timeout"), _, _, _)) =>
      log.info("Dropping Timeout message")

    case Timedout(HttpRequest(method, uri, _, _, _)) =>
      sender ! HttpResponse(
        status = 500,
        entity = "The " + method + " request to '" + uri + "' has timed out..."
      )


    //
    //    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
    //      sender ! HttpResponse(entity = "PONG!")
    //
    //    case HttpRequest(GET, Uri.Path("/crash"), _, _, _) =>
    //      sender ! HttpResponse(entity = "About to throw an exception in the request handling actor, " +
    //        "which triggers an actor restart")
    //      sys.error("BOOM!")
    //

    //    case HttpRequest(GET, Uri.Path("/stop"), _, _, _) =>
    //      sender ! HttpResponse(entity = "Shutting down in 1 second ...")
    //      sender ! Http.Close
    //      context.system.scheduler.scheduleOnce(1.second) {
    //        context.system.shutdown()
    //      }
  }

  import spray.routing._

  ////////////// helpers //////////////
  def getRespFromFile(src: String, entityType: ContentType): HttpResponse = {
    val file = new File(s"./res$src")
    val data = HttpData(file)
    lazy val resp = HttpResponse(entity = HttpEntity(entityType,data))
    resp
  }

}
