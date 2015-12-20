import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives
import com.github.maprohu.osgirx.akka.api.{AkkaContextImpl, AkkaHttpActivator, RxActorSystem}
import com.github.maprohu.osgirx.akka.api.impl.Activator
import rx.core.Rx
import scala.concurrent.duration._

/**
  * Created by maprohu on 12/20/15.
  */
object RunHttp extends App {

  val akkaApiActivator = new Activator()
  akkaApiActivator.start(null)

  val actorSystem = ActorSystem()

  RxActorSystem.register(actorSystem)

  import Directives._

  val route = complete { "hello" }

  val akkaContext = new AkkaContextImpl(actorSystem)

  val akkaHttpActivator = new AkkaHttpActivator(akkaContext, Rx(Some(route)))
  akkaHttpActivator.start(null)

  import actorSystem.dispatcher

  actorSystem.scheduler.scheduleOnce(3.seconds)(RxActorSystem.register(ActorSystem()))


}
