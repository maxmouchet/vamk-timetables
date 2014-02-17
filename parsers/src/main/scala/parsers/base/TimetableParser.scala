package parsers.base

import models.Schedule

trait TimetableParser {

  def parse: Array[Schedule]

}
