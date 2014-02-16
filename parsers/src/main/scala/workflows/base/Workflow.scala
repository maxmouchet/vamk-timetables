package workflows.base

import models.Schedule

/** A Workflow takes zero or more arguments as an Array of String, and returns a Set of Schedules.
  * Set is used to guarantees that the schedules will be unique.
  */
abstract class Workflow {

  def run(args: Array[String]): Set[Schedule]

}
