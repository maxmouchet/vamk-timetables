package parsers.vamk

import scala.util.matching.Regex
import parsers.base.CellParser

object ITVAMKCellParser extends CellParser {

  val coursePattern = new Regex( """^\s*((\w+-(\w+)\s+)|((ALOIT|VARAU)\w+\s+))?(.+)""")

  def getCourse(cell: String): String = coursePattern.findFirstMatchIn(cell).get.group(6).trim

  def getProfessor(cell: String): String = cell.split("\n")(cell.split("\n").length - 2).trim

  def getRoom(cell: String): String = cell.split("\n").last.trim

}