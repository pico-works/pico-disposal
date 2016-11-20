
## Using the dispose method instead of close methods to close resources
In order to dispose a resource, call the `dispose` method on the resource:

    resource.dispose()

## How is Disposable different from Closeable and AutoCloseable?
The dispose pattern is different from `Closeable` and `AutoCloseable` in a number of ways:

The `Disposable` pattern:

* means objects may be disposed without risk of thrown exceptions
* can be made to work on arbitrary types
* can be composed
* provides `for` syntax support for automatic disposal
* allows disposal to generically delegated to another object

### Treatment of exceptions
The `dispose` method will silently catch and ignore all exceptions.  Often this is what you
want because if for whatever reason the clean up of a resource fails, there is not much
more you can do anyway.

```tut:reset
import org.pico.disposal._
import org.pico.disposal.syntax.disposable._
import org.pico.disposal.std.autoCloseable._

val closeable = OnClose(throw new Exception())
closeable.dispose() // does not throw
try {
    closeable.close() // throws
} catch {
  case e: Exception => println("Exception thrown")
}
```

### Working on arbitrary types
Sometimes, third-party libraries will make the arbitrary choice not to implement
`Closeable` or `AutoCloseable`, which means you can't write or use a generic method
to close them.

To work-around these situations, this library introduces the `Disposable` type-class.
Whenever a type needs disposal capabilities, it is simple to retrospectively add it
by implementing the type-class.  For example, the library implements the type-class
for all types inheriting from `AutoCloseable` like this:

```tut:reset
import org.pico.disposal._

implicit val disposableAutoCloseable_YYKh2cf = new Disposable[AutoCloseable] {
  protected override def onDispose(a: AutoCloseable): Unit = a.close()
}
```

It is okay to throw from `onDispose` in the type-class instance because it is never
called directly, but rather via the `dispose` method which catches and ignores the
exception.

Likewise, the `Dispose` instances can be defined for other types:

```tut:reset
import org.pico.disposal._
import org.pico.disposal.syntax.disposable._

case class Shutdownable() {
  def shutdown(): Unit = ()
}

implicit val disposableShutdownable_F8mA7jE = new Disposable[Shutdownable] {
  protected override def onDispose(a: Shutdownable): Unit = a.shutdown()
}

val shutdownable = Shutdownable()
shutdownable.dispose()
```

### Composition of disposable objects
`Disposable` objects can be pair-wise composed into a larger `Closeable` object that will `dispose`
of each of the original objects in reverse order when closed.  The `:+:` operator was chosen
for this purposes because it is right associative and so will close composed disposables from right
to left.

```tut:reset
import org.pico.disposal._
import org.pico.disposal.syntax.disposable._
import org.pico.disposal.std.autoCloseable._
import java.io._

implicitly[Disposable[AutoCloseable]]
val file1 = new FileOutputStream("target/file1.txt")
val file2 = new FileOutputStream("target/file2.txt")
val closeable = file1 :+: file2
closeable.close() // Closes both files
```

Naturally, more than two disposable objects can be composed in this way with repeated application
of the `:+:` operator.

### For syntax for automatic disposal
In many situations, the code calls for opening a resources, doing some work and then
closing the resource:

```tut:reset
import java.io._

val file1 = new FileOutputStream("target/file1.txt")
val file2 = new FileOutputStream("target/file2.txt")
// Do work
file2.close()
file1.close()
```

The above code is incorrect.  An exception may be thrown from anywhere in the code
causing the `close` method call to be skipped resulting in a resource leak.  The
garbage collector may or may not get around to collecting and closing these resources.
For an application that churns through a high volume of resources, the system could
very quickly run out of handles.

In order to do this properly in the presence of exceptions, some exception handling is
also called for.

```tut:reset
import java.io._

val file1 = new FileOutputStream("target/file1.txt")
try {
  val file2 = new FileOutputStream("target/file2.txt")
  try {
    // Do work
  } finally {
    file2.close()
  }
} finally {
  file1.close()
}
```

This is very tedious and often, code is not written this carefully.

This library provides a convenient alternative syntax that does the work for you, as
the following example shows:

```tut:reset
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import java.io._

for (file1 <- new FileOutputStream("target/file1.txt")) {
  for (file2 <- new FileOutputStream("target/file2.txt")) {
    // Do work
  } // file2 disposed in reverse order here
} // file1 disposed in reverse order here
```

Or the more concise version:

```tut:reset
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import java.io._

for (file1 <- new FileOutputStream("target/file1.txt");
     file2 <- new FileOutputStream("target/file2.txt")) {
  // Do work
} // Both files disposed in reverse order here
```

