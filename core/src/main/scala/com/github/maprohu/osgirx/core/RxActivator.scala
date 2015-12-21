package com.github.maprohu.osgirx.core

import com.github.maprohu.osgirx.ref.RxRef
import org.osgi.framework.BundleContext
import rx._

/**
  * Created by pappmar on 26/11/2015.
  */
abstract class RxActivator[T](target: RxRef[T]) extends KillableActivator {
  import RxRefOps._

  def create : (Rx[Option[T]], Killable)

  override def startKillable(context: BundleContext): Killable = {
    val (ref, kill) = create
    val unregister = target.register(ref)

    Seq[Killable](unregister, kill)
  }

}

object RxActivator {
  def apply[T](target: RxRef[T])(factory: () => (Rx[Option[T]], Killable)) = new RxActivator[T](target) {
    override def create: (Rx[Option[T]], Killable) = factory()
  }
}


