package org.pico.disposal

import java.io.Closeable

/** A Closeable object that is already closed upon creation.
  *
  * It would be used in place of null.  See null object pattern.
  */
trait Closed extends Closeable {
  @inline
  final override def close(): Unit = ()
}

/** A singleton Closed object.
  */
object Closed extends Closed
