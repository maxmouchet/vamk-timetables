package com.maxmouchet.vamk.timetables.parser.list.week.vamk

import scala.util.matching.Regex
import com.maxmouchet.vamk.timetables.parser.list.week.models.Week
import scala.collection.mutable
import org.jsoup.Jsoup
import com.maxmouchet.vamk.timetables.parser.list.week.WeekListParser

class IBVAMKWeekListParser extends WeekListParser {

  val weekPattern = new Regex( """(\d{2})""")

  def parse(url: String): Array[Week] = {
    var weeks = mutable.MutableList[Week]()

    val document = Jsoup.connect(url).get
    val links = document.select("table a").iterator

    while (links.hasNext) {
      val link = links.next

      weekPattern findFirstIn link.text match {
        case Some(number) => {
          weeks += new Week(Integer.parseInt(number), "", "", "http://www.bet.puv.fi/studies/lukujarj/LV_13_14/" + link.attr("href"))
        }
        case _ =>
      }
    }

    weeks.toArray.distinct
  }

}
