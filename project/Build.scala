import sbt.Keys._
import sbt._

object Build extends sbt.Build {  
  val pico_atomic               = "org.pico"        %%  "pico-atomic"               % "0.2.0"

  val specs2_core               = "org.specs2"      %%  "specs2-core"               % "3.7.2"

  implicit class ProjectOps(self: Project) {
    def standard(theDescription: String) = {
      self
          .settings(scalacOptions in Test ++= Seq("-Yrangepos"))
          .settings(publishTo := Some("Releases" at "s3://dl.john-ky.io/maven/releases"))
          .settings(description := theDescription)
          .settings(isSnapshot := true)
    }

    def notPublished = self.settings(publish := {}).settings(publishArtifact := false)

    def libs(modules: ModuleID*) = self.settings(libraryDependencies ++= modules)

    def testLibs(modules: ModuleID*) = self.libs(modules.map(_ % "test"): _*)
  }

  lazy val `pico-fake` = Project(id = "pico-fake", base = file("pico-fake"))
      .standard("Fake project").notPublished
      .testLibs(specs2_core)

  lazy val `pico-disposal` = Project(id = "pico-disposal", base = file("pico-disposal"))
      .standard("Tiny resource management library")
      .libs(pico_atomic)
      .testLibs(specs2_core)

  lazy val all = Project(id = "pico-disposal-project", base = file("."))
      .notPublished
      .aggregate(`pico-disposal`, `pico-fake`)
}
