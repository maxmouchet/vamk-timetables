package com.maxmouchet.vamk.timetables.parser.timetable.algorithms

import com.maxmouchet.vamk.timetables.parser.timetable.models.Schedule

trait Strategy {

  def parse: Array[Schedule]

}
