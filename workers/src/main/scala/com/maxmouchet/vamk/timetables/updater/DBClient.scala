package com.maxmouchet.vamk.timetables.updater

import org.postgresql.ds.PGPoolingDataSource
import java.sql.PreparedStatement
import java.util.Locale
import java.sql.Timestamp
import com.maxmouchet.vamk.timetables.parser.Schedule

class DBClient(source: PGPoolingDataSource) {

  val conn = source.getConnection()

  def insertCourse(name: String) = {
    val st = conn.prepareStatement("INSERT INTO courses(name) VALUES(?)")
    st.setString(1, name)
    st.executeUpdate
    st.close
  }

  def insertSchedule(schedule: Schedule) = {
    var courseId = findCourseId(schedule.courseName)
    if (courseId == -1) {
      insertCourse(schedule.courseName)
      courseId = findCourseId(schedule.courseName)
    }

    val st = conn.prepareStatement("INSERT INTO schedules(course_id, room, professor, start_time, end_time, \"group\") VALUES(?, ?, ?, ?, ?, ?)")
    st.setInt(1, courseId)
    st.setString(2, schedule.room)
    st.setString(3, schedule.professor)
    st.setTimestamp(4, new Timestamp(schedule.startDate.getMillis))
    st.setTimestamp(5, new Timestamp(schedule.endDate.getMillis))
    st.setString(6, schedule.group)

    st.executeUpdate
    st.close
  }

  def dropAllCourses = {
    val st = conn.prepareStatement("DELETE FROM courses")
    st.executeUpdate
    st.close
  }

  def dropAllSchedules = {
    val st = conn.prepareStatement("DELETE FROM schedules")
    st.executeUpdate
    st.close
  }

  def findCourseId(name: String) = {
    var result = -1

    val st = conn.prepareStatement("SELECT id FROM courses WHERE name = ?")
    st.setString(1, name)

    try {
      val results = st.executeQuery
      results.next
      result = results.getInt(1)
    } catch {
      case _: Throwable => //
    } finally {
      st.close
    }

    result
  }

  def findSchedule(name: String) = {
    var result = -1

    val st = conn.prepareStatement("SELECT id FROM schedules WHERE name = ?")
    st.setString(1, name)

    try {
      val results = st.executeQuery
      results.next
      result = results.getInt(1)
    } catch {
      case _: Throwable => //
    } finally {
      st.close
    }

    result
  }

}