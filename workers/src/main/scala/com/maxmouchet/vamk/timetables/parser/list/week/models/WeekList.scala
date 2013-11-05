package com.maxmouchet.vamk.timetables.parser.list.week.models

import java.net.URL
import com.maxmouchet.vamk.timetables.parser.list.models.Week
import com.maxmouchet.vamk.timetables.parser.list.week.algorithms.{Strategy, ITVAMKStrategy}

case class WeekList(weeks: Array[Week])

object WeekList {
  def parse(strategy: Strategy, url: String) = {
    new WeekList(strategy.parse(url))
  }
}
