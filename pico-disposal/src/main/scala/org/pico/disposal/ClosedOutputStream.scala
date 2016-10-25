package org.pico.disposal

import java.io.OutputStream

object ClosedOutputStream extends OutputStream {
  override def write(b: Int): Unit = ()
}
