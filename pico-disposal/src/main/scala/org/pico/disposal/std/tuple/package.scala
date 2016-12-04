package org.pico.disposal.std

import org.pico.disposal.Disposable
import org.pico.disposal.syntax.disposable._

package object tuple {
  implicit def disposable_Tuple2_c4wWHes[A: Disposable, B: Disposable] = {
    Disposable[(A, B)] { case (a, b) =>
      a.dispose()
      b.dispose()
    }
  }

  implicit def disposable_Tuple3_c4wWHes[A: Disposable, B: Disposable, C: Disposable] = {
    Disposable[(A, B, C)] { case (a, b, c) =>
      a.dispose()
      b.dispose()
      c.dispose()
    }
  }

  implicit def disposable_Tuple3_c4wWHes[A: Disposable, B: Disposable, C: Disposable, D: Disposable] = {
    Disposable[(A, B, C, D)] { case (a, b, c, d) =>
      a.dispose()
      b.dispose()
      c.dispose()
      d.dispose()
    }
  }

  implicit def disposable_Tuple3_c4wWHes[A: Disposable, B: Disposable, C: Disposable, D: Disposable, E: Disposable] = {
    Disposable[(A, B, C, D, E)] { case (a, b, c, d, e) =>
      a.dispose()
      b.dispose()
      c.dispose()
      d.dispose()
      e.dispose()
    }
  }
}
