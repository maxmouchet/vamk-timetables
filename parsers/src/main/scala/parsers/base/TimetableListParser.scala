package parsers.base

import models.TimetableLink

trait TimetableListParser {

  def parse: Array[TimetableLink]

}
