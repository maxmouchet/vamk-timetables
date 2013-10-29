package com.maxmouchet.vamk.timetables.discoverer

import java.net.URL
import scala.collection.mutable

object Main extends App {

  val name = "vamk-timetables-discoverer"

  val parser = new scopt.OptionParser[Config](name) {
    head(name, "0.1")

    opt[String]("amqp-host") required() valueName "<host>" action {
      (x, c) =>
        c.copy(amqpHost = x)
    } text "AMQP Host"

    opt[String]("amqp-queue") required() valueName "<queue>" action {
      (x, c) =>
        c.copy(amqpQueue = x)
    } text "AMQP Queue"

    arg[String]("url ...") unbounded() action {
      (x, c) => c.copy(urls = c.urls :+ x)
    } text "URLs to discover"
  }

  parser.parse(args, Config()) map {
    config =>
      val urls = new mutable.MutableList[URL]
      for (url <- config.urls) {
        urls += new URL(url)
      }

      val output = new AMQPOutput(config.amqpHost, config.amqpQueue)
      val discoverer = new Discoverer(urls.toArray[URL], output)

      while (true) {
        try {
          discoverer.discover
        } catch {
          case e: Exception => println(e)
        }
        Thread.sleep(30000)
      }
  } getOrElse {
    // arguments are bad, usage message will have been displayed
  }
}
