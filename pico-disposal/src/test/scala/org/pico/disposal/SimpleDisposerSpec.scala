package org.pico.disposal

import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import org.specs2.mutable.Specification

class SimpleDisposerSpec extends Specification {
  "SimpleDisposer" should {
    "make it easier to handle resources safely in a class setting" >> {
      var log = List.empty[String]

      class TwoFiles extends SimpleDisposer {
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
