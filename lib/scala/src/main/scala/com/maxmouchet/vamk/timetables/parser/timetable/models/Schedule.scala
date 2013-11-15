package com.maxmouchet.vamk.timetables.parser.timetable.models

import org.joda.time.DateTime

case class Schedule(courseName: String, startDate: DateTime, endDate: DateTime, professor: String, room: String, group: String)