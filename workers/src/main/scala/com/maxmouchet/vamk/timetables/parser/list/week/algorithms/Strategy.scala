package com.maxmouchet.vamk.timetables.parser.list.week.algorithms

import com.maxmouchet.vamk.timetables.parser.list.models.Week

trait Strategy {

  def parse(url: String): Array[Week]

}
