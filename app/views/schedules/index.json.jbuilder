json.schedules @schedules do |schedule|
  json.id        schedule.id
  json.course_id schedule.course_id
  json.group     schedule.group
  json.room      schedule.room
  json.professor schedule.professor
  json.start_datetime schedule.start_datetime
  json.end_datetime   schedule.end_datetime
end
