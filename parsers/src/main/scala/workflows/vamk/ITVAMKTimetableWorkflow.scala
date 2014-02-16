package workflows.vamk

import workflows.base.Workflow
import models.Schedule
import parsers.generic.HTMLTableParser
import parsers.vamk.{ITVAMKCellParser, VAMKTimetableParser}
import scala.collection.mutable

/** Workflow for parsing the given timetables.
  *
  * '''University:''' Vaasa University of Applied Sciences (VAMK).<br/>
  * '''Department(s):''' Information Technology (IT), Mechanical Engineering (ME).
  */
class ITVAMKTimetableWorkflow extends Workflow {

  def run(args: Array[String]): Set[Schedule] = {
    val schedules = mutable.Set.empty[Schedule]

    for (timetableUrl <- args) {
      val tableParser = new HTMLTableParser(timetableUrl, "table[cellspacing=1]", "tr", "td")
      schedules ++= new VAMKTimetableParser(tableParser, ITVAMKCellParser).parse
    }

    schedules.toSet
  }

}
