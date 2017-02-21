package org.pico.disposal

import java.io.PrintWriter

/** An print writer that is always closed.
  */
object ClosedPrintWriter extends PrintWriter(ClosedOutputStream)
