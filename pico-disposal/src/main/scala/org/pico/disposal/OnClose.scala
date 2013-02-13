package org.pico.disposal

import java.io.Closeable

object OnClose {
  /** Construct a Closeable object which calls the provided callback upon being closed.
    *
    * @param f The callback to be called when closed.
    * @return Closeable function that when closed calls the callback.
    */
  def apply(f: => Unit): Closeable = {
    new Closeable {
      override def close(): Unit = f
    }
  }
}
