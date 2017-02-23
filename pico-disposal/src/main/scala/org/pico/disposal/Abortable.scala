package org.pico.disposal

/** Type class for objects that can be disposed
  */
trait Abortable[A] {
  /** Abort the abortable object.
    *
    * @param a The abortable object
    */
  def abort(a: A): Unit
}

object Abortable {
  def apply[A](f: A => Unit): Abortable[A] = {
    new Abortable[A] {
      override def abort(a: A): Unit = f(a)
    }
  }
}
