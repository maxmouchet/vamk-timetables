package com.maxmouchet.vamk.timetables.parser

import scala.util.matching.Regex
import org.jsoup.Jsoup
import scala.collection.mutable.MutableList
import scala.collection.mutable
import java.net.URL

class WeekListParser(url: String) {

  val itWeekPattern = new Regex( """^(\d{2}):\s(\d{1,2}\.\d{1,2}\.\d{4})\.{3}(\d{1,2}\.\d{1,2}\.\d{4})""", "number", "startDate", "endDate")
  val ibWeekPattern = new Regex("""(\d{2})""")

  def parse: Array[Week] = {
    var weeks = mutable.MutableList[Week]()

    val document = Jsoup.connect(url).get
    val links = document.select("table a").iterator

    while (links.hasNext) {
      val link = links.next

      itWeekPattern findFirstIn link.text match {
        case Some(itWeekPattern(number, startDate, endDate)) => {
          weeks += new Week(Integer.parseInt(number), startDate, endDate, "http://www.bet.puv.fi/schedule/P1_13_14/" + link.attr("href"))
        }
        case _ => ibWeekPattern findFirstIn link.text match {
          case Some(number) => {
            weeks += new Week(Integer.parseInt(number), "", "", "http://www.bet.puv.fi/studies/lukujarj/LV_13_14/" + link.attr("href"))
          }
          case _ =>
        }
      }
    }

    weeks.toArray.distinct
  }

}
