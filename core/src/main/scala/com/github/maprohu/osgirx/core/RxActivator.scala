package com.github.maprohu.osgirx.core

import org.osgi.framework.BundleContext
import rx._

/**
  * Created by pappmar on 26/11/2015.
  */
abstract class RxActivator[T](target: RxRef[T]) extends KillableActivator {

  def create : (Rx[Option[T]], Killable)

  override def startKillable(context: BundleContext): Killable = {
    val (ref, kill) = create
    val unregister = target.register(ref)
    Seq(unregister, kill)
  }

}

object RxActivator {
  def apply[T](target: RxRef[T])(factory: () => (Rx[Option[T]], Killable)) = new RxActivator[T](target) {
    override def create: (Rx[Option[T]], Killable) = factory()
  }

//  implicit def nokill[T](value: Rx[T]) = (value, Killable.Empty)
}


