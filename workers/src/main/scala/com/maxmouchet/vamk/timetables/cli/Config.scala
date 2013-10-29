package com.maxmouchet.vamk.timetables.cli

case class Config(amqpHost: String = "localhost", amqpQueueIn: String = "", amqpQueueOut: String = "")