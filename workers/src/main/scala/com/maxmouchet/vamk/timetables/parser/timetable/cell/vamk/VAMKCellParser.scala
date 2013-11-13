package com.maxmouchet.vamk.timetables.parser.timetable.cell.vamk

import scala.util.matching.Regex

trait VAMKCellParser {

  val coursePattern = new Regex( """^((\w+-(\w+)\s+)|((ALOIT|VARAU)\w+\s+))?(.+)""")

}
