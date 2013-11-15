package com.maxmouchet.vamk.timetables.parser.timetable.vamk

import com.maxmouchet.vamk.timetables.parser.timetable.{ParserHelper, TimetableParser}
import com.maxmouchet.vamk.timetables.parser.timetable.models.{Timetable, TimeInterval, Schedule}
import com.maxmouchet.vamk.timetables.parser.table.HTMLTableParser
import scala.util.matching.Regex
import org.joda.time.LocalDate
import com.maxmouchet.vamk.timetables.parser.table.sources.TableSource
import com.maxmouchet.vamk.timetables.parser.timetable.cell.CellParser

class VAMKTimetableParser(tableSource: TableSource, cellParser: CellParser) extends TimetableParser {

  val groupPattern = new Regex( """(\w-\w{2,3}-\w{1,3}-?\d?)""")
  val datePattern = new Regex( """([^\d]+)(\d{1,2}).(\d{1,2}).(\d{4})""", "name", "day", "month", "year")
  val timePattern = new Regex( """(\d{1,2}):(\d{2})-(\d{1,2}):(\d{2})""", "startHour", "startMinute", "endHour", "endMinute")

  def parse: Timetable = {
    var schedules = Vector.empty[Schedule]

    val table = tableSource.getTable

    // Parse group name in the first row (title).
    val groupName = groupPattern findFirstIn table(0)(0) match {
      case Some(groupPattern(x)) => x
      case None => "Unknown"
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

            if (!duplicate) {
              schedules = schedules :+ schedule
            }
          }
        } catch {
          case e: Exception => System.err.println("Error (" + e.toString + ") while parsing: " + cell)
        }
      }
    }

    new Timetable(schedules.toArray)
  }

  def cellToSchedule(cell: String, date: LocalDate, timeInterval: TimeInterval, group: String): Schedule = {
    val startDate = ParserHelper.mergeDateAndTime(date, timeInterval.startTime)
    val endDate = ParserHelper.mergeDateAndTime(date, timeInterval.endTime)

    val course = cellParser.getCourse(cell)
    val professor = cellParser.getProfessor(cell)
    val room = cellParser.getRoom(cell)

    new Schedule(course, startDate, endDate, professor, room, group)
  }
}
