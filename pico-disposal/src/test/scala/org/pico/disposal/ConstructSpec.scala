package org.pico.disposal

import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import org.specs2.mutable.Specification

class ConstructSpec extends Specification {
  "Construct" should {
    "not dispose constructor resources on successful construction" in {
      var counter = List.empty[Int]

      val resource = Construct { constructor =>
        constructor.onClose(counter ::= 1)
        constructor.onClose(counter ::= 2)

        Disposer()
      }

      counter ==== List.empty

      resource.dispose()

      counter.reverse ==== List(2, 1)
    }

    "dispose constructor resources on unsuccessful construction" in {
      var counter = List.empty[Int]

      Construct[Disposer] { constructor =>
        constructor.onClose(counter ::= 1)
        constructor.onClose(counter ::= 2)

        throw new Exception()
      } must throwA[Exception]

      counter.reverse ==== List(2, 1)
    }
  }
}
