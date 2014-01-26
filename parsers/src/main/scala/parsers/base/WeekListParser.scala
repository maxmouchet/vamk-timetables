package parsers.base

import models.Week

trait WeekListParser {

  def parse: Array[Week]

}
