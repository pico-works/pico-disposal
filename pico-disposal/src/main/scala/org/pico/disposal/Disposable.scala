package org.pico.disposal

import java.io.Closeable

import scala.util.control.NonFatal

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
    * Instances of the type-class may choose to return the underlying Closeable object instead
    * of a wrapper.
    *
    * @param a The disposable object
    * @return The Closeable wrapper
    */
  final def wrapInCloseable(a: A): Closeable = new Closeable {
    override def close(): Unit = onDispose(a)
  }

  /** Create a wrapper for the disposable object that implements AutoCloseable.  Calling close on the
    * wrapper will directly call onDispose on the disposable object.  It does not call the dispose
    * method because the dispose method will silently catch non-fatal exceptions.  Callers may
    * choose this method instead of calling dispose to get access to any thrown exceptions.
    * Instances of the type-class may choose to return the underlying AutoCloseable object instead
    * of a wrapper.
    *
    * @param a The disposable object
    * @return The Closeable wrapper
    */
  def asAutoCloseable(a: A): AutoCloseable = new AutoCloseable {
    override def close(): Unit = onDispose(a)
  }

  /** Dispose the disposable object.  Any non-fatal exceptions thrown during the call are caught
    * and silently ignored.
    *
    * @param a The disposable object
    */
  @inline
  final def dispose(a: A): Unit = try onDispose(a) catch { case NonFatal(e) => }
}

object Disposable {
  /** Use the performDispose side-effecting function to implement Disposable for the given type A.
    *
    * @param performDispose The side-effecting function that performs the dispose operation on type A
    * @tparam A The type of the disposable
    * @return A Disposable instance for type A
    */
  @inline
  final def apply[A](performDispose: A => Unit): Disposable[A] = {
    new Disposable[A] {
      override protected def onDispose(a: A): Unit = performDispose(a)
    }
  }
}
