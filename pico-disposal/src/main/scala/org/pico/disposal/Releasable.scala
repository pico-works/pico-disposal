package org.pico.disposal

trait Releasable[A] {
  def release(self: A): Unit
}

object Releasable {
  def apply[A](f: A => Unit): Releasable[A] = {
    new Releasable[A] {
      override def release(self: A): Unit = f(self)
    }
  }
}
