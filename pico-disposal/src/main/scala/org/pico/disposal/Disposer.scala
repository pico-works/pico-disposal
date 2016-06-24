package org.pico.disposal

import java.io.Closeable
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicLong, AtomicReference}

import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._

/** The Disposer disposes disposable objects that are registered with it when it is closed.
  *
  * The disposer is thread-safe, but will not prevent more disposable object from being registered
  * even after it is closed.  Objects registered after a call to close may be disposed by calling
  * close again.
  */
trait Disposer extends Closeable {
  /** Register a disposable object for disposable by the disposer on close.
    *
    * @param disposable The object to be registered by disposal
    * @tparam D The type of the disposable object
    * @return The disposable object
    */
  @inline
  final def +=[D: Disposable](disposable: D): D = disposes(disposable)

  /** Register a disposable object for disposable by the disposer on close.  If the disposer is
    * already closed, then the disposable object will also be closed upon return.
    *
    * @param disposable The object to be registered for disposal
    * @tparam D The type of the disposable object
    * @return The disposable object
    */
  @inline
  def disposes[D: Disposable](disposable: D): D

  /** Register an atomic reference for disposal on close.  The value type of AtomicReference must
    * be Disposable.  When the disposer is closed, the replacement value is swapped in and the
    * swapped out disposable object is disposed.
    *
    * @param replacement The replacement value to use when swapping
    * @param reference The reference to swap
    * @tparam D The type of the disposable object
    * @return The disposable object
    */
  @inline
  final def swapDisposes[D: Disposable](replacement: D, reference: AtomicReference[D]): AtomicReference[D] = {
    this.disposes(OnClose(reference.getAndSet(replacement).dispose()))
    reference
  }

  /** Register an atomic reference for release on close.  When the disposer is closed, the
    * replacement value is swapped in.
    *
    * @param replacement The replacement value to use when swapping
    * @param reference The reference to swap
    * @tparam V The type of the value
    * @return The disposable object
    */
  @inline
  final def swapReleases[V](replacement: V, reference: AtomicReference[V]): AtomicReference[V] = {
    this.disposes(OnClose(reference.set(replacement)))
    reference
  }

  /** Register an reference to keep until closed.
    *
    * @param reference The reference to keep
    * @tparam V The type of the reference
    * @return The disposable object
    */
  @inline
  final def releases[V <: AnyRef](reference: V): V = {
    this.disposes(new Closeable {
      @volatile var ref: AnyRef = reference
      override def close(): Unit = ref = null
    })
    reference
  }

  /** Register an atomic reference for reset on close.  When the disposer is closed, the
    * replacement value is swapped in.
    *
    * @param replacement The replacement value to use when swapping
    * @param reference The reference to swap
    * @tparam V The type of the value
    * @return The disposable object
    */
  @inline
  final def resets[V](replacement: V, reference: AtomicReference[V]): AtomicReference[V] = {
    this.disposes(OnClose(reference.set(replacement)))
    reference
  }

  /** Register an atomic boolean for reset on close.  When the disposer is closed, the
    * replacement value is swapped in.
    *
    * @param replacement The replacement value to use when swapping
    * @param atomicValue The atomic value to swap
    * @return The disposable object
    */
  @inline
  final def resets(replacement: Boolean, atomicValue: AtomicBoolean): AtomicBoolean = {
    this.disposes(OnClose(atomicValue.set(replacement)))
    atomicValue
  }

  /** Register an atomic integer for reset on close.  When the disposer is closed, the
    * replacement value is swapped in.
    *
    * @param replacement The replacement value to use when swapping
    * @param atomicValue The atomic value to swap
    * @return The disposable object
    */
  @inline
  final def resets(replacement: Int, atomicValue: AtomicInteger): AtomicInteger = {
    this.disposes(OnClose(atomicValue.set(replacement)))
    atomicValue
  }

  /** Register an atomic long for reset on close.  When the disposer is closed, the
    * replacement value is swapped in.
    *
    * @param replacement The replacement value to use when swapping
    * @param atomicValue The atomic value to swap
    * @return The disposable object
    */
  @inline
  final def resets(replacement: Long, atomicValue: AtomicLong): AtomicLong = {
    this.disposes(OnClose(atomicValue.set(replacement)))
    atomicValue
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
}

object Disposer {
  @inline
  final def apply(): Disposer = new SimpleDisposer {}
}
