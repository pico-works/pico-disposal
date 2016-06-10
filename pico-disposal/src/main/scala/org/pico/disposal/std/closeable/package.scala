package org.pico.disposal.std

import java.io.Closeable

import org.pico.atomic.EmptyReferent
import org.pico.disposal.Closed

package object closeable {
  implicit val emptyReferent_Closeable_po9XBKr = EmptyReferent.define[Closeable](Closed)
}
