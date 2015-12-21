package com.github.maprohu.osgirx.core

import java.io.{InputStream, File}
import java.net.URL

import com.github.maprohu.osgirx.ref.RxRef
import rx.Rx
import sbt.io.IO

/**
  * Created by pappmar on 18/12/2015.
  */
class FileResourceActivator(archive: () => InputStream, target: RxRef[File]) extends RxActivator(target) {

  override def create: (Rx[Option[File]], Killable) = {
    val dir = IO.createTemporaryDirectory
    IO.unzipStream(archive(), dir)

    (
      Rx(Some(dir)),
      () => {
        IO.delete(dir)
      }
    )
  }

}
