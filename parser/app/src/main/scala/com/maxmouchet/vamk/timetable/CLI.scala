package com.maxmouchet.vamk.timetable

import java.io.File
import java.net.URL

object CLI {

  def main(args: Array[String]): Unit = {
    val parser = new scopt.OptionParser[Config]("vamk-timetable-parser") {
      head("vamk-timetable-parser", "0.1")

      opt[Unit]("pretty-print") action {
        (_, c) =>
          c.copy(prettyprint = true)
      } text ("verbose is a flag")

      arg[String]("url ...") unbounded() action {
        (x, c) => c.copy(urls = c.urls :+ x)
      } text ("URLs to parse")

      //      opt[Int]('f', "foo") action {
      //        (x, c) =>
      //          c.copy(foo = x)
      //      } text ("foo is an integer property")
      //      opt[File]('o', "out") required() valueName ("<file>") action {
      //        (x, c) =>
      //          c.copy(out = x)
      //      } text ("out is a required file property")
      //      opt[(String, Int)]("max") action {
      //        case ((k, v), c) =>
      //          c.copy(libName = k, maxCount = v)
      //      } validate {
      //        x =>
      //          if (x._2 > 0) success else failure("Value <max> must be >0")
      //      } keyValueName("<libname>", "<max>") text ("maximum count for <libname>")
      //      opt[Unit]("verbose") action {
      //        (_, c) =>
      //          c.copy(verbose = true)
      //      } text ("verbose is a flag")
      //      opt[Unit]("debug") hidden() action {
      //        (_, c) =>
      //          c.copy(debug = true)
      //      } text ("this option is hidden in any usage text")
      //      note("some notes.\n")
      //      help("help") text ("prints this usage text")
      //      arg[File]("<file>...") unbounded() optional() action {
      //        (x, c) =>
      //          c.copy(files = c.files :+ x)
      //      } text ("optional unbounded args")
      //      cmd("update") action {
      //        (_, c) =>
      //          c.copy(mode = "update")
      //      } text ("update is a command.") children(
      //        opt[Unit]("not-keepalive") abbr ("nk") action {
      //          (_, c) =>
      //            c.copy(keepalive = false)
      //        } text ("disable keepalive"),
      //        opt[Boolean]("xyz") action {
      //          (x, c) =>
      //            c.copy(xyz = x)
      //        } text ("xyz is a boolean property")
      //      )
    }
    // parser.parse returns Option[C]
    parser.parse(args, Config()) map {
      config =>
        for (url <- config.urls) {
          val table = new TableParser(url, "table[cellspacing=1]", "tr", "td").parse
          val schedules = new TimetableParser(table).parse

          if (config.prettyprint) {
            for (schedule <- schedules) {
              println(scheduleToPrettyString(schedule))
              //            allSchedules += schedule
            }
          } else {
            for (schedule <- schedules) {
              println(schedule.toString)
              //            allSchedules += schedule
            }
          }


        }
    } getOrElse {
      // arguments are bad, usage message will have been displayed
    }
  }

  def scheduleToPrettyString(schedule: Schedule): String = {
    var output = ""

    output += schedule.courseName + "\n"
    output += "Start on " + schedule.startDate + "\n"
    output += "Ends  on " + schedule.endDate + "\n"
    output += "Group is " + schedule.group + "\n"
    output += "Professor is " + schedule.professor + "\n"
    output += "Room is " + schedule.room + "\n"

    output
  }

}
