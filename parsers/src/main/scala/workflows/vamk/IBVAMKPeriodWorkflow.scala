package workflows.vamk

import workflows.base.Workflow
import models.Schedule
import scala.collection.mutable
import parsers.vamk.IBVAMKWeekListParser

/** Workflow for parsing all the timetables in the given periods.
  *
  * '''University:''' Vaasa University of Applied Sciences (VAMK).<br/>
  * '''Department(s):''' International Business (IB).
  */
class IBVAMKPeriodWorkflow extends Workflow {

  def run(args: Array[String]): Set[Schedule] = {
    val schedules = mutable.Set.empty[Schedule]

    for (periodUrl <- args) {
      val weekUrls = new IBVAMKWeekListParser(periodUrl).parse.map(_.url)
      val weekWorkflow = new IBVAMKWeekWorkflow
      schedules ++= weekWorkflow.run(weekUrls)
    }

    schedules.toSet
  }

}
