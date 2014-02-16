package parsers.vamk

import org.jsoup.Jsoup

import scala.collection.JavaConversions._
import scala.collection.mutable
import java.io.File
import models.{TimetableLink, TimetableLinkType}
import parsers.base.TimetableListParser
import scala.util.matching.Regex

/** Parse a list of timetables.
  *
  * '''University:''' Vaasa University of Applied Sciences (VAMK).
  * @param url URL of the timetables list.
  */
class VAMKTimetableListParser(url: String) extends TimetableListParser {

  // TODO: Improve the groupPattern for business.
  val groupPattern = new Regex( """(\w-\w{2,3}-\w{1,3}-?\d?)""")

  def parse: Array[TimetableLink] = {
    var links = mutable.MutableList[TimetableLink]()

    val baseUrl = new File(url).getParent.replace("http:/", "http://") + '/'

    val document = Jsoup.connect(url).get

    for (link <- document.select("table a")) {
      groupPattern findFirstIn link.text match {
        case Some(groupPattern(x)) => links += new TimetableLink(TimetableLinkType.Group, link.text, baseUrl + link.attr("href"))
        case None =>
      }
    }

    links.toArray
  }

}
