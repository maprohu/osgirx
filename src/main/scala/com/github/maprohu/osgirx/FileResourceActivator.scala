package com.github.maprohu.osgirx

import java.io.File
import java.net.URL

import rx.Rx
import sbt.IO

/**
  * Created by pappmar on 18/12/2015.
  */
class FileResourceActivator(archive: URL, target: RxRef[File]) extends RxActivator(target) {

  override def create: (Rx[File], Killable) = {
    val dir = IO.createTemporaryDirectory
    IO.unzipURL(archive, dir)

    (
      Rx(dir),
      () => {
        IO.delete(dir)
      }
    )
  }

}
