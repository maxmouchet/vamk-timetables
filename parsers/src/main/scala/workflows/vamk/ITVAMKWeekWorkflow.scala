package workflows.vamk

import workflows.base.Workflow
import models.{TimetableLinkType, Schedule}
import scala.collection.mutable
import parsers.vamk.VAMKTimetableListParser

/** Workflow for parsing all the timetables in given weeks.
  *
  * '''University:''' Vaasa University of Applied Sciences (VAMK).<br/>
  * '''Department(s):''' Information Technology (IT), Mechanical Engineering (ME).
  */
class ITVAMKWeekWorkflow extends Workflow {

  def run(args: Array[String]): Set[Schedule] = {
    val schedules = mutable.Set.empty[Schedule]

    for (weekUrl <- args) {
      val timetableUrls = new VAMKTimetableListParser(weekUrl).parse.filter(_.t == TimetableLinkType.Group).map(_.url)
      val timetableWorkflow = new ITVAMKTimetableWorkflow
      schedules ++= timetableWorkflow.run(timetableUrls)
    }

    schedules.toSet
  }

}
