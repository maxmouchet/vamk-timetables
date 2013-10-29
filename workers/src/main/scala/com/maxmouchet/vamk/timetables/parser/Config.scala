package com.maxmouchet.vamk.timetables.parser

case class Config(amqpHost: String = "localhost", amqpQueueIn: String = "", amqpQueueOut: String = "")