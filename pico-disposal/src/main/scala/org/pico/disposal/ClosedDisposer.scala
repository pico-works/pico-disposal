package org.pico.disposal

import org.pico.disposal.syntax.disposable._

/** A Disposer object that is already closed upon creation.  As a result of being already closed,
  * any disposable objects registered to it will be immediately closed as well.
  *
  * It would be used in place of null.  See null object pattern.
  */
trait ClosedDisposer extends Disposer with Closed {
  @inline
  final def release(): Unit = ()

  override def disposes[D: Disposable](disposable: D): D = {
    disposable.dispose()
    disposable
  }
}

/** A singleton ClosedDisposer object.
  */
object ClosedDisposer extends ClosedDisposer
