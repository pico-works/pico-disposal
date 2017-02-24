package org.pico.disposal.syntax

import org.pico.disposal.Releasable

package object releasable {
  implicit class ReleasableOps_WMuALPy[A](val self: A) extends AnyVal {
    def release()(implicit ev: Releasable[A]): Unit = ev.release(self)
  }
}
