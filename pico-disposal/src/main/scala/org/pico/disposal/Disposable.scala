package org.pico.disposal

import java.io.Closeable

/** Type trait for objects that can be disposed.  An instance is provided for AutoCloseable.
  * Additional instances may be provided for types that are not Closeable, particularly those found
  * in third-party libraries.
  *
  * @tparam A The type of the disposable object
  */
trait Disposable[-A] {
  /** Instances override onDispose to implement disposable.
    *
    * @param a The disposable object
    */
  protected def onDispose(a: A): Unit

  /** Create a wrapper for the disposable object that implements Closeable.  Calling close on the
    * wrapper will directly call onDispose on the disposable object.  It does not call the dispose
    * method because the dispose method will silently catch non-fatal exceptions.  Callers may
    * choose this method instead of calling dispose to get access to any thrown exceptions.
    *
    * @param a The disposable object
    * @return The Closeable wrapper
    */
  def asCloseable(a: A): Closeable = new Closeable {
    override def close(): Unit = onDispose(a)
  }

  /** Dispose the disposable object.  Any non-fatal exceptions thrown during the call are caught
    * and silently ignored.
    *
    * @param a The disposable object
    */
  final def dispose(a: A): Unit = try onDispose(a) catch { case e: Exception => }
}
