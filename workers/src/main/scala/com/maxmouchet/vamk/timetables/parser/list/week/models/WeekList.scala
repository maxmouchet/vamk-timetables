package com.maxmouchet.vamk.timetables.parser.list.week.models

import java.net.URL
import com.maxmouchet.vamk.timetables.parser.list.models.Week
import com.maxmouchet.vamk.timetables.parser.list.week.vamk.{ITVAMKWeekListParser}
import com.maxmouchet.vamk.timetables.parser.list.week.WeekListParser

case class WeekList(weeks: Array[Week])

object WeekList {
  def parse(strategy: WeekListParser, url: String) = {
    new WeekList(strategy.parse(url))
  }
}
