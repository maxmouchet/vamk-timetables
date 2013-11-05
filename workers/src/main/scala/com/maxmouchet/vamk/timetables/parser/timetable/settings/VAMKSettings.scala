package com.maxmouchet.vamk.timetables.parser.timetable.settings

import scala.util.matching.Regex

abstract class VAMKSettings(val groupNamePattern: Regex, val courseNamePattern: Regex, val datePattern: Regex, val timePattern: Regex) {

  def getProfessor(cell: String): String

  def getRoom(cell: String): String

}