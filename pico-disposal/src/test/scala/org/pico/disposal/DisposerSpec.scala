package org.pico.disposal

import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.std.closeable._
import org.pico.disposal.syntax.disposable._
import org.specs2.mutable.Specification
import org.pico.atomic.syntax.std.atomicReference._

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

    "make it easier to handle resources safely in a class setting" >> {
      var log = List.empty[String]

      class TwoFiles extends Disposer {
        val resource1 = this.disposesOrClose(OnClose(log ::= "resource 1 closed"))
        val resource2 = this.disposesOrClose(throw new Exception("Oops"))
        val resource3 = this.disposesOrClose(OnClose(log ::= "resource 3 closed"))
      }

      try {
        new TwoFiles().dispose()
      } catch {
        case e: Exception =>
      }

      log ==== List("resource 1 closed")
    }

    "be able to release AtomicReference[Closeable]" >> {
      var log = List.empty[String]
      val disposer = Disposer()
      val reference = disposer.releases(new AtomicReference[Closeable](OnClose(log ::= "closed")))
      disposer.dispose()
      reference.get() ==== Closed
      log ==== List()
    }

    "be able to release AtomicReference[AutoCloseable]" >> {
      var log = List.empty[String]
      val disposer = Disposer()
      val reference = disposer.releases(new AtomicReference[AutoCloseable](OnClose(log ::= "closed")))
      disposer.dispose()
      reference.get() ==== Closed
      log ==== List()
    }

    "be able to dispose AtomicReference[Closeable]" >> {
      var log = List.empty[String]
      val disposer = Disposer()
      val reference = disposer.disposes(new AtomicReference[Closeable](OnClose(log ::= "closed")))
      disposer.dispose()
      reference.get() ==== Closed
      log ==== List("closed")
    }

    "be able to dispose AtomicReference[AutoCloseable]" >> {
      var log = List.empty[String]
      val disposer = Disposer()
      val reference = disposer.disposes(new AtomicReference[AutoCloseable](OnClose(log ::= "closed")))
      disposer.dispose()
      reference.get() ==== Closed
      log ==== List("closed")
    }
  }
}
