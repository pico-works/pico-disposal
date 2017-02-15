package org.pico.disposal

import java.util.concurrent.atomic.AtomicReference
import org.pico.atomic.syntax.std.atomicReference._

object SilencedExceptions {
  private val subscriptions = new AtomicReference(List.empty[Throwable => Unit])

  def subscribe(f: Throwable => Unit): Unit = subscriptions.update(f :: _)

  def publish(t: Throwable): Unit = {
    subscriptions.get.foreach { subscription =>
      subscription(t)
    }
  }
}
