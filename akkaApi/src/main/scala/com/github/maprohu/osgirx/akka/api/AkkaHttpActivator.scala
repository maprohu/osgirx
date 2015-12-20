package com.github.maprohu.osgirx.akka.api

import akka.actor.{ActorSystem, Props, Actor}
import akka.http.scaladsl.Http.{IncomingConnection, ServerBinding}
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.server.{Directives, RoutingSettings, Route}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Source, Keep, Sink, Flow}
import com.github.maprohu.osgirx.core.{Killable, KillableActivator}
import org.osgi.framework.BundleContext
import rx.Rx
import rx.core.Obs

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration

/**
  * Created by maprohu on 12/20/15.
  */
class AkkaHttpActivator(akkaContext: AkkaContext, routeRx: Rx[Option[Route]], interface : String = "0.0.0.0", port: Int = 8999) extends KillableActivator {

  import Directives._
  import rx.ops._

  val noRoute = complete { "no route" }

  @volatile
  var currentRoute : Route = noRoute

  val baseRoute : Route = rc => currentRoute(rc)

  def bind(implicit ctx: AkkaContext) : Future[ServerBinding] = {
    import ctx._
    val bindingResult = http.bindAndHandle(baseRoute, interface, port)
    bindingResult.onComplete(println(_))
    bindingResult
  }

  def unbind(binding: Future[ServerBinding])(implicit ctx: AkkaContext) : Future[Unit] = {
    import ctx._
    binding.map( _.unbind() )
  }

  override def startKillable(context: BundleContext): Killable = {
    val routeObs = Obs(routeRx) {
      currentRoute = routeRx().getOrElse(noRoute)
    }

    import akkaContext._
    import scala.async.Async._
    var bindingOpt = Option.empty[ServerBinding]
    val ctxQueue = Source.queue[Option[AkkaContext]](10, OverflowStrategy.fail).mapAsync(1)({ ctxOpt =>
      async {
        await( bindingOpt.map( _.unbind() ).getOrElse(Future()))
        bindingOpt = await( ctxOpt.map( implicit ctx => bind.map(Some(_)) ).getOrElse(Future(Option.empty[ServerBinding])) )
      }
    }).to(Sink.ignore).run()

    val ctxObs = Obs(RxAkkaContext.ref) {
      ctxQueue.offer(RxAkkaContext.ref())
    }

    Seq[Killable](
      routeObs,
      () => bindingOpt.foreach( binding => Await.ready( binding.unbind(), Duration.Inf ) ),
      ctxObs
    )

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

