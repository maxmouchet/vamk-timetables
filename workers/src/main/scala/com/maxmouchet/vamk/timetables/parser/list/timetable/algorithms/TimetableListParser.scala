package com.maxmouchet.vamk.timetables.parser.list.timetable.algorithms

import org.jsoup.Jsoup
import scala.collection.mutable.MutableList
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

import scala.collection.JavaConversions._
import scala.collection.mutable
import java.io.File
import com.maxmouchet.vamk.timetables.parser.list.timetable.models.{TimetableLink, TimetableLinkType}

class TimetableListParser(url: String) {

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