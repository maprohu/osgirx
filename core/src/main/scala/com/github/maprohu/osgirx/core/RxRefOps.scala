package com.github.maprohu.osgirx.core

import com.github.maprohu.osgirx.ref.{RxRegistration, RxRef}
import rx._

/**
  * Created by pappmar on 25/11/2015.
  */
object RxRefOps {

  implicit class RxRefExtra[T](rxref: RxRef[T]) {

    def register(value : T) : (Var[Option[T]], Killable) = {
      register(Some(value))
    }

    def register(value : Option[T]) : (Var[Option[T]], Killable) = {
      val v = Var(value)
      (v, rxref.register(v))
    }
  }

  implicit def regToKillable(reg: RxRegistration) : Killable = new Killable {
    override def kill(): Unit = reg.unregister()
  }

}
