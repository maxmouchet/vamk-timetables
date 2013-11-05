package com.maxmouchet.vamk.timetables.parser.timetable.settings

import scala.util.matching.Regex

object IBVAMKSettings extends VAMKSettings(
  new Regex( """(\w-\w{2,3}-\w{1,3}-?\d?)"""),
  new Regex( """^((\w+-(\w+)\s+)|((ALOIT|VARAU)\w+\s+))?(.+)"""),
  new Regex( """([^\d]+)(\d{1,2}).(\d{1,2}).(\d{4})""", "name", "day", "month", "year"),
  new Regex( """(\d{1,2}):(\d{2})-(\d{1,2}):(\d{2})""", "startHour", "startMinute", "endHour", "endMinute")
) {

  def getProfessor(cell: String): String = {
    val professorNamePattern = new Regex( """(^[A-Z]{2,4}$)""")

    var professorName = ""
    var i = 0

    while (professorName.equals("") && i < cell.split("\n").length) {
      professorName = professorNamePattern findFirstIn cell.split("\n")(i).trim match {
        case Some(professorNamePattern(x)) => x
        case None => ""
      }

      i += 1
    }

    professorName
  }

  def getRoom(cell: String): String = {
    val roomNamePattern = new Regex("""(^[A-Z]+\d+)""")

    var roomName = ""
    var i = 0

    while (roomName.equals("") && i < cell.split("\n").length) {
      roomName = roomNamePattern findFirstIn cell.split("\n")(i).trim match {
        case Some(roomNamePattern(x)) => x
        case None => ""
      }

      i += 1
    }

    roomName
  }
}
