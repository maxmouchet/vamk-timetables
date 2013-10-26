package com.maxmouchet.vamk.timetable

import scala.util.matching.Regex
import org.joda.time.DateTime
import org.joda.time.LocalDate

class TimetableParser(table: Array[Array[String]]) {

  val timePattern = new Regex("""(\d{1,2}):(\d{2})-(\d{1,2}):(\d{2})""", "startHour", "startMinute", "endHour", "endMinute")
  val datePattern = new Regex("""([^\d]+)(\d{1,2}).(\d{1,2}).(\d{4})""", "name", "day", "month", "year")

  val courseNamePattern = new Regex("""^((\w+-(\w+)\s+)|((ALOIT|VARAU)\w+\s+))?(.+)""")
  val groupNamePattern = new Regex("""(\w-\w{2}-\w{2,3})""")

  def parse: Array[Schedule] = {
    var schedules = Vector.empty[Schedule]

    // Parse group name in the first row (title).
    val groupName = groupNamePattern findFirstIn table(0)(0) match {
      case Some(groupNamePattern(x)) => x
      case None => "Unknown"
    }

    // Header is the second row.
    val header = ParserHelper.rowToDateArray(table(1), datePattern)

    for (row <- table.drop(2)) {

      val timeInterval = ParserHelper.cellToTimeInterval(row(0), timePattern)

      for ((cell, index) <- row.view.zipWithIndex) {

        try {
          val schedule = cellToSchedule(cell, header(index), timeInterval, groupName)

          if ("""\w+""".r.findFirstMatchIn(schedule.courseName) != None) {
            var duplicate = false

            for (i <- 0 until schedules.length) {
              val previousSchedule = schedules(i)
              if (previousSchedule.courseName.equals(schedule.courseName)) {
                if (ParserHelper.isSchedulesSuccesives(previousSchedule, schedule)) {
                  val newSchedule =
                    new Schedule(
                      previousSchedule.courseName,
                      previousSchedule.startDate,
                      schedule.endDate,
                      previousSchedule.professor,
                      previousSchedule.room,
                      previousSchedule.group)

                  schedules = schedules.updated(i, newSchedule)

                  duplicate = true
                }
              }
            }

            if (!duplicate) { schedules = schedules :+ schedule }
          }
        } catch {
          case _: Throwable => System.err.println("Error while parsing: " + cell)
        }
      }
    }

    schedules.toArray
  }

  def cellToSchedule(cell: String, date: LocalDate, timeInterval: TimeInterval, group: String): Schedule = {
    val startDate = ParserHelper.mergeDateAndTime(date, timeInterval.startTime)
    val endDate = ParserHelper.mergeDateAndTime(date, timeInterval.endTime)

    val courseName = courseNamePattern.findFirstMatchIn(cell).get.group(6).trim

    val professor = cell.split("\n")(cell.split("\n").length - 2).trim
    val room = cell.split("\n").last.trim

    new Schedule(courseName, startDate, endDate, professor, room, group)
  }

}