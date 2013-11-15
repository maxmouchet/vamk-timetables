package com.maxmouchet.vamk.timetables.parser.list.week.vamk

import scala.util.matching.Regex
import org.jsoup.Jsoup
import scala.collection.mutable.MutableList
import scala.collection.mutable
import java.net.URL
import com.maxmouchet.vamk.timetables.parser.list.week.models.Week
import com.maxmouchet.vamk.timetables.parser.list.week.WeekListParser

class ITVAMKWeekListParser extends WeekListParser {

  val weekPattern = new Regex( """^(\d{2}):\s(\d{1,2}\.\d{1,2}\.\d{4})\.{3}(\d{1,2}\.\d{1,2}\.\d{4})""", "number", "startDate", "endDate")

  def parse(url: String): Array[Week] = {
    var weeks = mutable.MutableList[Week]()

    val document = Jsoup.connect(url).get
    val links = document.select("table a").iterator

    while (links.hasNext) {
      val link = links.next

      weekPattern findFirstIn link.text match {
        case Some(weekPattern(number, startDate, endDate)) => {
          weeks += new Week(Integer.parseInt(number), startDate, endDate, "http://www.bet.puv.fi/schedule/P1_13_14/" + link.attr("href"))
        }
        case _ =>
      }
    }

    weeks.toArray.distinct
  }

}
