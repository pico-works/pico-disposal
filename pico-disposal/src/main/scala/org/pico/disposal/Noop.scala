package org.pico.disposal

object Noop extends (() => Unit) {
  override def apply(): Unit = ()
}
