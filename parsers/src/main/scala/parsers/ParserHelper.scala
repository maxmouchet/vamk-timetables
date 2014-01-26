package parsers

import java.lang.Boolean
import org.joda.time.{DateTime, LocalDate, LocalTime}
import scala.util.matching.Regex
import models.{Schedule, TimeInterval}

object ParserHelper {

  def cellToArray(cell: String): Array[String] = cell.split('\n').map((l: String) => l.trim)

  def cellToTimeInterval(cell: String, timePattern: Regex): TimeInterval = {
    val timeInterval = timePattern findFirstIn cell match {

      case Some(timePattern(startHour, startMinute, endHour, endMinute)) => {
        val startTime = new LocalTime(
          Integer.parseInt(startHour),
          Integer.parseInt(startMinute))

        val endTime = new LocalTime(
          Integer.parseInt(endHour),
          Integer.parseInt(endMinute))

        new TimeInterval(startTime, endTime)
      }

      case _ => new TimeInterval(new LocalTime, new LocalTime)
    }

    timeInterval
  }

  def rowToDateArray(row: Array[String], datePattern: Regex): Array[LocalDate] = {
    row.map((cell: String) => {
      datePattern findFirstIn cell match {
        case Some(datePattern(_, day, month, year)) =>
          new LocalDate(
            Integer.parseInt(year),
            Integer.parseInt(month),
            Integer.parseInt(day))
        case _ => new LocalDate()
      }
    })
  }

  def mergeDateAndTime(date: LocalDate, time: LocalTime) = {
    new DateTime(
      date.year.get,
      date.monthOfYear.get,
      date.dayOfMonth.get,
      time.hourOfDay.get,
      time.minuteOfHour.get)
  }

  /**
   * Determine if two schedules are consecutive in the time.
   * @param schedule1
   * @param schedule2
   * @return true if the schedules are consecutives.
   */
  def isSchedulesSuccesives(schedule1: Schedule, schedule2: Schedule): Boolean = {
    if (schedule1.startDateTime.dayOfWeek != schedule2.startDateTime.dayOfWeek) {
      return false
    }

    //      TODO: Verify which course is the first in time.

    schedule1.endDateTime.secondOfDay.get + 900 >= schedule2.startDateTime.secondOfDay.get
  }

}
