package org.pico.disposal

import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import org.specs2.mutable.Specification

class DisposableSpec extends Specification {
  "Disposable" >> {
    "when composed" should {
      "dispose in reverse order" >> {
        var value = 1
        val disposable1 = OnClose(value += 1)
        val disposable2 = OnClose(value *= 10)

        (disposable1 ++ disposable2).dispose()

        value ==== 11
      }
    }

    "dispose in reverse order in nested for comprehension" >> {
      var x = 1

      for (a <- OnClose(x += 1)) {
        for (b <- OnClose(x *= 10)) {
          "Hello world"
        }
      }: String

      x ==== 11
    }

    "dispose in reverse order in shared for comprehension" >> {
      var x = 1

      for (a <- OnClose(x += 1); b <- OnClose(x *= 10)) {
        "Hello World"
      }: String

      x ==== 11
    }
  }
}
