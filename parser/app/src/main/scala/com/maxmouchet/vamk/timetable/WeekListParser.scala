package com.maxmouchet.vamk.timetable

import scala.util.matching.Regex
import org.jsoup.Jsoup
import scala.collection.mutable.MutableList

class WeekListParser(url: String) {

  val weekPattern = new Regex("""^(\d{2}):\s(\d{1,2}\.\d{1,2}\.\d{4})\.{3}(\d{1,2}\.\d{1,2}\.\d{4})""", "number", "startDate", "endDate")

  def parse: Array[Week] = {
    var weeks = MutableList[Week]()

    val document = Jsoup.connect(url).get
    val links = document.select("table a").iterator

    while (links.hasNext) {
      val link = links.next

      weekPattern findFirstIn link.text match {
        case Some(weekPattern(number, startDate, endDate)) => {
          weeks += new Week(Integer.parseInt(number), startDate, endDate, link.attr("href"))
        }
        case _ =>
      }
    }

    weeks.toArray
  }

}