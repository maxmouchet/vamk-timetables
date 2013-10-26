package com.maxmouchet.vamk.timetable

import scala.util.matching.Regex
import scala.collection.mutable.MutableList
import org.joda.time.DateTime
import org.postgresql.ds.PGPoolingDataSource
import java.net.URI

object Test {

  def main(args: Array[String]): Unit = {
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

    val beginTime = new DateTime()

    val baseUrl = "http://www.bet.puv.fi/schedule/P1_13_14/"

    val groupNamePattern = new Regex("""(\w-\w{2}-\w{2,3})""")

    val weeks = new WeekListParser(baseUrl + "mfw.htm").parse
    //    val weeks = new WeekListParser(baseUrl + "mfw.htm").parse.drop(17)

    var total = 0

    var allSchedules = MutableList[Schedule]()

    for (week <- weeks.par) {
      val timetables = new TimetableListParser(baseUrl + week.url).parse

      for (timetable <- timetables.par) {
        val groupName = groupNamePattern findFirstIn timetable.name match {
          case Some(groupNamePattern(x)) => {
            try {
              println("Parsing: " + timetable.name);

              val table = new TableParser(baseUrl + timetable.url, "table[cellspacing=1]", "tr", "td").parse
              val schedules = new TimetableParser(table).parse

              total += 1

              for (schedule <- schedules) {
                println(schedule.courseName);
                println(schedule.startDate);
                println(schedule.endDate);
                println(schedule.group);
                println(schedule.professor);
                println(schedule.room);
                println("---");
                allSchedules += schedule
              }
            } catch {
              case _: Throwable => System.err.println("Error while parsing timetable at: " + baseUrl + timetable.url)
            }
          }
          case None => "Unknown"
        }

      }
    }

    val endTime = new DateTime()
    val elapsedTime = endTime.secondOfDay().get() - beginTime.secondOfDay().get()

    println("Time elapsed: " + elapsedTime + " seconds")
    println("Number of timetables parsed: " + total + " (" + total / elapsedTime + "/s)")
    println("Number of schedules parsed: " + allSchedules.length)

    println("Updating DB...")

    val dbClient = new DBClient(source)
    dbClient.dropAllSchedules
    for (schedule <- allSchedules) {
      dbClient.insertSchedule(schedule)
    }
  }

}