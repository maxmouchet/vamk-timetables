import java.io.{PrintWriter, File}
import models.{IndexedSchedule, Schedule}
import parsers.generic.HTMLTableParser
import parsers.vamk.{VAMKTimetableListParser, ITVAMKWeekListParser, VAMKTimetableParser, ITVAMKCellParser}

import org.json4s.native.Serialization.write

object Test extends App {
  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all

  val weekLists = List("http://www.bet.puv.fi/schedule/kevat2014/mfw.htm")
  //  val weekLists = List()

  val connection = new InMemoryConnection

  connection.connect
  connection.initSchema

  //  val testSchedule = new Schedule("test", new DateTime(), new DateTime(), "MG", "WB201", "I-IT-3N1")
  //  val testSchedule2 = new Schedule("test", new DateTime(), new DateTime(), "MG", "WB221", "I-IT-3N2")
  //  val testSchedule3 = new Schedule("test123", new DateTime(), new DateTime(), "MG", "WB201", "I-IT-3N1")
  //
  //  connection.addSchedule(testSchedule)
  //  connection.addSchedule(testSchedule2)
  //  connection.addSchedule(testSchedule3)

  for (weekList <- weekLists) {
    val weeks = new ITVAMKWeekListParser(weekList).parse

    for (week <- weeks.par) {
      val timetableLinks = new VAMKTimetableListParser(week.url).parse

      for (timetableLink <- timetableLinks.par) {
        try {
          val tableParser = new HTMLTableParser(timetableLink.url, "table[cellspacing=1]", "tr", "td")

          val schedules = new VAMKTimetableParser(tableParser, ITVAMKCellParser).parse

          for (schedule <- schedules) {
            if (schedule.group != "Unknown") {
              connection.addSchedule(schedule)
            }
          }
          System.out.println("Parsed " + schedules.length + " schedules from " + timetableLink.url)
        } catch {
          case e: Exception => System.err.println("Error (" + e.toString + ") while parsing: " + timetableLink.url)
        }
      }
    }
  }

  val schedules = connection.getDistinctSchedules
  val courseNames = connection.getDistinctCourseNames

  System.out.println("Before distinct: " + connection.getSchedules.length)
  System.out.println("After distinct: " + schedules.length + " for " + courseNames.length + " courses")

  // courses.json -> courseEntries
  var i = 0
  var courseEntries: Map[String, Int] = Map()
  for (courseName <- courseNames) {
    courseEntries += (courseName -> i)
    i += 1
  }

  // Create /api dir
  val apiDir = new File("api")
  apiDir.mkdir()

  val apiCoursesDir = new File(apiDir.getPath + "/courses")
  apiCoursesDir.mkdir()

  // Generate api/courses.json
  var writer = new PrintWriter(apiDir.getPath + "/courses.json", "UTF-8")
  var courses = Vector.empty[Map[String, Any]]
  for (courseEntry <- courseEntries) {
    courses = courses :+ Map("id" -> courseEntry._2, "name" -> courseEntry._1)
  }

  writer.println(write(courses))
  writer.close()

  // schedulesMap / courses
  i = 0
  var schedulesMap = scala.collection.mutable.HashMap.empty[Int, Vector[IndexedSchedule]]
  for (schedule <- schedules) {
    val courseId = courseEntries(schedule.courseName)
    if (!schedulesMap.contains(courseId)) {
      schedulesMap += (courseId -> Vector.empty[IndexedSchedule])
    }

    schedulesMap(courseId) = schedulesMap(courseId) :+ new IndexedSchedule(i, schedule.courseName, schedule.startDateTime, schedule.endDateTime, schedule.professor, schedule.room, schedule.group)
    i += 1
  }

  // Generate api/courses/.../schedules.json
  for (courseSchedules <- schedulesMap) {
    val dir = new File(apiCoursesDir.getPath + "/" + courseSchedules._1.toString)
    dir.mkdir()

    writer = new PrintWriter(dir.getPath + "/schedules.json", "UTF-8")
    writer.println(write(courseSchedules._2.toArray))
    writer.close()
  }

  // Generate api/courses/.../groups.json
  for (courseSchedules <- schedulesMap) {
    val dir = new File(apiCoursesDir.getPath + "/" + courseSchedules._1.toString)

    var groups = Vector.empty[String]

    for (schedule <- courseSchedules._2) {
      if (!groups.contains(schedule.group)) {
        groups = groups :+ schedule.group
      }
    }

    writer = new PrintWriter(dir.getPath + "/groups.json", "UTF-8")
    writer.println(write(groups.toArray))
    writer.close()
  }

  connection.close
}
