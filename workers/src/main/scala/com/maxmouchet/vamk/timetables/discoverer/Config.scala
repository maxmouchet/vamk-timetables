package com.maxmouchet.vamk.timetables.discoverer

case class Config(amqpHost: String = "localhost", amqpQueue: String = "", urls: Seq[String] = Seq())
