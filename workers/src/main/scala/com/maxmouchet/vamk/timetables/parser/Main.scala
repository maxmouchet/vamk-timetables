package com.maxmouchet.vamk.timetables.parser

import com.rabbitmq.client.{QueueingConsumer, ConnectionFactory}
import com.weiglewilczek.slf4s.Logging
import scala.util.matching.Regex
import com.maxmouchet.vamk.timetables.parser.timetable.models.Timetable
import com.maxmouchet.vamk.timetables.parser.list.timetable.models.TimetableLink

object Main extends App with Logging {

  val name = "vamk-timetables-parser"

  val parser = new scopt.OptionParser[Config](name) {
    head(name, "0.1")

    opt[String]("amqp-host") required() valueName "<host>" action {
      (x, c) =>
        c.copy(amqpHost = x)
    } text "AMQP Host"

    opt[String]("amqp-queue-in") required() valueName "<queue>" action {
      (x, c) =>
        c.copy(amqpQueueIn = x)
    } text "AMQP Queue to listen on"

    opt[String]("amqp-queue-out") required() valueName "<queue>" action {
      (x, c) =>
        c.copy(amqpQueueOut = x)
    } text "AMQP Queue to send on"
  }

  parser.parse(args, Config()) map {
    config =>

      val factory = new ConnectionFactory();
      factory.setHost(config.amqpHost);
      val connection = factory.newConnection();
      val channel = connection.createChannel();

      def doWork(message: String) = {
        val link = TimetableLink.fromJSON(message)

        val groupNamePattern = new Regex( """(\w-\w{2}-\w{2,3})""")
        if (groupNamePattern.findFirstMatchIn(link.name).nonEmpty) {
//          for (schedule <- Timetable.fromURL(link.url).schedules) {
//            channel.basicPublish("", config.amqpQueueOut, null, schedule.toJSON.getBytes)
//            println(schedule.toJSON)
//          }
        }
      }

      channel.queueDeclare(config.amqpQueueIn, false, false, false, null);
      System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

      val consumer = new QueueingConsumer(channel);
      channel.basicConsume(config.amqpQueueIn, false, consumer);

      while (true) {
        val delivery = consumer.nextDelivery();
        val message = new String(delivery.getBody());
        println(" [x] Received '" + message + "'");
        doWork(message);
        println(" [x] Done");
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
      }
      sys.exit(1)

  } getOrElse {
    // arguments are bad, usage message will have been displayed
  }

}
