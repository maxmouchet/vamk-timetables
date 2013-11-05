package com.maxmouchet.vamk.timetables.parser.timetable.models

import com.maxmouchet.vamk.timetables.parser.timetable.algorithms.Strategy

case class Timetable(schedules: Array[Schedule])

object Timetable {
  def parse(strategy: Strategy) = {
    new Timetable(strategy.parse)
  }
}