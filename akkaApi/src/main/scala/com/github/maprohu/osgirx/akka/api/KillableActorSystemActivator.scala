package com.github.maprohu.osgirx.akka.api

import akka.actor.ActorSystem
import akka.osgi.ActorSystemActivator
import com.github.maprohu.osgirx.core.{Killable, RxActivator}
import org.osgi.framework.{BundleActivator, BundleContext}
import rx.Rx


/**
  * Created by pappmar on 18/12/2015.
  */
abstract class KillableActorSystemActivator extends ActorSystemActivator {

  private var killable : Killable = null

  def startKillable(context: BundleContext, system: ActorSystem) : Killable

  override def configure(context: BundleContext, system: ActorSystem): Unit = {
    killable = startKillable(context, system)
  }

  override def stop(context: BundleContext): Unit = {
    try {
      if (killable != null) killable.kill()
    } finally {
      killable = null
    }
    super.stop(context)
  }

}
