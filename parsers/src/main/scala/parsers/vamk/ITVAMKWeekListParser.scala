package parsers.vamk

import scala.util.matching.Regex
import org.jsoup.Jsoup
import scala.collection.mutable
import models.Week
import parsers.base.WeekListParser
import java.io.File

class ITVAMKWeekListParser(url: String) extends WeekListParser {

  val weekPattern = new Regex( """^(\d{2}):\s(\d{1,2}\.\d{1,2}\.\d{4})\.{3}(\d{1,2}\.\d{1,2}\.\d{4})""", "number", "startDate", "endDate")

  def parse: Array[Week] = {
    var weeks = mutable.MutableList[Week]()

    val document = Jsoup.connect(url).get
    val links = document.select("table a").iterator

    val baseUrl = new File(url).getParent.replace("http:/", "http://") + '/'

    while (links.hasNext) {
      val link = links.next

      weekPattern findFirstIn link.text match {
        case Some(weekPattern(number, startDate, endDate)) => {
          weeks += new Week(Integer.parseInt(number), startDate, endDate, baseUrl + link.attr("href"))
        }
        case _ =>
      }
    }

    weeks.toArray.distinct
  }

}
