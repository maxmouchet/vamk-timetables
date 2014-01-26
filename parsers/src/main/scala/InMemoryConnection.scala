import java.sql.{Connection, DriverManager, Statement}
import models.Schedule
import org.joda.time.DateTime
import scala.collection.mutable

class InMemoryConnection {
  var connection: Connection = null

  def connect {
    try {
      Class.forName("org.sqlite.JDBC")
      connection = DriverManager.getConnection("jdbc:sqlite::memory:")
    } catch {
      case e: Exception => System.err.println("Connection error: " + e.toString)
    }
  }

  def close {
    try {
      connection.close()
    } catch {
      case e: Exception => System.err.println(e.toString)
    }
  }

  def initSchema {
    val query =
      """
        |CREATE TABLE IF NOT EXISTS
        |Schedules (
        | "courseName" TEXT,
        | "startDateTime" TEXT,
        | "endDateTime" TEXT,
        | "professor" TEXT,
        | "room" TEXT,
        | "grp" TEXT
        |)
      """.stripMargin

    try {
      val statement = connection.createStatement()
      statement.executeUpdate(query)
      statement.close
    } catch {
      case e: Exception => System.err.println(e.printStackTrace)
    }
  }

  def addSchedule(schedule: Schedule) {
    val courseName = schedule.courseName
    val startDateTime = schedule.startDateTime.toString()
    val endDateTime = schedule.endDateTime.toString()
    val professor = schedule.professor
    val room = schedule.room
    val group = schedule.group

    val query =
      s"""
        |INSERT INTO schedules VALUES (
        | '$courseName',
        | '$startDateTime',
        | '$endDateTime',
        | '$professor',
        | '$room',
        | '$group'
        |)
      """.stripMargin

    try {
      val statement = connection.createStatement()
      statement.executeUpdate(query)
      statement.close
    } catch {
      case e: Exception => System.err.println(e.printStackTrace)
    }
  }

  def scheduleExist(schedule: Schedule): Boolean = {
    val courseName = schedule.courseName
    val startDateTime = schedule.startDateTime.toString()
    val endDateTime = schedule.endDateTime.toString()
    val professor = schedule.professor
    val room = schedule.room
    val group = schedule.group

    val query =
      s"""
        |SELECT * FROM schedules WHERE
        | schedules.courseName = '$courseName'
        | AND schedules.startDateTime = '$startDateTime'
        | AND schedules.endDateTime = '$endDateTime'
        | AND schedules.professor = '$professor'
        | AND schedules.room = '$room'
        | AND schedules.grp = '$group'
      """.stripMargin

    val statement = connection.createStatement()
    val resultSet = statement.executeQuery(query)

    val exist = resultSet.next
    statement.close

    exist
  }

  def getSchedules: Array[Schedule] = {
    var schedules = Vector.empty[Schedule]

    val query = "SELECT * FROM schedules"

    val statement = connection.createStatement()
    val resultSet = statement.executeQuery(query)


    while(resultSet.next) {
      val courseName = resultSet.getString("courseName")
      val startDateTime = new DateTime(resultSet.getString("startDateTime"))
      val endDateTime = new DateTime(resultSet.getString("endDateTime"))
      val professor = resultSet.getString("professor")
      val room = resultSet.getString("room")
      val group = resultSet.getString("grp")

      schedules = schedules :+ new Schedule(courseName, startDateTime, endDateTime, professor, room, group)
    }

    statement.close
    schedules.toArray
  }

  def getDistinctSchedules: Array[Schedule] = {
    var schedules = Vector.empty[Schedule]

    val query = "SELECT DISTINCT * FROM schedules"

    val statement = connection.createStatement()
    val resultSet = statement.executeQuery(query)


    while(resultSet.next) {
      val courseName = resultSet.getString("courseName")
      val startDateTime = new DateTime(resultSet.getString("startDateTime"))
      val endDateTime = new DateTime(resultSet.getString("endDateTime"))
      val professor = resultSet.getString("professor")
      val room = resultSet.getString("room")
      val group = resultSet.getString("grp")

      schedules = schedules :+ new Schedule(courseName, startDateTime, endDateTime, professor, room, group)
    }

    statement.close
    schedules.toArray
  }

  def getDistinctCourseNames: Array[String] = {
    var courseNames = Vector.empty[String]

    val query = "SELECT DISTINCT courseName FROM schedules"

    val statement = connection.createStatement()
    val resultSet = statement.executeQuery(query)


    while(resultSet.next) {
      courseNames = courseNames :+ resultSet.getString("courseName")
    }

    statement.close
    courseNames.toArray
  }

}
