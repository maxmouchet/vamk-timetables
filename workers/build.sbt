name := "vamk-timetables-workers"

version := "1.0"

organization := "com.maxmouchet"

scalaVersion := "2.10.2"

scalaSource in Compile := baseDirectory.value / "src/main/scala"

resolvers += "oss.sonatype.org" at "https://oss.sonatype.org/content/groups/scala-tools/"

libraryDependencies ++= Seq(
      "org.scalatest" % "scalatest_2.10" % "1.9.2" % "test",
      "joda-time" % "joda-time" % "2.3",
      "org.joda" % "joda-convert" % "1.5",
      "org.jsoup" % "jsoup" % "1.7.2",
      "org.postgresql" % "postgresql" % "9.2-1003-jdbc4",
      "com.github.scopt" %% "scopt" % "3.1.0",
      "com.rabbitmq" % "amqp-client" % "3.1.4",
      "org.json4s" % "json4s-native_2.10" % "3.2.5",
      "org.slf4j" % "slf4j-api" % "1.7.5",
      "org.slf4j" % "slf4j-log4j12" % "1.7.5",
      "com.weiglewilczek.slf4s" % "slf4s_2.9.1" % "1.0.7")
