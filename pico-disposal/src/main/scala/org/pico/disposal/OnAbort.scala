package org.pico.disposal

class OnAbort(
    val abort: () => Unit = Noop,
    val close: () => Unit = Noop)

object OnAbort {
  /** Construct a Abortable object which calls the provided callback upon being aborted.
    *
    * @param onAbort The callback to be called when aborted.
    * @return Abortable function that when aborted calls the callback.
    */
  def apply(onAbort: => Unit): OnAbort = new OnAbort(() => onAbort, Noop)

  def apply(onAbort: => Unit, onClose: => Unit): OnAbort = new OnAbort(() => onAbort, () => onClose)

  implicit val abortable_OnAbort_fkyceme = Abortable[OnAbort](_.abort())

  implicit val disposable_OnAbort_fkyceme = Disposable[OnAbort](_.close())
}
