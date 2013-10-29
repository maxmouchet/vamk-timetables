package com.maxmouchet.vamk.timetables.parser

import com.maxmouchet.vamk.timetables.parser.TimetableLinkType.TimetableLinkType
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s.DefaultFormats

class TimetableLink(val t: TimetableLinkType, val name: String, val url: String) {

  def toJSON: String = {
    compact(render(Map(
      "type" -> t.toString,
      "name" -> name,
      "url" -> url
    )))
  }

}

object TimetableLink {
  def fromJSON(json: String) = {
    implicit val formats = DefaultFormats
    val map = parse(json).extract[Map[String, String]]
    new TimetableLink(TimetableLinkType.Group, map("name"), map("url"))
  }
}