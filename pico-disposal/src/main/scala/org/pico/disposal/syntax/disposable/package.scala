package org.pico.disposal.syntax

import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

import org.pico.disposal.{Disposable, SilencedExceptions}

import scala.util.control.NonFatal

package object disposable {
  implicit class DisposableOps_YYKh2cf[A](val self: A) extends AnyVal {
    /** Dispose the disposable object.  Any exceptions that might be thrown during this process
      * will be caught and silently ignored.
      *
      * @param ev Evidence that A is disposable.
      */
    @inline
    final def dispose()(implicit ev: Disposable[A]): Unit = {
      try {
        ev.dispose(self)
      } catch {
        case NonFatal(e) => SilencedExceptions.publish(e)
      }
    }

    /** Dispose the disposable object allowing any exceptions that might be thrown during this process to
      * propagate.
      *
      * @param ev Evidence that A is disposable.
      */
    @inline
    final def disposeOrFail()(implicit ev: Disposable[A]): Unit = ev.dispose(self)

    /** Create a wrapper for the disposable object that implements Closeable.  Calling close on the
      * wrapper will directly call onDispose on the disposable object.  It does not call the dispose
      * method because the dispose method will silently catch non-fatal exceptions.  Callers may
      * choose this method instead of calling dispose to get access to any thrown exceptions.
      *
      * @param ev Evidence that A is disposable.
      * @return The Closeable wrapper
      */
    @inline
    final def wrapInCloseable(implicit ev: Disposable[A]): Closeable = ev.wrapInCloseable(self)

    /** Get Closeable of the disposable, creating a Closeable wrapper if necessary.  Calling close
      * on the wrapper will directly call onDispose on the disposable object.  It does not call
      * the dispose method because the dispose method will silently catch non-fatal exceptions.
      * Callers may choose this method instead of calling dispose to get access to any thrown
      * exceptions.
      *
      * @param ev Evidence that A is disposable.
      * @return The Closeable wrapper
      */
    @inline
    final def asCloseable(implicit ev: Disposable[A]): Closeable = {
      self.asAutoCloseable match {
        case closeable: Closeable => closeable
        case _                    => wrapInCloseable
      }
    }

    /** Create a wrapper for the disposable object that implements AutoCloseable.  Calling close on
      * the wrapper will directly call onDispose on the disposable object.  It does not call the
      * dispose method because the dispose method will silently catch non-fatal exceptions.  Callers
      * may choose this method instead of calling dispose to get access to any thrown exceptions.
      *
      * @param ev Evidence that A is disposable.
      * @return The Closeable wrapper
      */
    @inline
    final def asAutoCloseable(implicit ev: Disposable[A]): AutoCloseable = ev.asAutoCloseable(self)

    /** Compose two disposable objects into a single Closeable object that when closed will dispose
      * both disposable objects.
      *
      * @param that The disposable object to compose with
      * @param evA Evidence that A is disposable
      * @param evB Evidence that B is disposable
      * @tparam B The type of the disposable object to compose with
      * @return The closeable object that will dispose of both disposable objects when closed
      */
    def :+:[B](that: B)(implicit evA: Disposable[A], evB: Disposable[B]): Closeable = {
      val disposableRefThat = new AtomicReference[B](that)
      val disposableRefThis = new AtomicReference[A](self)

      new Closeable {
        override def close(): Unit = {
          disposableRefThis.getAndSet(null.asInstanceOf[A]).dispose()
          disposableRefThat.getAndSet(null.asInstanceOf[B]).dispose()
        }
      }
    }

    /** Method to enable "for" syntax support for AutoCloseable objects:
      *
      * {{{
      *   for (file <- new FileOutputStream("file.txt")) {
      *     // Do stuff
      *   }
      * }}}
      *
      * The close method on closeable objects initialised this way will be called when the for
      * block scope finishes.
      *
      * @param f The callback that uses the closeable object
      * @tparam B The return type of the callback
      * @return The return type of the callback
      */
    @inline @deprecated("Use Auto instead", "1.0.5")
    final def foreach[B](f: A => B)(implicit ev: Disposable[A]): B = {
      try {
        f(self)
      } finally {
        self.dispose()
      }
    }
  }
}
