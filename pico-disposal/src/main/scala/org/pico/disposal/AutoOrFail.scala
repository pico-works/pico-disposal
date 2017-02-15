package org.pico.disposal

import org.pico.disposal.syntax.disposable._

case class AutoOrFail[A](resource: A) extends AnyVal {
  def foreach[B](f: A => B)(implicit ev: Disposable[A]): B = {
    try {
      f(resource)
    } finally {
      resource.disposeOrFail()
    }
  }

  def filter(a: A => Boolean): AutoOrFail[A] = this

  def withFilter(a: A => Boolean): AutoOrFail[A] = this
}
