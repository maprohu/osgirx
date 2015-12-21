package com.github.maprohu.osgirx.ref

import rx._

/**
  * Created by pappmar on 25/11/2015.
  */
trait RxRef[T] {

  private val refs = Var(Seq[Rx[Option[T]]]())
  val ref : Rx[Option[T]] = Rx(refs().headOption.flatMap(_()))

  def register(value: Rx[Option[T]]) : RxRegistration = this.synchronized {
    refs() = value +: refs()

    new RxRegistration {
      override def unregister(): Unit = {
        refs() = refs() diff Seq(value)
        value.killAll()
      }
    }
  }

}

trait RxRegistration {
  def unregister() : Unit
}
