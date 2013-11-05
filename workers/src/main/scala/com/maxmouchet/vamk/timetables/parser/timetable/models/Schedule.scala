package com.maxmouchet.vamk.timetables.parser.timetable.models

import org.joda.time.DateTime
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.json4s.DefaultFormats

class Schedule(val courseName: String, val startDate: DateTime, val endDate: DateTime, val professor: String, val room: String, val group: String) {

  def toJSON: String = {
    compact(render(Map(
      "courseName" -> courseName,
      "startDate" -> startDate.toString(),
      "endDate" -> endDate.toString(),
      "professor" -> professor,
      "room" -> room,
      "group" -> group
    )))
  }

}

object Schedule {
  def fromJSON(json: String) = {
    implicit val formats = DefaultFormats
    val map = parse(json).extract[Map[String, String]]
    new Schedule(map("courseName"), DateTime.parse(map("startDate")), DateTime.parse(map("endDate")), map("professor"), map("room"), map("group"))
  }
}