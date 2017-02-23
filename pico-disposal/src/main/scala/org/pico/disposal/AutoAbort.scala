package org.pico.disposal

import org.pico.disposal.syntax.abortable._
import org.pico.disposal.syntax.disposable._

case class AutoAbort[A](resource: A) extends AnyVal {
  def foreach[B](f: A => B)(implicit ev0: Disposable[A], ev1: Abortable[A]): B = {
    try {
      f(resource)
    } catch {
      case t: Throwable =>
        resource.abort()
        throw t
    } finally {
      resource.dispose()
    }
  }

  def filter(a: A => Boolean): AutoAbort[A] = this

  def withFilter(a: A => Boolean): AutoAbort[A] = this
}
