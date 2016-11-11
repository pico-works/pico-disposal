package org.pico.disposal

import org.pico.disposal.syntax.disposable._

case class Part[A](a: A) {
  def flatMap[B](f: A => B)(implicit ev: Disposable[A]): B = {
    try {
      f(a)
    } catch {
      case e: Throwable =>
        a.dispose()
        throw e
    }
  }

  def map[B: Disposable](f: A => B)(implicit ev: Disposable[A]): B = {
    try {
      f(a)
    } catch {
      case e: Throwable =>
        a.dispose()
        throw e
    }
  }
}
