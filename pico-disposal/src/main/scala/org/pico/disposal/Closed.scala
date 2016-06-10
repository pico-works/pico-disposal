package org.pico.disposal

import java.io.Closeable

/** A singleton object that is an already closed closeable object.
  *
  * It would be used in place of null.  See null object pattern.
  */
object Closed extends Closeable {
  @inline
  final override def close(): Unit = ()
}
