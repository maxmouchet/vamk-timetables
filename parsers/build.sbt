import com.typesafe.sbt.SbtStartScript

seq(SbtStartScript.startScriptForClassesSettings: _*)

name := "vamk-timetables"

scalaVersion := "2.10.3"

libraryDependencies += "org.jsoup" % "jsoup" % "1.7.2"

libraryDependencies += "joda-time" % "joda-time" % "2.3"

libraryDependencies += "org.joda" % "joda-convert" % "1.5"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.6"

libraryDependencies += "org.json4s" % "json4s-ext_2.10" % "3.2.6"

libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.6.12"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.13"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"