package com.maxmouchet.vamk.timetables.parser.timetable.cell.vamk

import scala.util.matching.Regex
import com.maxmouchet.vamk.timetables.parser.timetable.cell.CellParser

class ITVAMKCellParser extends CellParser with VAMKCellParser {

  def getCourse(cell: String): String = coursePattern.findFirstMatchIn(cell).get.group(6).trim

  def getProfessor(cell: String): String = cell.split("\n")(cell.split("\n").length - 2).trim

  def getRoom(cell: String): String = cell.split("\n").last.trim

}
