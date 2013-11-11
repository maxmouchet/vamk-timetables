package com.maxmouchet.vamk.timetables.parser.list.models

import com.maxmouchet.vamk.timetables.parser.list.timetable.models.TimetableLink
import com.maxmouchet.vamk.timetables.parser.list.timetable.vamk.VAMKTimetableListParser

class Week(val number: Integer, val startDate: String, val endDate: String, val url: String) {

  def getTimetableList: Array[TimetableLink] = {
    new VAMKTimetableListParser(url).parse
  }

}