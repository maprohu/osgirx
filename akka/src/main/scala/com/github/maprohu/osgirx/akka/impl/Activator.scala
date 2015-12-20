package com.github.maprohu.osgirx.akka.impl

import akka.actor.ActorSystem
import akka.osgi.ActorSystemActivator
import com.github.maprohu.osgirx.akka.api.{RxActorSystem, RxActorMaterializer}
import com.github.maprohu.osgirx.core.RxActivator
import org.osgi.framework.{BundleActivator, BundleContext}
import rx.Rx


/**
  * Created by pappmar on 18/12/2015.
  */
class Activator extends ActorSystemActivator {

  var delegateOpt : Option[BundleActivator] = None

  override def configure(context: BundleContext, system: ActorSystem): Unit = {
    val delegate = RxActivator(RxActorSystem) { () => Rx(Some(system)) }
    delegate.start(context)
    delegateOpt = Some(delegate)
  }

  override def stop(context: BundleContext): Unit = {
    delegateOpt.foreach( _.stop(context) )
    delegateOpt = None
    super.stop(context)
  }

}
