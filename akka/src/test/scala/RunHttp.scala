import akka.actor.ActorSystem
import akka.http.scaladsl.server.{Route, Directives}
import com.github.maprohu.osgirx.akka.api.{AkkaContextImpl, AkkaHttpActivator, RxActorSystem}
import com.github.maprohu.osgirx.akka.api.impl.Activator
import rx.core.{Var, Rx}
import scala.concurrent.duration._

/**
  * Created by maprohu on 12/20/15.
  */
object RunHttp extends App {
  import com.github.maprohu.osgirx.core.RxRefOps._

  val akkaApiActivator = new Activator()
  akkaApiActivator.start(null)

  val actorSystem = ActorSystem()

  RxActorSystem.register(actorSystem)

  import Directives._

  val route = complete { "hello" }
  val routeRx = Var[Option[Route]](Some(route))

  val akkaContext = new AkkaContextImpl(actorSystem)

  val akkaHttpActivator = new AkkaHttpActivator(akkaContext, routeRx)
  akkaHttpActivator.start(null)

  import actorSystem.dispatcher

  actorSystem.scheduler.scheduleOnce(3.seconds){println("new actorsystem"); RxActorSystem.register(ActorSystem())}
  actorSystem.scheduler.scheduleOnce(5.seconds){println("remove route"); routeRx() = None}
  actorSystem.scheduler.scheduleOnce(7.seconds){println("new route"); routeRx() = Some(complete("boo"))}
  actorSystem.scheduler.scheduleOnce(9.seconds) {
    println("new akka http activator");
    akkaHttpActivator.stop(null)

    val akkaHttpActivator2 = new AkkaHttpActivator(akkaContext, routeRx)
    akkaHttpActivator2.start(null)
  }


}
