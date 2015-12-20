package com.github.maprohu.osgirx.akka.api

import akka.actor.ActorSystem
import akka.http.scaladsl.{HttpExt, Http}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.github.maprohu.osgirx.core.RxRef

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait AkkaContext {
  implicit val actorSystem : ActorSystem
  implicit val actorMaterializer : ActorMaterializer
  implicit val executiontContext : ExecutionContext
  implicit val http : HttpExt
  implicit val timeout : Timeout = 30.second
}

class AkkaContextImpl(val actorSystem : ActorSystem) extends AkkaContext {
  override implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()(actorSystem)
  override implicit val executiontContext: ExecutionContext = actorSystem.dispatcher
  override implicit val http: HttpExt = Http(actorSystem)
}

/**
  * Created by pappmar on 18/12/2015.
  */
object RxAkkaContext extends RxRef[AkkaContext] {

}
