package com.maxmouchet.vamk.timetables.parser.timetable.algorithms

import scala.util.matching.Regex
import org.joda.time.LocalDate
import com.maxmouchet.vamk.timetables.parser.timetable.settings.{VAMKSettings}
import com.maxmouchet.vamk.timetables.parser.timetable.models.{TimeInterval, Schedule}
import com.maxmouchet.vamk.timetables.parser.timetable.ParserHelper
import com.maxmouchet.vamk.timetables.parser.table.TableParser

class VAMKStrategy(url: String, settings: VAMKSettings) extends Strategy {

  def parse: Array[Schedule] = {
    var schedules = Vector.empty[Schedule]

    val table = new TableParser(url, com.maxmouchet.vamk.timetables.parser.table.settings.VAMKSettings).parse

    // Parse group name in the first row (title).
    val groupName = settings.groupNamePattern findFirstIn table(0)(0) match {
      case Some(settings.groupNamePattern(x)) => x
      case None => "Unknown"
    }

    // Header is the second row.
    val header = ParserHelper.rowToDateArray(table(1), settings.datePattern)

    for (row <- table.drop(2)) {

      val timeInterval = ParserHelper.cellToTimeInterval(row(0), settings.timePattern)

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
          case _: Throwable => System.err.println("Error while parsing: " + cell)
        }
      }
    }

    schedules.toArray
  }

  def cellToSchedule(cell: String, date: LocalDate, timeInterval: TimeInterval, group: String): Schedule = {
    val startDate = ParserHelper.mergeDateAndTime(date, timeInterval.startTime)
    val endDate = ParserHelper.mergeDateAndTime(date, timeInterval.endTime)

    val courseName = settings.courseNamePattern.findFirstMatchIn(cell).get.group(6).trim

    val professor = settings.getProfessor(cell)
    val room = settings.getRoom(cell)

    new Schedule(courseName, startDate, endDate, professor, room, group)
  }

}
