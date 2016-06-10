package org.pico.disposal

import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import org.specs2.mutable.Specification

class PoisonedCloseableSpec extends Specification {
  "PoisonedCloseable when composed with another disposable object" should {
    "Dispose the other object" in {
      var stack = List.empty[String]
      PoisonedCloseable :+: OnClose(stack ::= "Poisoned 1")
      stack must_== List("Poisoned 1")
      OnClose(stack ::= "Poisoned 2") :+: PoisonedCloseable
      stack must_== List("Poisoned 2", "Poisoned 1")
      OnClose(stack ::= "Poisoned 3") :+: PoisonedCloseable :+: OnClose(stack ::= "Poisoned 4")
      stack must_== List("Poisoned 3", "Poisoned 4", "Poisoned 2", "Poisoned 1")
    }
  }
}
