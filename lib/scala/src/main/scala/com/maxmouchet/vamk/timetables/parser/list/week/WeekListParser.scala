package com.maxmouchet.vamk.timetables.parser.list.week

import com.maxmouchet.vamk.timetables.parser.list.week.models.Week

trait WeekListParser {

  def parse(url: String): Array[Week]

}
