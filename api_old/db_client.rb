require 'pg'

class DBClient

  def initialize(connection_hash)
    @conn = PG.connect(connection_hash)
  end

  def get_groups(course = nil)
    params = []

    params << course if course

    query = 'SELECT schedules.group FROM schedules GROUP BY schedules.group'
    query = 'SELECT schedules.group FROM schedules WHERE schedules.course_id = $1 GROUP BY schedules.group' if course

    @conn.prepare('stmt', query)
    @conn.exec_prepared('stmt', params) do |results|
      groups = []

      results.each do |group|
        groups << group.values_at('group').first.strip
      end

      groups
    end
  end

  def get_courses
    courses = []

    query = 'SELECT * FROM courses'

    @conn.exec(query) do |results|
      results.each do |course|
        courses << {
          id: course.values_at('id').first,
          name: course.values_at('name').first.strip
        }
      end
    end

    courses
  end

  def get_course(id, group = nil)
    params = []

    params << id
    params << group if group

    query = 'SELECT DISTINCT schedules.id, schedules.room, schedules.professor,
                           schedules.start_time, schedules.end_time, schedules.group, courses.name
                           FROM schedules
                           INNER JOIN courses ON schedules.course_id = courses.id
                           WHERE courses.id = $1'

    query += ' AND schedules.group = $2' if group

    @conn.prepare('stmt', query)
    @conn.exec_prepared('stmt', params) do |results|
      schedules = []

      results.each do |schedule|
        schedules << {
            id: schedule.values_at('id').first,
            text: "#{ schedule.values_at('name').first.strip }\n#{ schedule.values_at('room').first.strip }",
            start_date: DateTime.parse(schedule.values_at('start_time').first).strftime('%m/%d/%Y %H:%M'),
            end_date: DateTime.parse(schedule.values_at('end_time').first).strftime('%m/%d/%Y %H:%M')
          }
      end

      schedules
    end
  end

end
