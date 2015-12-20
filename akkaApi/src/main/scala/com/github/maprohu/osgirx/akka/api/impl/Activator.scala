package com.github.maprohu.osgirx.akka.api.impl

import _root_.akka.actor.ActorSystem
import _root_.akka.osgi.ActorSystemActivator
import _root_.akka.stream.ActorMaterializer
import com.github.maprohu.osgirx.akka.api._
import com.github.maprohu.osgirx._
import com.github.maprohu.osgirx.core.{RxExecutionContext, RxActivator, MultiActivator}
import org.osgi.framework.{BundleActivator, BundleContext}
import rx.Rx
import rx.core.Obs

import scala.concurrent.ExecutionContext

/**
  * Created by pappmar on 18/12/2015.
  */
class Activator extends MultiActivator(
  RxActivator(RxAkkaContext) { () => Rx( RxActorSystem.ref().map( as => new AkkaContextImpl(as) ) ) },
  RxActivator(RxExecutionContext) { () => Rx( RxAkkaContext.ref().map( _.executiontContext ) ) },
  RxActivator(RxActorMaterializer) { () => Rx( RxAkkaContext.ref().map( _.actorMaterializer ) ) }
)

