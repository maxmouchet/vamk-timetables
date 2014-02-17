package parsers.vamk

import org.scalatest._

class ITVAMKCellParserTest extends FunSpec {

  describe("A CellParser for IT timetables at VAMK") {

    val cellParser = ITVAMKCellParser
    val cell = """
                 |km09-IMEP0105a Chemistry in Energy Technology
                 |I-IT-3N2
                 |I-ME-3N
                 |I-ME-4N
                 |I-MX-4N
                 |RNI
                 |WC1161""".stripMargin

    it("should return the course name") {
      assert(cellParser.getCourse(cell).equals("Chemistry in Energy Technology"))
    }

    it("should return the professor") {
      assert(cellParser.getProfessor(cell).equals("RNI"))
    }

    it("should return the room") {
      assert(cellParser.getRoom(cell).equals("WC1161"))
    }
  }

}
