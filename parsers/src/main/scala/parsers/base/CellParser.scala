package parsers.base

trait CellParser {

  def getCourse(cell: String): String

  def getProfessor(cell: String): String

  def getRoom(cell: String): String

}