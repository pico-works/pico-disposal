package org.pico.disposal

import org.pico.disposal.syntax.disposable._

case class Auto[A](resource: A) extends AnyVal {
  def foreach[B](f: A => B)(implicit ev: Disposable[A]): B = {
    try {
      f(resource)
    } finally {
      resource.dispose()
    }
  }

  def filter(a: A => Boolean): Auto[A] = this

  def withFilter(a: A => Boolean): Auto[A] = this
}
