package com.github.maprohu.osgirx.core

import org.osgi.framework.{BundleActivator, BundleContext}

/**
  * Created by pappmar on 18/12/2015.
  */
abstract class KillableActivator extends BundleActivator {

  private var killable : Killable = null

  override def start(context: BundleContext): Unit = {
    killable = startKillable(context)
  }

  def startKillable(context: BundleContext) : Killable

  override def stop(context: BundleContext): Unit = {
    try {
      if (killable != null) killable.kill()
    } finally {
      killable = null
    }
  }

}
