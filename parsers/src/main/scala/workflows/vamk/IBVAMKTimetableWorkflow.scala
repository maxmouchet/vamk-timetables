package workflows.vamk

import workflows.base.Workflow
import models.Schedule
import scala.collection.mutable
import parsers.generic.HTMLTableParser
import parsers.vamk.{IBVAMKCellParser, VAMKTimetableParser}

/** Workflow for parsing the given timetables.
  *
  * '''University:''' Vaasa University of Applied Sciences (VAMK).<br/>
  * '''Department(s):''' International Business (IB).
  */
class IBVAMKTimetableWorkflow extends Workflow {

  def run(args: Array[String]): Set[Schedule] = {
    val schedules = mutable.Set.empty[Schedule]

    for (timetableUrl <- args) {
      val tableParser = new HTMLTableParser(timetableUrl, "table[cellspacing=1]", "tr", "td")
      schedules ++= new VAMKTimetableParser(tableParser, IBVAMKCellParser).parse
    }

    schedules.toSet
  }

}
