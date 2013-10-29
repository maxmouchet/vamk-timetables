package com.maxmouchet.vamk.timetables.updater

import com.weiglewilczek.slf4s.Logging
import com.rabbitmq.client.{QueueingConsumer, ConnectionFactory}
import scala.util.matching.Regex
import com.maxmouchet.vamk.timetables.parser.Schedule
import org.postgresql.ds.PGPoolingDataSource
import java.net.URI

object Main extends App with Logging {

  val name = "vamk-timetables-updater"

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
  }

  parser.parse(args, Config()) map {
    config =>

      val factory = new ConnectionFactory();
      factory.setHost(config.amqpHost);
      val connection = factory.newConnection();
      val channel = connection.createChannel();

      val dbUri = new URI(sys.env("DATABASE_URL"));

      val username = dbUri.getUserInfo().split(":")(0)
      val password = dbUri.getUserInfo().split(":")(1)
      val databaseName = dbUri.getPath().replace('/', ' ').trim()
      val serverName = dbUri.getHost()
      val port = dbUri.getPort()

      val source = new PGPoolingDataSource()

      source.setDataSourceName("DB");
      source.setServerName(serverName);
      source.setPortNumber(port)
      source.setDatabaseName(databaseName);
      source.setUser(username);
      if (password != 1234) {
        source.setPassword(password)
      }
      source.setMaxConnections(10);

      val dbClient = new DBClient(source)

      def doWork(message: String) = {
        val schedule = Schedule.fromJSON(message)




        //        val link = TimetableLink.fromJSON(message)
        //
        //        val groupNamePattern = new Regex( """(\w-\w{2}-\w{2,3})""")
        //        if (groupNamePattern.findFirstMatchIn(link.name).nonEmpty) {
        //          for (schedule <- Timetable.fromURL(link.url).schedules) {
        //            channel.basicPublish("", config.amqpQueueOut, null, schedule.toJSON.getBytes)
        //            println(schedule.toJSON)
        //          }
        //        }
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