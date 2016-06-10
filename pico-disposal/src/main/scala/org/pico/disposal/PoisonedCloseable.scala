package org.pico.disposal

import java.io.Closeable

object PoisonedCloseable extends Closeable {
  @inline
  final override def close(): Unit = ()
}
