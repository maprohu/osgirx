package com.github.maprohu.osgirx.akka.impl

import akka.actor.ActorSystem
import akka.osgi.ActorSystemActivator
import com.github.maprohu.osgirx.akka.api.{KillableActorSystemActivator, RxActorSystem, RxActorMaterializer}
import com.github.maprohu.osgirx.core.{Killable, RxActivator}
import org.osgi.framework.{BundleActivator, BundleContext}
import rx.Rx


/**
  * Created by pappmar on 18/12/2015.
  */
class Activator extends KillableActorSystemActivator {

  override def startKillable(context: BundleContext, system: ActorSystem): Killable = {
    val delegate = RxActivator(RxActorSystem) { () => Rx(Some(system)) }
    delegate.start(context)

    () => delegate.stop(context)
  }
}
