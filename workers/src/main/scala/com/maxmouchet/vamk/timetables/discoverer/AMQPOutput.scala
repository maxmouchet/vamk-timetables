package com.maxmouchet.vamk.timetables.discoverer

import com.rabbitmq.client.ConnectionFactory

class AMQPOutput(amqpHost: String, amqpQueue: String) extends Output {

  val factory = new ConnectionFactory
  factory.setHost(amqpHost)
  val connection = factory.newConnection
  val channel = connection.createChannel

  def write(string: String): Unit = channel.basicPublish("", amqpQueue, null, string.getBytes)

}
