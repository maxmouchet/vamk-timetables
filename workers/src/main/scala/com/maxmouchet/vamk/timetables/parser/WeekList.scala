package com.maxmouchet.vamk.timetables.parser

import java.net.URL

case class WeekList(weeks: Array[Week])

object WeekList {
  def fromURL(url: URL) = {
    new WeekList(new WeekListParser(url.toString).parse)
  }
}
