package com.maxmouchet.vamk.timetables.parser.timetable.models

import org.joda.time.LocalDate

case class Timetable(group: String, startDate: LocalDate, endDate: LocalDate, schedules: Array[Schedule])