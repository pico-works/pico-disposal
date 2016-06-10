package org.pico.disposal.std

import org.pico.atomic.EmptyReferent
import org.pico.disposal.{Closed, Disposable, PoisonedCloseable}

package object autoCloseable {
  /** Evidence that all AutoCloseable objects are disposable.  Disposal is implemented as calling
    * the close method on the AutoCloseable object.
    */
  implicit val disposableAutoCloseable_YYKh2cf = new Disposable[AutoCloseable] {
    protected override def onDispose(a: AutoCloseable): Unit = a.close()

    @inline
    final override def disposablePoisoned(a: AutoCloseable): Boolean = a eq PoisonedCloseable

    @inline
    final override def asAutoCloseable(a: AutoCloseable): AutoCloseable = a
  }

  implicit val emptyReferent_AutoCloseable_po9XBKr = EmptyReferent.define[AutoCloseable](Closed)
}
