package org.pico.disposal

import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import org.specs2.mutable.Specification

class DisposableSpec extends Specification {
  "Disposable should" should {
    "when composed dispose in reverse order" >> {
      var value = 1
      val disposable1 = OnClose(value += 1)
      val disposable2 = OnClose(value *= 10)

      (disposable1 :+: disposable2).dispose()

      value ==== 11
    }

    "dispose in reverse order in nested for comprehension" >> {
      var x = 1

      for (a <- OnClose(x += 1)) {
        for (b <- OnClose(x *= 10)) {
          "Hello world"
        }
      }: String

      x ==== 11
    }

    "dispose in reverse order in shared for comprehension" >> {
      var x = 1

      for (a <- OnClose(x += 1); b <- OnClose(x *= 10)) {
        "Hello World"
      }: String

      x ==== 11
    }

    "have asCloseable method" in {
      Closed.asCloseable must_=== Closed
    }

    "have asCloseable method for not Closeable types" in {
      var count = 0
      class NewType()
      implicit val disposable_NewType = Disposable[NewType](_ => count += 1)
      val newType = new NewType()
      newType.asAutoCloseable.dispose()
      newType.asCloseable.close()
      count must_=== 2
    }

    "have asAutoCloseable method" in {
      Closed.asAutoCloseable must_=== Closed
    }

    "have dispose method that does not suppress exception" in {
      class NewType()
      implicit val disposable_NewType = Disposable[NewType](_ => throw new Exception())
      val newType = new NewType()
      newType.dispose() must throwA[Exception]
      ok
    }
  }
}
