package org.pico.disposal

import java.io.Reader

/** An reader that is always closed.
  */
object ClosedReader extends Reader {
  override def close(): Unit = ()

  override def read(cbuf: Array[Char], off: Int, len: Int): Int = -1
}
