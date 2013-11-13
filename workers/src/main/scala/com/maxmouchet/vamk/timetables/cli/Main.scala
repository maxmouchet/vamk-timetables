package com.maxmouchet.vamk.timetables.cli

import scala.util.matching.Regex
import scala.collection.mutable

import java.net.URI
import com.weiglewilczek.slf4s.Logging
import org.postgresql.ds.PGPoolingDataSource

import com.maxmouchet.vamk.timetables.discoverer.Discoverer
import com.maxmouchet.vamk.timetables.updater.DBClient

import com.maxmouchet.vamk.timetables.parser.timetable.models.Schedule
import com.maxmouchet.vamk.timetables.parser.list.timetable.models.TimetableLink
import com.maxmouchet.vamk.timetables.discoverer.outputs.CallbackOutput
import com.maxmouchet.vamk.timetables.parser.list.week._
import com.maxmouchet.vamk.timetables.parser.list.week.vamk._
import com.maxmouchet.vamk.timetables.parser.timetable.cell._
import com.maxmouchet.vamk.timetables.parser.timetable.cell.vamk._
import com.maxmouchet.vamk.timetables.parser.table.HTMLTableParser
import com.maxmouchet.vamk.timetables.parser.table.sources.RemoteTableSource
import com.maxmouchet.vamk.timetables.parser.table.settings.VAMKSettings
import com.maxmouchet.vamk.timetables.parser.timetable.vamk.VAMKTimetableParser

object Main extends App with Logging {

  val schedules = new mutable.MutableList[Schedule]
  val timetables = new mutable.MutableList[TimetableLink]

  val sources = new mutable.HashMap[String, WeekListParser]
  sources("http://www.bet.puv.fi/schedule/P1_13_14/mfw.htm") = new ITVAMKWeekListParser
  sources("http://www.bet.puv.fi/studies/lukujarj/LV_13_14/syksy.htm") = new IBVAMKWeekListParser

  override def main(args: Array[String]) {

    println("Trying to connect to DB...")
    val dbClient = getDBClient

    println("Discovering timetables...")
    new Discoverer(
      sources.toMap[String, WeekListParser],
      new CallbackOutput(discoveryCallback)
    ).discover

    println(timetables.length + " timetables to parse...")

    for (timetableLink <- timetables.par) {
      try {
        parseTimetable(timetableLink)
      } catch {
        case e: Exception => println(e)
      }
    }

    println("\n" + schedules.length + " schedules parsed (incl. duplicates)")

    println("Deduplicating schedules...")
    val schedules_d = deduplicateSchedules(schedules.toList)

    println(schedules_d.length + " schedules after deduplication")


    println("Updating DB...")
    dbClient.dropAllSchedules
    for (schedule <- schedules_d.toList) {
      dbClient.insertSchedule(schedule)
    }
  }

  def discoveryCallback(message: String) = {
    val link = TimetableLink.fromJSON(message)

    val groupNamePattern = new Regex( """(\w-\w{2,3}-\w{1,3}-?\d?)""")
    if (groupNamePattern.findFirstMatchIn(link.name).nonEmpty) {
      timetables += link
    }
  }

  def parseTimetable(timetableLink: TimetableLink) = {
    println(timetableLink.url)

    var cellParser: CellParser = null

    if (timetableLink.url.contains("studies/lukujarj/LV_13_14")) {
      cellParser = new IBVAMKCellParser
    }
    else {
      cellParser = new ITVAMKCellParser
    }

    val tableParser = new HTMLTableParser(timetableLink.url, VAMKSettings)
    val tableSource = new RemoteTableSource(tableParser)

    for (schedule <- new VAMKTimetableParser(tableSource, cellParser).parse.schedules) {
      schedules += schedule
    }
  }

  def deduplicateSchedules(input: List[Schedule]): List[Schedule] = {
    val output = new mutable.MutableList[Schedule]

    for (schedule <- schedules.toList) {
      var duplicate = false

      for (schedule_d <- output.toList) {
        if (schedule.courseName.equals(schedule_d.courseName)
          && schedule.startDate.toString().equals(schedule_d.startDate.toString())
          && schedule.endDate.toString().equals(schedule_d.endDate.toString())
          && schedule.room.equals(schedule_d.room.toString)
          && schedule.group.equals(schedule_d.group.toString)
        ) {
          duplicate = true
        }
      }

      if (!duplicate) {
        output += schedule
      }
    }

    output.toList
  }

  private def getDBClient: DBClient = {
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

    new DBClient(source)
  }


}
