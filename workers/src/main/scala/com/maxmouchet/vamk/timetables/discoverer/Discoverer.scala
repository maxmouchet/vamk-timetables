package com.maxmouchet.vamk.timetables.discoverer

import java.net.URL
import com.maxmouchet.vamk.timetables.parser.list.week.models.WeekList
import com.maxmouchet.vamk.timetables.discoverer.outputs.Output
import com.maxmouchet.vamk.timetables.parser.list.week.WeekListParser

class Discoverer(val sources: Map[String, WeekListParser], val output: Output) {

  def discover = {
    for (source <- sources) {
      for (week <- WeekList.parse(source._2, source._1).weeks) {
        for (link <- week.getTimetableList) {
          output.write(link.toJSON)
        }
      }
    }
  }

}
