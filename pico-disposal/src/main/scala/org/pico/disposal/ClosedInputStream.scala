package org.pico.disposal

import java.io.InputStream

/** An input stream that is always closed.
  */
object ClosedInputStream extends InputStream {
  override def read(): Int = -1
}
