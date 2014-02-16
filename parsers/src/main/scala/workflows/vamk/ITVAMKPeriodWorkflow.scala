package workflows.vamk

import workflows.base.Workflow
import models.Schedule
import scala.collection.mutable
import parsers.vamk.ITVAMKWeekListParser

/** Workflow for parsing all the timetables in the given periods.
  *
  * '''University:''' Vaasa University of Applied Sciences (VAMK).<br/>
  * '''Department(s):''' Information Technology (IT), Mechanical Engineering (ME).
  */
class ITVAMKPeriodWorkflow extends Workflow {

  def run(args: Array[String]): Set[Schedule] = {
    val schedules = mutable.Set.empty[Schedule]

    for (periodUrl <- args) {
      val weekUrls = new ITVAMKWeekListParser(periodUrl).parse.map(_.url)
      val weekWorkflow = new ITVAMKWeekWorkflow
      schedules ++= weekWorkflow.run(weekUrls)
    }

    schedules.toSet
  }

}
