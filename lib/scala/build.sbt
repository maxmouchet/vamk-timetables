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
      "com.rabbitmq" % "amqp-client" % "3.1.4"
      )
