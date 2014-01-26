package parsers.vamk

import scala.util.matching.Regex
import scala.collection.mutable

import org.jsoup.Jsoup

import models.Week
import parsers.base.WeekListParser
import java.io.File

class IBVAMKWeekListParser(url: String) extends WeekListParser {

  val weekPattern = new Regex( """(\d{2})""")

  def parse: Array[Week] = {
    var weeks = mutable.MutableList[Week]()

    val document = Jsoup.connect(url).get
    val links = document.select("table a").iterator

    val baseUrl = new File(url).getParent.replace("http:/", "http://") + '/'

    while (links.hasNext) {
      val link = links.next

      weekPattern findFirstIn link.text match {
        case Some(number) => {
          weeks += new Week(Integer.parseInt(number), "", "", baseUrl + link.attr("href"))
        }
        case _ =>
      }
    }

    weeks.toArray.distinct
  }

}
