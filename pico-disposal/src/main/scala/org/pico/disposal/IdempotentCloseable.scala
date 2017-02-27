package org.pico.disposal

import java.io.Closeable
import java.util.concurrent.atomic.AtomicBoolean

/** Inherit this trait to make close idempotent.  Be sure to put this first in the inheritance list.
  */
trait IdempotentCloseable extends Closeable {
  private val openRef = new AtomicBoolean(true)

  abstract override def close(): Unit = {
    if (openRef.getAndSet(false)) {
      super.close()
    }
  }
}
