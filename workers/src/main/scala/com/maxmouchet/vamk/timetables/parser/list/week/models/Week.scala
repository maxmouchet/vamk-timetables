package com.maxmouchet.vamk.timetables.parser.list.models

import com.maxmouchet.vamk.timetables.parser.list.timetable.models.TimetableLink
import com.maxmouchet.vamk.timetables.parser.list.timetable.algorithms.TimetableListParser

class Week(val number: Integer, val startDate: String, val endDate: String, val url: String) {

  def getTimetableList: Array[TimetableLink] = {
    new TimetableListParser(url).parse
  }

}