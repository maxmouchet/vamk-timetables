package com.maxmouchet.vamk.timetables.parser.timetable

import com.maxmouchet.vamk.timetables.parser.timetable.models.{Timetable}

trait TimetableParser {

  def parse: Timetable

}
