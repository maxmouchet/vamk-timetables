package parsers.vamk

import org.joda.time.LocalDate
import scala.util.matching.Regex
import parsers.base.{TableParser, CellParser}
import parsers.ParserHelper
import models.{TimeInterval, Schedule}

class VAMKTimetableParser(tableParser: TableParser, cellParser: CellParser) {

  val periodPattern = new Regex( """(\d{1,2}).(\d{1,2}).(\d{4})\.+(\d{1,2}).(\d{1,2}).(\d{4})""", "startDay", "startMonth", "startYear", "endDay", "endMonth", "endYear")
  val groupPattern = new Regex( """(\w-\w{2,3}-\w{1,3}-?\d?)""")
  val datePattern = new Regex( """([^\d]+)(\d{1,2}).(\d{1,2}).(\d{4})""", "name", "day", "month", "year")
  val timePattern = new Regex( """(\d{1,2}):(\d{2})-(\d{1,2}):(\d{2})""", "startHour", "startMinute", "endHour", "endMinute")

  def parse: Array[Schedule] = {
    var schedules = Vector.empty[Schedule]

    val table = tableParser.parse

    // Parse group name in the first row (title).
    val groupName = groupPattern findFirstIn table(0)(0) match {
      case Some(groupPattern(x)) => x
      case None => "Unknown"
    }

    // Parse timetable period in the first row (title).
    val startDate = periodPattern findFirstIn table(0)(0) match {
      case Some(periodPattern(startDay, startMonth, startYear, _, _, _)) =>
        new LocalDate(
          Integer.parseInt(startYear),
          Integer.parseInt(startMonth),
          Integer.parseInt(startDay))
      case _ => new LocalDate()
    }

    val endDate = periodPattern findFirstIn table(0)(0) match {
      case Some(periodPattern(_, _, _, endDay, endMonth, endYear)) =>
        new LocalDate(
          Integer.parseInt(endYear),
          Integer.parseInt(endMonth),
          Integer.parseInt(endDay))
      case _ => new LocalDate()
    }

    // Header is the second row.
    val header = ParserHelper.rowToDateArray(table(1), datePattern)

    for (row <- table.drop(2)) {

      val timeInterval = ParserHelper.cellToTimeInterval(row(0), timePattern)

      for ((cell, index) <- row.view.zipWithIndex) {

        try {
          val schedule = cellToSchedule(cell, header(index), timeInterval, groupName)

          if ( """\w+""".r.findFirstMatchIn(schedule.courseName) != None) {
            var duplicate = false

            for (i <- 0 until schedules.length) {
              val previousSchedule = schedules(i)
              if (previousSchedule.courseName.equals(schedule.courseName)) {
                if (ParserHelper.isSchedulesSuccesives(previousSchedule, schedule)) {
                  val newSchedule =
                    new Schedule(
                      previousSchedule.courseName,
                      previousSchedule.startDateTime,
                      schedule.endDateTime,
                      previousSchedule.professor,
                      previousSchedule.room,
                      previousSchedule.group)

                  schedules = schedules.updated(i, newSchedule)

                  duplicate = true
                }
              }
            }

            if (!duplicate) {
              schedules = schedules :+ schedule
            }
          }
        } catch {
          case e: Exception => System.err.println("Error (" + e.toString + ") while parsing: " + cell)
        }
      }
    }

    schedules.toArray
  }

  def cellToSchedule(cell: String, date: LocalDate, timeInterval: TimeInterval, group: String): Schedule = {
    val startDateTime = ParserHelper.mergeDateAndTime(date, timeInterval.startTime)
    val endDateTime = ParserHelper.mergeDateAndTime(date, timeInterval.endTime)

    val course = cellParser.getCourse(cell)
    val professor = cellParser.getProfessor(cell)
    val room = cellParser.getRoom(cell)

    new Schedule(course, startDateTime, endDateTime, professor, room, group)
  }

}
