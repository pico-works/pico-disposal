package org.pico.disposal

import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

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

  /** Register a disposable object for disposable by the disposer.
    *
    * @param disposable The object to be registered by disposal
    * @tparam D The type of the disposable object
    * @return The disposable object
    */
  final def +=[D: Disposable](disposable: D): D = disposes(disposable)

  /** Register a disposable object for disposable by the disposer.
    *
    * @param disposable The object to be registered by disposal
    * @tparam D The type of the disposable object
    * @return The disposable object
    */
  final def disposes[D: Disposable](disposable: D): D = {
    disposables.update(_ ++ disposable.asCloseable)
    disposable
  }

  /** Register a disposable object for disposable by the disposer.
    *
    * @param disposable The object to be registered by disposal
    * @tparam D The type of the disposable object
    * @return The disposable object
    */
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
  final override def close(): Unit = disposables.getAndSet(Closed).dispose()
}

object Disposer {
  def apply(): Disposer = new Disposer {}
}
