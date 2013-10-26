package com.maxmouchet.vamk.timetable

import org.jsoup.Jsoup
import scala.collection.mutable.MutableList
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

import scala.collection.JavaConversions._

class TimetableListParser(url: String) {

  def parse: Array[TimetableLink] = {
    var links = MutableList[TimetableLink]()

    val document = Jsoup.connect(url).get
    
    for (link <- document.select("table a")) {
      links += new TimetableLink(TimetableLinkType.Group, link.text, link.attr("href"))
    }

    links.toArray
  }

}