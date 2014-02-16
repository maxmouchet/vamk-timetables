package outputs.base

import models.Schedule

abstract class Output {

  def execute(args: Array[String], schedules: Array[Schedule])

}
