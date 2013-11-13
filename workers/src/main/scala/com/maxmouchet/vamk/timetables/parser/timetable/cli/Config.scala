package com.maxmouchet.vamk.timetables.parser.timetable.cli

case class Config(amqpHost: String = "localhost", amqpQueueIn: String = "", amqpQueueOut: String = "")