### Delegating disposal
By providing a single abstraction for disposing all resources, it is possible to
delegate disposal to other objects.  The library providers a `Disposer` class for this
purpose:

```tut:reset
import org.pico.disposal._
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import java.io._

for (disposer <- Disposer()) {
  val file1 = disposer.disposes(new FileOutputStream("target/file1.txt"))
  val file2 = disposer.disposes(new FileOutputStream("target/file2.txt"))
  // Do work
} // Both files disposed by the disposer in reverse order here
```

The `Disposer` is particularly useful in a class setting when a class may own several
disposable objects.  In this case inherit from `SimpleDisposer` to enable a class to be able to
take ownership of objects and manage their lifetimes:

```tut:reset
import org.pico.disposal._
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import java.io._

class TwoFiles extends SimpleDisposer {
  val file1 = this.disposesOrClose(new FileOutputStream("target/file1.txt"))
  val file2 = this.disposesOrClose(new FileOutputStream("target/file2.txt"))
}

for (twoFiles <- new TwoFiles) {
  // Do work
} // Both files disposed by the disposer in reverse order here if no exceptions thrown.
```

Here, if the constructor does not throw an exception, then the two files are disposed in reverse
order as normal.  If, however, the opening of `file2.txt` throws an exception, then we risk
not closing `file1.txt`.  The use of `disposesOrClose` in the above example avoids this problem
by closing the disposer in the event of an exception, which in turn disposes anything registered
to it.

## Other features
### Closed object
The library includes a singleton `Closed` object.  The `Closed` object is a `Closeable` object with
the notion of being already closed.  This means additional calls to `close` or `dispose` on it has
no effect.  It is used as per the null object pattern where it is swapped in place of an existing
`Closeable` object to represent the fact that the resource is closed and collectible.

This is especially important for dropping references to objects so they can be garbage collected
as in the following example:

```tut:reset
import java.io.Closeable
import java.util.concurrent.atomic._
import org.pico.disposal.Closed
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._

class Owner extends Closeable {
    val resource: AtomicReference[Closeable] = ???
    // ...
    override def close(): Unit = resource.getAndSet(Closed).dispose()
}
```

What is happening here, is that a resource is swapped with a lightweight already closed singleton
in a single atomic action, which means the `Owner` is no longer holding a reference to the
resource.  Then the resource that is returned by `getAndSet` is disposed.

### OnClose callbacks
Sometimes, an object needs to run some effect when an object is closed, but the effect needs to be
decided on at some point other than in the close function.  It is convenient to write such code
with a combination of the `OnClose` function and the `Disposer` type:

```tut:reset
import org.pico.disposal._
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import java.io._

class Owner extends SimpleDisposer {
  // ...
  this.disposes(OnClose(/* do something */))
  // ...
}

val owner: Owner = new Owner()
owner.close() // "do something" will be called at this point
```

### Disposer and AtomicReference
The Disposer can swap and release a value referred to by an AtomicReference when it closes.

```tut:reset
import org.pico.disposal._
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import java.util.concurrent.atomic._

val disposer = Disposer()
val valueRef = disposer.swapReleases(0, new AtomicReference(100))
disposer.dispose()
assert(valueRef.get() == 0)
```

The Disposer can also swap and dispose a disposable object referrred to by an AtomicReference
when it closes.

```tut:reset
import org.pico.disposal._
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.syntax.disposable._
import java.io._
import java.util.concurrent.atomic._

val disposer = Disposer()
val closeableRef = disposer.swapDisposes[Closeable](Closed, new AtomicReference(new FileOutputStream("target/file.txt")))
disposer.dispose() // The file is closed at this point
assert(closeableRef.get() == Closed)
```

### Constructing complex resources safely

Use `Part` in a for comprehension to safely construct composite resources:

```tut
import java.io.Closeable

import org.pico.disposal.std.autoCloseable._
import org.specs2.mutable.Specification

import scala.util.control.NonFatal

class Resource extends Closeable {
  override def close(): Unit = ()
}

object Resource {
  def apply(f: => Unit = ()): Resource = new Resource
}

case class Composite(a: Resource, b: Resource, c: Resource) extends SimpleDisposer {
  this.disposes(a)
  this.disposes(b)
  this.disposes(c)
}

val composite: Composite = {
  for {
    a <- Part(Resource())
    b <- Part(Resource())
    c <- Part(Resource())
  } yield Composite(a, a, a)
}
```