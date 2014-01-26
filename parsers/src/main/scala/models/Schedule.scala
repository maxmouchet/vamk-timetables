package models

import org.joda.time.DateTime

case class Schedule(courseName: String, startDateTime: DateTime, endDateTime: DateTime, professor: String, room: String, group: String)