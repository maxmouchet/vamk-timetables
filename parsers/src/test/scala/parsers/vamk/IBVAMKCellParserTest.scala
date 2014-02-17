package parsers.vamk

import org.scalatest._

class IBVAMKCellParserTest extends FunSpec {

  describe("A CellParser for IB timetables at VAMK") {

    val cellParser = IBVAMKCellParser
    val cell = """
                 |European Business Management
                 |RA315
                 |T-IB-2-1
                 |SLM""".stripMargin

    it("should return the course name") {
      assert(cellParser.getCourse(cell).equals("European Business Management"))
    }

    it("should return the professor") {
      assert(cellParser.getProfessor(cell).equals("SLM"))
    }

    it("should return the room") {
      assert(cellParser.getRoom(cell).equals("RA315"))
    }
  }

}
