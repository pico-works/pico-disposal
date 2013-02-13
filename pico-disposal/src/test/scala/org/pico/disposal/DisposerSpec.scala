package org.pico.disposal

import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import org.specs2.mutable.Specification

class DisposerSpec extends Specification {
  "Disposer" should {
    "dispose in reverse order" >> {
      var value = 1
      val disposer = new Disposer()

      disposer.disposes(OnClose(value += 1))
      disposer.disposes(OnClose(value *= 10))
      disposer.dispose()

      value ==== 11
    }

    "Make it easier to handle resources safely in a class setting" >> {
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
  }
}
