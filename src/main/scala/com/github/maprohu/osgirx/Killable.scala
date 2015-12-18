package com.github.maprohu.osgirx

import rx.Rx

/**
  * Created by pappmar on 09/12/2015.
  */
trait Killable {

  def kill() : Unit

}

object Killable {

  implicit def rxToKillable(rxv: Rx[_]) : Killable = new Killable {
    override def kill(): Unit = rxv.killAll()
  }

  implicit def rxsToKillable(rxs: Seq[Rx[_]]) : Killable = new Killable {
    override def kill(): Unit = rxs.foreach(_.killAll())
  }

  implicit def fnToKillable(fn: () => Unit) : Killable = new Killable {
    override def kill(): Unit = fn()
  }

  implicit def seqToKillable(killables: Seq[Killable]) : Killable = new Killable {
    override def kill(): Unit = killables.foreach( _.kill() )
  }

  def rx[T](rxv: Rx[T])(kill: T => Unit) : Rx[T] = {
    import _root_.rx.ops._
    rxv.reduce { (oldValue, newValue) =>
      kill(oldValue)
      newValue
    }
  }

  def wrap[T](rxv: Rx[T])(implicit ev: T => Killable) : Rx[T] =
    rx(rxv)(ev(_).kill())
}
