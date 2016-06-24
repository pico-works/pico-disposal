package org.pico.disposal

import java.io.Closeable
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import org.pico.atomic.syntax.std.atomicBoolean._
import org.pico.atomic.syntax.std.atomicReference._
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._

/** A simple trait that other types can inherit to acquire the ability to track disposable objects
  * for disposal upon close.
  */
trait SimpleDisposer extends Disposer {
  private val closed = new AtomicBoolean(false)
  private val disposables = new AtomicReference[Closeable](Closed)

  @inline
  final override def disposes[D: Disposable](disposable: D): D = {
    disposables.update(_ :+: disposable.asCloseable)

    if (closed.value) {
      // It is possible that the object was closed the first `closed` test.  If that's the
      // case we want to ensure that the `disposable` argument is also closed.
      disposables.getAndSet(Closed).dispose()
    }

    disposable
  }

  @inline
  final override def close(): Unit = {
    closed.set(true)
    disposables.getAndSet(Closed).dispose()
  }
}
