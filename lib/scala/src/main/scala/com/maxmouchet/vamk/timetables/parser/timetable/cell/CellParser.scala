package com.maxmouchet.vamk.timetables.parser.timetable.cell

trait CellParser {

  def getCourse(cell: String): String

  def getProfessor(cell: String): String

  def getRoom(cell: String): String

}
