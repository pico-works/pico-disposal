package org.pico.disposal.std

import java.util.concurrent.ExecutorService

import org.pico.disposal.Disposable

package object executorService {
  implicit val disposable_ExecutorService_joRu2Nb = Disposable[ExecutorService](_.shutdown())
}
