package org.pico.disposal

import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._

object Construct {
  def apply[A <: Disposer](f: Disposer => A): A = {
    val disposer = Disposer()

    try {
      val resource = f(disposer)

      resource.disposes(disposer.release())

      resource
    } finally {
      disposer.dispose()
    }
  }
}
