package org.pico.disposal.syntax

import org.pico.disposal.Abortable

package object abortable {
  implicit class AbortableOps_iioGpJ2[A](val self: A) extends AnyVal {
    def abort()(implicit ev: Abortable[A]): Unit = ev.abort(self)
  }
}
