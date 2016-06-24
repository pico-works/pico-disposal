package org.pico.disposal

import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import org.specs2.mutable.Specification

class DisposerSpec extends Specification {
  "Disposer" should {
    "dispose in reverse order" >> {
      var value = 1
      val disposer = Disposer()

      disposer.disposes(OnClose(value += 1))
      disposer.disposes(OnClose(value *= 10))
      disposer.dispose()

      value ==== 11
    }

    "be able to release AtomicReference[Closeable]" >> {
      var log = List.empty[String]
      val disposer = Disposer()
      val reference = disposer.swapReleases(Closed, new AtomicReference[Closeable](OnClose(log ::= "closed")))
      disposer.dispose()
      reference.get() ==== Closed
      log ==== List()
    }

    "be able to release AtomicReference[AutoCloseable]" >> {
      var log = List.empty[String]
      val disposer = Disposer()
      val reference = disposer.swapReleases(Closed, new AtomicReference[AutoCloseable](OnClose(log ::= "closed")))
      disposer.dispose()
      reference.get() ==== Closed
      log ==== List()
    }

    "be able to dispose AtomicReference[Closeable]" >> {
      var log = List.empty[String]
      val disposer = Disposer()
      val reference = disposer.swapDisposes(Closed, new AtomicReference[Closeable](OnClose(log ::= "closed")))
      disposer.dispose()
      reference.get() ==== Closed
      log ==== List("closed")
    }

    "be able to dispose AtomicReference[AutoCloseable]" >> {
      var log = List.empty[String]
      val disposer = Disposer()
      val reference = disposer.swapDisposes(Closed, new AtomicReference[AutoCloseable](OnClose(log ::= "closed")))
      disposer.dispose()
      reference.get() ==== Closed
      log ==== List("closed")
    }
  }
}
