package org.pico.disposal

import java.io.Closeable

import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.std.tuple._
import org.specs2.mutable.Specification

class TupleSpec extends Specification {
  "Can dispose tuples" in {
    var value: Int = 0

    for {
      (a, b) <- Auto((OnClose(value += 1), OnClose(value += 2)))
    } {
      identity(a: Closeable)
      identity(a: Closeable)
    }

    value must_=== 3
  }
}
