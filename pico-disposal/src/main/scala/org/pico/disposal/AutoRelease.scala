package org.pico.disposal

import org.pico.disposal.syntax.disposable._
import org.pico.disposal.syntax.releasable._

case class AutoRelease[A](resource: A) extends AnyVal {
  def foreach[B](f: A => B)(implicit ev0: Disposable[A], ev1: Releasable[A]): B = {
    try {
      val result = f(resource)

      resource.release()

      result
    } finally {
      resource.dispose()
    }
  }

  def filter(a: A => Boolean): AutoRelease[A] = this

  def withFilter(a: A => Boolean): AutoRelease[A] = this
}
