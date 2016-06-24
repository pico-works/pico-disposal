package org.pico.disposal

import org.specs2.mutable.Specification
import org.pico.disposal.std.autoCloseable._

class ClosedDisposerSpec extends Specification {
  "ClosedDisposer" should {
    "immediately dispose of closeable objects upon registration" in {
      var count = 0
      ClosedDisposer.disposes(OnClose(count += 1))
      count must_=== 1
    }
  }
}
