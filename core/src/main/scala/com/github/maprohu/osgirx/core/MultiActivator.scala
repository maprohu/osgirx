package com.github.maprohu.osgirx.core

import org.osgi.framework.{BundleActivator, BundleContext}

/**
  * Created by pappmar on 18/12/2015.
  */
class MultiActivator(items: BundleActivator*) extends BundleActivator {

  override def start(context: BundleContext): Unit = {
    items.foreach( _.start(context) )
  }

  override def stop(context: BundleContext): Unit = {
    items.reverse.foreach( _.stop(context) )
  }

}
