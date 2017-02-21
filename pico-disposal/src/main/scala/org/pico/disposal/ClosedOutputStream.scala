package org.pico.disposal

import java.io.OutputStream

/** An output stream that is always closed.
  */
object ClosedOutputStream extends OutputStream {
  override def write(b: Int): Unit = ()
}
