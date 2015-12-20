package com.github.maprohu.osgirx.core

import rx._

/**
  * Created by pappmar on 25/11/2015.
  */
trait RxRef[T] {

  private val refs = Var(Seq[Rx[Option[T]]]())
  val ref : Rx[Option[T]] = Rx(refs().headOption.flatMap(_()))


  def register(value : Option[T]) : (Var[Option[T]], Killable) = {
    val v = Var(value)
    (v, register(v))
  }

//  def registerSome(value: Rx[T]) : Killable = {
//    Seq[Killable](register(Rx(Some(value()))), () => value.killAll())
//  }

  def register(value: Rx[Option[T]]) : Killable = this.synchronized {
    refs() = value +: refs()

    () => {
      unregister(value)
      value.killAll()
    }
  }

  private def unregister(value: Rx[Option[T]]) : Unit = this.synchronized {
    refs() = refs() diff Seq(value)
  }

}
