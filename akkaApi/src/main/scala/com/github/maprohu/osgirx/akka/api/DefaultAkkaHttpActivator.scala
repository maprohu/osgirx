package com.github.maprohu.osgirx.akka.api

import akka.actor.ActorSystem
import com.github.maprohu.osgirx.core.Killable
import org.osgi.framework.BundleContext

/**
  * Created by maprohu on 12/20/15.
  */
class DefaultAkkaHttpActivator extends KillableActorSystemActivator{
  override def startKillable(context: BundleContext, system: ActorSystem): Killable = {
    val delegate = new AkkaHttpActivator(new AkkaContextImpl(system))
    delegate.start(context)

    () => delegate.stop(context)
  }
}
