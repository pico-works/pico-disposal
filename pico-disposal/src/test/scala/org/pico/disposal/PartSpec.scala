package org.pico.disposal

import java.io.Closeable

import org.pico.disposal.std.autoCloseable._
import org.specs2.mutable.Specification

import scala.util.control.NonFatal

class PartSpec extends Specification {
  class Resource extends Closeable {
    var _closed: Boolean = false

    def closed: Boolean = _closed

    override def close(): Unit = _closed = true
  }

  object Resource {
    def apply(f: => Unit = ()): Resource = new Resource
  }

  case class Composite(a: Resource, b: Resource, c: Resource) extends SimpleDisposer {
    this.disposes(a)
    this.disposes(b)
    this.disposes(c)
  }

  "Part" should {
    "build a complete resource when no exceptions" in {
      val composite: Composite = {
        for {
          a <- Part(Resource())
          b <- Part(Resource())
          c <- Part(Resource())
        } yield Composite(a, a, a)
      }

      composite.a.closed ==== false
      composite.b.closed ==== false
      composite.c.closed ==== false
    }

    "dispose resource when exception occur 2" in {
      val ra = Resource()
      val rb = Resource()

      try {
        for {
          a <- Part(ra)
          b <- Part(rb)
          c <- Part((throw new Exception): Resource)
        } yield Composite(a, a, a)
      } catch {
        case NonFatal(e) =>
      }

      ra.closed ==== true
      rb.closed ==== true
    }

    "dispose resource when exception occur 1" in {
      val ra = Resource()
      val rb = Resource()
      val rc = Resource()

      try {
        for {
          a <- Part(ra)
          b <- Part(rb)
          c <- Part(rc)
        } yield (throw new Exception()): Composite
      } catch {
        case NonFatal(e) =>
      }

      ra.closed ==== true
      rb.closed ==== true
      rc.closed ==== true
    }
  }
}
