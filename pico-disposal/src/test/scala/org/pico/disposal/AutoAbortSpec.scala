package org.pico.disposal

import org.specs2.mutable.Specification

class AutoAbortSpec extends Specification {
  "AutoAbort" should {
    "have foreach syntax that disposes argument on return" in {
      var counter = List.empty[Int]

      for (_ <- AutoAbort(OnAbort(counter ::= 1, counter ::= 2))) {
        counter ==== List.empty
      }

      counter ==== List(2)
    }

    "have foreach syntax that aborts then disposes argument on exception" in {
      var counter = List.empty[Int]

      {
        for (_ <- AutoAbort(OnAbort(counter ::= 1, counter ::= 2))) {
          throw new Exception()
        }: Int
      } must throwA[Exception]

      counter ==== List(2, 1)
    }
  }
}
