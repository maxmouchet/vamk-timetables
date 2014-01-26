package parsers.vamk

import org.jsoup.Jsoup

import scala.collection.JavaConversions._
import scala.collection.mutable
import java.io.File
import models.{TimetableLink, TimetableLinkType}
import parsers.base.TimetableListParser

class VAMKTimetableListParser(url: String) extends TimetableListParser {

  def parse: Array[TimetableLink] = {
    var links = mutable.MutableList[TimetableLink]()

    val baseUrl = new File(url).getParent.replace("http:/", "http://") + '/'

    val document = Jsoup.connect(url).get

    for (link <- document.select("table a")) {
      links += new TimetableLink(TimetableLinkType.Group, link.text, baseUrl + link.attr("href"))
    }

    links.toArray
  }

}
