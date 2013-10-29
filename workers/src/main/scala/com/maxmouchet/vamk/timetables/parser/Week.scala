package com.maxmouchet.vamk.timetables.parser

class Week(val number: Integer, val startDate: String, val endDate: String, val url: String) {

  def getTimetableList: Array[TimetableLink] = {
    new TimetableListParser(url).parse
  }

}