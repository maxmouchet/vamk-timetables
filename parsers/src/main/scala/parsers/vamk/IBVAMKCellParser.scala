package parsers.vamk

import scala.util.matching.Regex
import parsers.base.CellParser

object IBVAMKCellParser extends CellParser {

  val coursePattern = new Regex( """^((\w+-(\w+)\s+)|((ALOIT|VARAU)\w+\s+))?(.+)""")

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
