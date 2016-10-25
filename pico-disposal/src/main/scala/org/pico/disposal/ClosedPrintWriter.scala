package org.pico.disposal

import java.io.PrintWriter

object ClosedPrintWriter extends PrintWriter(ClosedOutputStream)
