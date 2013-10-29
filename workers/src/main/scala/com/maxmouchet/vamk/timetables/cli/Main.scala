package com.maxmouchet.vamk.timetables.cli

import com.weiglewilczek.slf4s.Logging
import com.maxmouchet.vamk.timetables.parser.{Schedule, Timetable, TimetableLink}
import scala.util.matching.Regex
import com.maxmouchet.vamk.timetables.discoverer.{CallbackOutput, Discoverer}
import java.net.{URI, URL}
import scala.collection.mutable
import com.maxmouchet.vamk.timetables.updater.DBClient
import org.postgresql.ds.PGPoolingDataSource

object Main extends App with Logging {

  val name = "vamk-timetables-cli"

  val parser = new scopt.OptionParser[Config](name) {
    head(name, "0.1")
  }

  parser.parse(args, Config()) map {
    config =>

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

      val schedules = new mutable.MutableList[Schedule]
      val timetables = new mutable.MutableList[TimetableLink]

      def doWork(message: String) = {
        val link = TimetableLink.fromJSON(message)

        val groupNamePattern = new Regex( """(\w-\w{2,3}-\w{2,3})""")
        if (groupNamePattern.findFirstMatchIn(link.name).nonEmpty) {
          timetables += link
        }
      }

      val urls = new mutable.MutableList[URL]
      urls += new URL("http://www.bet.puv.fi/schedule/P1_13_14/mfw.htm")
      urls += new URL("http://www.bet.puv.fi/studies/lukujarj/LV_13_14/syksy.htm")

      val output = new CallbackOutput(doWork)
      val discoverer = new Discoverer(urls.toArray[URL], output)

      println("Discovering timetables...")

      discoverer.discover

      println(timetables.length + " timetables to parse...")

      for (timetableLink <- timetables.par) {
        try {
          for (schedule <- Timetable.fromURL(timetableLink.url).schedules) {
            schedules += schedule
          }
        } catch {
          case e: Exception => println(e)
        }
      }

      println("\n" + schedules.length + " schedules parsed")

      println("Updating DB...")

      val dbClient = new DBClient(source)
      dbClient.dropAllSchedules
      for (schedule <- schedules.toList) {
        dbClient.insertSchedule(schedule)
      }

      sys.exit(0)

  } getOrElse {
    // arguments are bad, usage message will have been displayed
  }

}
