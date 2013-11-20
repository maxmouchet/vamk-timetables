package com.maxmouchet.vamk.timetables.parser.list.timetable.models

import com.maxmouchet.vamk.timetables.parser.list.timetable.models.TimetableLinkType.TimetableLinkType

case class TimetableLink(t: TimetableLinkType, name: String, url: String)