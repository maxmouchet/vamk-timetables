package com.maxmouchet.vamk.timetables.parser.timetable.settings

import scala.util.matching.Regex

object ITVAMKSettings extends VAMKSettings(
  new Regex( """(\w-\w{2,3}-\w{1,3}-?\d?)"""),
  new Regex( """^((\w+-(\w+)\s+)|((ALOIT|VARAU)\w+\s+))?(.+)"""),
  new Regex( """([^\d]+)(\d{1,2}).(\d{1,2}).(\d{4})""", "name", "day", "month", "year"),
  new Regex( """(\d{1,2}):(\d{2})-(\d{1,2}):(\d{2})""", "startHour", "startMinute", "endHour", "endMinute")
){
  def getProfessor(cell: String): String = cell.split("\n")(cell.split("\n").length - 2).trim

  def getRoom(cell: String): String = cell.split("\n").last.trim
}