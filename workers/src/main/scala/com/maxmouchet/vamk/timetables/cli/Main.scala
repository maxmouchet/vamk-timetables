package com.maxmouchet.vamk.timetables.cli

import com.weiglewilczek.slf4s.Logging
import scala.util.matching.Regex
import com.maxmouchet.vamk.timetables.discoverer.{CallbackOutput, Discoverer}
import java.net.{URI, URL}
import scala.collection.mutable
import com.maxmouchet.vamk.timetables.updater.DBClient
import org.postgresql.ds.PGPoolingDataSource
import com.maxmouchet.vamk.timetables.parser.timetable.models.{Timetable, Schedule}
import com.maxmouchet.vamk.timetables.parser.list.timetable.models.TimetableLink
import com.maxmouchet.vamk.timetables.parser.timetable.algorithms.VAMKStrategy
import com.maxmouchet.vamk.timetables.parser.timetable.settings.{IBVAMKSettings, ITVAMKSettings}
import com.maxmouchet.vamk.timetables.parser.list.week.algorithms.{IBVAMKStrategy, ITVAMKStrategy, Strategy}

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

        val groupNamePattern = new Regex( """(\w-\w{2,3}-\w{1,3}-?\d?)""")
        if (groupNamePattern.findFirstMatchIn(link.name).nonEmpty) {
          timetables += link
        }
      }

      val sources = new mutable.HashMap[String, Strategy]
      sources("http://www.bet.puv.fi/schedule/P1_13_14/mfw.htm") = new ITVAMKStrategy
      sources("http://www.bet.puv.fi/studies/lukujarj/LV_13_14/syksy.htm") = new IBVAMKStrategy

      val discoverer = new Discoverer(sources.toMap[String, Strategy], new CallbackOutput(doWork))

      println("Discovering timetables...")

      discoverer.discover

      println(timetables.length + " timetables to parse...")

      for (timetableLink <- timetables.par) {
        try {
          println(timetableLink.url)
          if (timetableLink.url.contains("studies/lukujarj/LV_13_14")) {
            for (schedule <- Timetable.parse(new VAMKStrategy(timetableLink.url, IBVAMKSettings)).schedules) {
              schedules += schedule
            }
          } else {
            for (schedule <- Timetable.parse(new VAMKStrategy(timetableLink.url, ITVAMKSettings)).schedules) {
              schedules += schedule
            }
          }
        } catch {
          case e: Exception => println(e)
        }
      }

      val schedules_d = new mutable.MutableList[Schedule]

      for (schedule <- schedules.toList) {
        var duplicate = false

        for (schedule_d <- schedules_d.toList) {
          if (schedule.courseName.equals(schedule_d.courseName)
            && schedule.startDate.toString().equals(schedule_d.startDate.toString())
            && schedule.endDate.toString().equals(schedule_d.endDate.toString())
            && schedule.room.equals(schedule_d.room.toString)
          ) {
            println("Duplicate found")
            duplicate = true
          }
        }

        if (!duplicate) {
          schedules_d += schedule
        }
      }

      println("\n" + schedules.length + " schedules parsed before distinct")
      println("\n" + schedules_d.length + " schedules after distinct")


      println("Updating DB...")

      val dbClient = new DBClient(source)
      dbClient.dropAllSchedules
      for (schedule <- schedules_d.toList) {
        dbClient.insertSchedule(schedule)
      }

      sys.exit(0)

  } getOrElse {
    // arguments are bad, usage message will have been displayed
  }

}
