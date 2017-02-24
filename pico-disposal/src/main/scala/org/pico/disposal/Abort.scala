package org.pico.disposal

import org.pico.disposal.syntax.abortable._

case class Abort[A](resource: A) extends AnyVal {
  @inline
  final def foreach[B](f: A => B)(implicit ev: Abortable[A]): B = {
    try {
      f(resource)
    } catch {
      case t: Throwable =>
        resource.abort()
        throw t
    }
  }
}
