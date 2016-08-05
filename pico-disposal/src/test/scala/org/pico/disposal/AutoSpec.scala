package org.pico.disposal

import org.pico.disposal.std.autoCloseable._
import org.specs2.mutable.Specification

class AutoSpec extends Specification {
  "Auto" should {
    "have foreach syntax that disposes argument" in {
      var counter = 0

      for (resource <- Auto(OnClose(counter += 1))) {
        counter ==== 0
      }

      counter ==== 1
    }
  }
}
