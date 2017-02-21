package org.pico.disposal

import java.io.Writer

/** An writer that is always closed.
  */
object ClosedWriter extends Writer {
  override def flush(): Unit = ()

  override def write(cbuf: Array[Char], off: Int, len: Int): Unit = ()

  override def close(): Unit = ()
}
