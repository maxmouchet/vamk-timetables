package com.maxmouchet.vamk.timetables.discoverer

import java.net.URL
import com.maxmouchet.vamk.timetables.parser.{WeekList, WeekListParser}

class Discoverer(val sources: Array[URL], val output: Output) {

  def discover = {
    for (source <- sources) {
      for (week <- WeekList.fromURL(source).weeks) {
        for (link <- week.getTimetableList) {
          output.write(link.toJSON)
        }
      }
    }
  }

}
