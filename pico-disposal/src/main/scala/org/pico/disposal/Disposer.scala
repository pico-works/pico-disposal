package org.pico.disposal

import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

import org.pico.atomic.EmptyReferent
import org.pico.atomic.syntax.std.atomicReference._
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._

/** The Disposer disposes disposable objects that are registered with it when it is closed.
  *
  * The disposer is thread-safe, but will not prevent more disposable object from being registered
  * even after it is closed.  Objects registered after a call to close may be disposed by calling
  * close again.
  */
trait Disposer extends Closeable {
  private val disposables = new AtomicReference[Closeable](Closed)

  /** Register a disposable object for disposable by the disposer on close.
    *
    * @param disposable The object to be registered by disposal
    * @tparam D The type of the disposable object
    * @return The disposable object
    */
  @inline
  final def +=[D: Disposable](disposable: D): D = disposes(disposable)

  /** Register a disposable object for disposable by the disposer on close.
    *
    * @param disposable The object to be registered for disposal
    * @tparam D The type of the disposable object
    * @return The disposable object
    */
  @inline
  final def disposes[D: Disposable](disposable: D): D = {
    disposables.update(_ :+: disposable.asCloseable)
    disposable
  }

  /** Register an atomic reference to a disposable object for disposable by the disposer on close.
    * The disposable object must have an instance of HasDisposed in order to produce a value that
    * represents an already disposed resource that can be substituted into the reference.
    *
    * @param reference The reference containing the object to be registered for disposal
    * @tparam D The type of the disposable object
    * @return The disposable object
    */
  @inline
  final def disposes[D: Disposable: EmptyReferent](reference: AtomicReference[D]): AtomicReference[D] = {
    disposables.update(_ :+: OnClose(reference.release().dispose()))
    reference
  }

  /** Register an atomic reference to a disposable object for disposable by the disposer on close.
    * The disposable object must have an instance of HasDisposed in order to produce a value that
    * represents an already disposed resource that can be substituted into the reference.
    *
    * @param reference The reference containing the object to be registered for disposal
    * @tparam D The type of the disposable object
    * @return The disposable object
    */
  @inline
  final def releases[D: EmptyReferent](reference: AtomicReference[D]): AtomicReference[D] = {
    disposables.update(_ :+: OnClose(reference.release()))
    reference
  }

  /** Register a callback to be called by the disposer on close.
    *
    * @param f The action to invoke when on close
    * @return The disposable object
    */
  @inline
  final def onClose(f: => Unit): Unit = disposes(OnClose(f))

  /** Register a disposable object for disposable by the disposer on close.
    *
    * @param disposable The object to be registered by disposal
    * @tparam D The type of the disposable object
    * @return The disposable object
    */
  @inline
  final def disposesOrClose[D: Disposable](disposable: => D): D = {
    try {
      disposes(disposable)
    } catch {
      case e: Exception =>
        this.dispose()
        throw e
    }
  }

  /** Dispose all registered disposable objects.
    */
  @inline
  final override def close(): Unit = disposables.getAndSet(Closed).dispose()
}

object Disposer {
  @inline
  final def apply(): Disposer = new Disposer {}
}
