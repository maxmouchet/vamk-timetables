package com.maxmouchet.vamk.timetables.parser.list.timetable

import com.maxmouchet.vamk.timetables.parser.list.timetable.models.TimetableLink

trait TimetableListParser {

  def parse: Array[TimetableLink]

}
