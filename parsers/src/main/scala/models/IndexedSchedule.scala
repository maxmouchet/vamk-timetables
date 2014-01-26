package models

import org.joda.time.DateTime

case class IndexedSchedule(id: Int, courseName: String, startDateTime: DateTime, endDateTime: DateTime, professor: String, room: String, group: String)