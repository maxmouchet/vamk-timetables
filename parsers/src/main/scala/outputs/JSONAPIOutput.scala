package outputs

import outputs.base.Output
import models.{IndexedCourse, IndexedSchedule, Schedule}
import java.io.{PrintWriter, File}
import scala.collection.mutable
import org.json4s.native.Serialization.write
import java.util.Date

/** Generate a JSON API from the given schedules. */
class JSONAPIOutput extends Output {
  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all

  def execute(args: Array[String], schedules: Array[Schedule]) = {
    val courses = mutable.Set.empty[String]

    var indexedCourses = Vector.empty[IndexedCourse]
    var indexedSchedules = Vector.empty[IndexedSchedule]

    var scheduleMap = mutable.HashMap.empty[Int, Vector[IndexedSchedule]]

    // - Extract course names
    // - Index schedules
    for (i <- 0 to schedules.length - 1) {
      courses += schedules(i).courseName
      indexedSchedules +:= new IndexedSchedule(i, schedules(i).courseName, schedules(i).startDateTime, schedules(i).endDateTime, schedules(i).professor, schedules(i).room, schedules(i).group)
    }

    // Index courses
    var i = 0
    for (course <- courses) {
      indexedCourses +:= new IndexedCourse(i, course)
      i += 1
    }

    // Map schedules to courses
    for (indexedCourse <- indexedCourses) {
      scheduleMap(indexedCourse.id) = Vector.empty[IndexedSchedule]
      for (indexedSchedule <- indexedSchedules) {
        if (indexedSchedule.courseName.equals(indexedCourse.name)) {
          scheduleMap(indexedCourse.id) +:= indexedSchedule
        }
      }
    }

    val apiDir = new File(args(0))
    apiDir.mkdir()

    val coursesDir = new File(apiDir.getPath + "/courses")
    coursesDir.mkdir()

    // Generate the courses list file (/courses.json)
    var writer = new PrintWriter(apiDir.getPath + "/courses.json", "UTF-8")
    var coursesWithId = Vector.empty[Map[String, Any]]
    i = 0
    for (course <- courses) {
      coursesWithId +:= Map("id" -> i, "name" -> course)
      i += 1
    }
    writer.println(write(coursesWithId))
    writer.close()

    // - Generate the schedules list files (/courses/{id}/schedules.json)
    // - Generate the groups list files (/courses/{id}/groups.json)
    for (courseSchedules <- scheduleMap) {
      val dir = new File(coursesDir.getPath + "/" + courseSchedules._1.toString)
      dir.mkdir()

      val groups = mutable.Set.empty[String]
      for (schedule <- courseSchedules._2) {
        groups += schedule.group
      }

      writer = new PrintWriter(dir.getPath + "/schedules.json", "UTF-8")
      writer.println(write(courseSchedules._2.toArray))
      writer.close()

      writer = new PrintWriter(dir.getPath + "/groups.json", "UTF-8")
      writer.println(write(groups.toArray))
      writer.close()
    }

    // Generate the status file (/status.json)
    val status = Map("update_time" -> new Date().toString, "courses_count" -> indexedCourses.length, "schedules_count" -> indexedSchedules.length)
    writer = new PrintWriter(apiDir.getPath + "/status.json", "UTF-8")
    writer.println(write(status))
    writer.close()
  }

}
