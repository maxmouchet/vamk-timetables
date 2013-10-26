import sbt._
import Keys._

object Builds extends Build {
  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    version := "0.1-SNAPSHOT",
    organization := "com.maxmouchet",
    scalaVersion := "2.10.2"
  )
  lazy val rootSettings = buildSettings ++ Seq(
    name := "foo"
  )
  lazy val librarySettings = buildSettings ++ Seq(
    libraryDependencies ++= Seq(
      "org.scalatest" % "scalatest_2.10" % "1.9.2" % "test",
      "joda-time" % "joda-time" % "2.3",
      "org.joda" % "joda-convert" % "1.5",
      "org.jsoup" % "jsoup" % "1.7.2",
      "org.postgresql" % "postgresql" % "9.2-1003-jdbc4",
      "com.github.scopt" %% "scopt" % "3.1.0")
  )
  lazy val appSettings = buildSettings ++
    sbtassembly.Plugin.assemblySettings ++ Seq(
    name := "vamk-timetable-parser"
  )
  lazy val root = Project("root", file("."), settings = rootSettings) aggregate(app)
  lazy val library = Project("library", file("library"), settings = librarySettings)
  lazy val app = Project("app", file("app"), settings = appSettings) dependsOn(library)
}