package org.pico.disposal

import java.io.Closeable
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicLong, AtomicReference}

import org.pico.atomic.syntax.std.atomicBoolean._
import org.pico.atomic.syntax.std.atomicInteger._
import org.pico.atomic.syntax.std.atomicLong._
import org.pico.atomic.syntax.std.atomicReference._
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import org.specs2.execute.Result
import org.specs2.mutable.Specification
import org.specs2.specification.AroundExample
import org.specs2.time.TimeConversions
import org.specs2.specification. AroundExample
import org.specs2.execute.{ EventuallyResults, Result }
import EventuallyResults._
import scala.concurrent.duration._

class DisposerSpec extends Specification {
  sequential

  "Disposer" should {
    "dispose in reverse order" >> {
      var value = 1
      val disposer = Disposer()

      disposer.disposes(OnClose(value += 1))
      disposer +=       OnClose(value *= 10)
      disposer.dispose()

      value ==== 11
    }

    "be able to release AtomicReference[Closeable]" >> {
      var log = List.empty[String]
      val disposer = Disposer()
      val reference = disposer.swapReleases(Closed, new AtomicReference[Closeable](OnClose(log ::= "closed")))
      disposer.dispose()
      reference.value ==== Closed
      log ==== List()
    }

    "be able to release AtomicReference[AutoCloseable]" >> {
      var log = List.empty[String]
      val disposer = Disposer()
      val reference = disposer.swapReleases(Closed, new AtomicReference[AutoCloseable](OnClose(log ::= "closed")))
      disposer.dispose()
      reference.value ==== Closed
      log ==== List()
    }

    "be able to dispose AtomicReference[Closeable]" >> {
      var log = List.empty[String]
      val disposer = Disposer()
      val reference = disposer.swapDisposes(Closed, new AtomicReference[Closeable](OnClose(log ::= "closed")))
      disposer.dispose()
      reference.value ==== Closed
      log ==== List("closed")
    }

    "be able to dispose AtomicReference[AutoCloseable]" >> {
      var log = List.empty[String]
      val disposer = Disposer()
      val reference = disposer.swapDisposes(Closed, new AtomicReference[AutoCloseable](OnClose(log ::= "closed")))
      disposer.dispose()
      reference.value ==== Closed
      log ==== List("closed")
    }

    "be able to close disposable objects registered concurrently with close" in {
      val disposer = Disposer()
      val counter = new AtomicInteger(0)
      val thread = new Thread {
        override def run(): Unit = {
          disposer.disposes(OnClose {
            Thread.sleep(10)
            counter.incrementAndGet()
          })
          Thread.sleep(20)
        }
      }

      thread.start()
      disposer.close()
      thread.join()

      counter.value must_=== 1

      success
    }

    "be able to register side-effects to run upon closing" in {
      val count = new AtomicInteger(0)

      for (disposer <- Disposer()) {
        disposer.onClose(count.incrementAndGet())
      }

      count.value must_=== 1
    }

    "be able to register reset of AtomicLong to a value upon closing" in {
      val disposer = Disposer()
      val count = disposer.resets(0, new AtomicLong(123))
      count.value must_=== 123L
      disposer.close()
      count.value must_=== 0L
    }

    "be able to register reset of AtomicInteger to a value upon closing" in {
      val disposer = Disposer()
      val count = disposer.resets(0, new AtomicInteger(123))
      count.value must_=== 123
      disposer.close()
      count.value must_=== 0
    }

    "be able to register reset of AtomicBoolean to a value upon closing" in {
      val disposer = Disposer()
      val flag = disposer.resets(true, new AtomicBoolean(false))
      flag.value must_=== false
      disposer.close()
      flag.value must_=== true
    }

    "be able to register reset of AtomicReference to a value upon closing" in {
      val disposer = Disposer()
      val flag = disposer.resets(true, new AtomicReference(false))
      flag.value must_=== false
      disposer.close()
      flag.value must_=== true
    }

    "be able to register release of reference upon closing" in {
      "and not release when not closed" in {
        val disposer = Disposer()
        val weakRef = new WeakReference(disposer.releases(new Object()))
        weakRef.get() must not be null
        System.gc()
        weakRef.get() must not be null
      }

      "and release when not closed" in {
        val disposer = Disposer()
        val weakRef = new WeakReference(disposer.releases(new Object()))
        weakRef.get() must not be null
        disposer.close()
        System.gc()
        weakRef.get() must be_==(null).eventually(1, 100.millis)
      }
    }
  }
}
