package outputs

import models.Schedule
import outputs.base.Output

class ConsoleOutput extends Output {

  def execute(args: Array[String], schedules: Array[Schedule]) = {
    for (schedule <- schedules) {
      System.out.println(schedule)
    }
  }

}
