package org.pico.disposal

class FailedDisposeException(
    message: String,
    causes: List[Throwable]) extends Exception(message, causes.headOption.orNull) {
  def this(causes: List[Throwable]) = this(null, causes)

  def this(message: String) = this(message, List.empty)

  def this() = this(null, List.empty)

  override def getMessage: String = s"${super.getMessage} [${causes.mkString(", ")}]"
}
