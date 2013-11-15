package com.maxmouchet.vamk.timetables.parser.list.week.models

import com.maxmouchet.vamk.timetables.parser.list.week.WeekListParser

case class WeekList(weeks: Array[Week])

object WeekList {
  def parse(strategy: WeekListParser, url: String) = {
    new WeekList(strategy.parse(url))
  }
}
