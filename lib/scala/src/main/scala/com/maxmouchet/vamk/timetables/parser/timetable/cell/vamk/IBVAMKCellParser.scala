package com.maxmouchet.vamk.timetables.parser.timetable.cell.vamk

import scala.util.matching.Regex
import com.maxmouchet.vamk.timetables.parser.timetable.cell.CellParser

class IBVAMKCellParser extends CellParser with VAMKCellParser {

  def getCourse(cell: String): String = coursePattern.findFirstMatchIn(cell).get.group(6).trim

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
    val roomNamePattern = new Regex( """(^[A-Z]+\d+)""")

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
