package org.pico.disposal

case class Eval[A](resource: A) extends AnyVal {
  def foreach[B](f: A => B): B = f(resource)
}
