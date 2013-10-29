package com.maxmouchet.vamk.timetables.parser

import com.maxmouchet.vamk.timetables.parser.Schedule

case class Timetable(schedules: Array[Schedule])

object Timetable {
  def fromURL(url: String) = {
    new Timetable(new TimetableParser(url).parse)
  }
}