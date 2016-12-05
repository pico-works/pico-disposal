# pico-disposal
[![CircleCI](https://circleci.com/gh/pico-works/pico-disposal/tree/develop.svg?style=svg)](https://circleci.com/gh/pico-works/pico-disposal/tree/develop)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c10d87ce47dd4a10adf7ec831eea139e)](https://www.codacy.com/app/newhoggy/pico-works-pico-disposal?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=pico-works/pico-disposal&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/c10d87ce47dd4a10adf7ec831eea139e)](https://www.codacy.com/app/newhoggy/pico-works-pico-disposal?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=pico-works/pico-disposal&amp;utm_campaign=Badge_Coverage)
[![Gitter chat](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/pico-works/general)

Support library for resource management.  Resource management centres around the `Disposable` type-class and the
`java.lang.AutoCloseable` interface.

## Getting started

Add this to your SBT project:

```
resolvers += "dl-john-ky-releases" at "http://dl.john-ky.io/maven/releases"

libraryDependencies += "org.pico" %%  "pico-disposal" % "1.0.10"
```

Then read the [tutorial](pico-disposal/src/main/tut/tutorial.md).
