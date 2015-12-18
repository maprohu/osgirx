package com.github.maprohu.osgirx

import org.osgi.framework.{BundleActivator, BundleContext}
import rx._

/**
  * Created by pappmar on 26/11/2015.
  */
abstract class RxActivator[T](target: RxRef[T]) extends KillableActivator {

  def create : (Rx[T], Killable)

  def nokill(value: Rx[T]) = (value, Killable.Empty)

  override def startKillable(context: BundleContext): Killable = {
    val (ref, kill) = create
    target.register(ref)

    () => {
      target.unregister(ref)
      ref.killAll()
      kill.kill()
    }
  }

}
