package org.pico.disposal.std

import org.pico.disposal.Disposable
import org.pico.disposal.syntax.disposable._

package object option {
  implicit def disposable_Option_Jyhd8yK[A: Disposable] = Disposable[Option[A]](_.foreach(_.dispose))
}
