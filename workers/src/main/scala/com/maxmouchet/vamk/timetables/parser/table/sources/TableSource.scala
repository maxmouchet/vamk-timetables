package com.maxmouchet.vamk.timetables.parser.table.sources

import com.maxmouchet.vamk.timetables.parser.timetable.models.Schedule

trait TableSource {

  def getTable: Array[Array[String]]

}
