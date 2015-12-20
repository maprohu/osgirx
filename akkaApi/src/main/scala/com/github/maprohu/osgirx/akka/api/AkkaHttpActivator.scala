package com.github.maprohu.osgirx.akka.api

import akka.actor.{Props, Actor}
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.server.{Directives, RoutingSettings, Route}
import com.github.maprohu.osgirx.core.{Killable, KillableActivator}
import org.osgi.framework.BundleContext
import rx.Rx
import rx.core.Obs

/**
  * Created by maprohu on 12/20/15.
  */
class AkkaHttpActivator(route: Rx[Option[Route]], interface : String = "0.0.0.0", port: Int = 8999) extends KillableActivator {
  import akka.pattern.ask

  override def startKillable(context: BundleContext): Killable = {
    val akkaContext : Rx[AkkaContext] = ???

    val bindingRx = Rx {
      val ctx = akkaContext()
      import ctx._

      val httpActor = actorSystem.actorOf(Props(classOf[DefaultHttpActor], route, ctx))

      val binding = http.bindAndHandleAsync(
        handler = req => (httpActor ? req).mapTo[HttpResponse],
        interface = interface,
        port = port
      )
      binding.onComplete(b => actorSystem.log.info(b.toString))
      binding
    }

    () => {
      val ctx = akkaContext()
      import ctx._

      val bnd = bindingRx()
      bindingRx.killAll()
      bnd.foreach( _.unbind() )
    }
  }

}

class DefaultHttpActor(routex: Rx[Option[Route]], akkaContext: AkkaContext) extends Actor {

  val obs = Obs(routex) {
    self ! NewRoute(routex())
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    obs.kill()
    super.postStop()
  }

  override def receive: Receive = waiting

  val waiting : Receive = {
    case NewRoute(route) =>
      context.become(
        route.map(
          working(_) orElse waiting
        ).getOrElse(
          waiting
        )
      )
  }

  def working(route: Route) : Receive = {
    import context.dispatcher
    import akka.pattern.pipe

    val flow = createRouteHandler(route)

    {
      case req : HttpRequest =>
        flow(req) pipeTo sender
    }
  }


  def createRouteHandler(route: Route) = {
    import akkaContext.actorMaterializer
    Route.asyncHandler(route)
  }

  case class NewRoute(route: Option[Route])
}

