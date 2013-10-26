require 'sinatra/base'
require 'sinatra/json'
require 'sinatra/reloader'
require 'sinatra/cross_origin'

require 'json'
require 'pg'

class API < Sinatra::Base

  configure do
    enable :cross_origin

    uri ||= URI.parse(ENV['DATABASE_URL'])
    uri ||= URI.parse('postgres://maxmouchet@localhost/timetables')

    username = uri.userinfo
    password = nil

    if (uri.userinfo.split(':').length > 0)
      username = uri.userinfo.split(':')[0]
      password = uri.userinfo.split(':')[1]
    end

    @@connect_hash = { host: uri.host, dbname: uri.path[1..-1], user: username }
    @@connect_hash[:password] = password if password
  end

  configure :development do
    register Sinatra::Reloader
  end

  before do
    @db = PG.connect(@@connect_hash)
    headers['Access-Control-Allow-Origin'] = '*'
  end

  get '/courses' do
    courses = Array.new

    @db.exec('SELECT * FROM courses') do |results|
      results.each do |course|
        courses << {
          id: course.values_at('id').first,
          name: course.values_at('name').first.strip
        }
      end
    end

    json courses
  end

  get '/courses/:id' do
    schedules = Array.new

    query = ''
    if params[:group]
      query = "SELECT schedules.id, schedules.room, schedules.professor, schedules.start_time, schedules.end_time, schedules.group, courses.name FROM schedules INNER JOIN courses ON schedules.course_id = courses.id WHERE courses.id = #{ params[:id] } AND schedules.group = '#{ params[:group] }'"
    else
      query = "SELECT schedules.id, schedules.room, schedules.professor, schedules.start_time, schedules.end_time, schedules.group, courses.name FROM schedules INNER JOIN courses ON schedules.course_id = courses.id WHERE courses.id = #{ params[:id] }"
    end

    @db.exec(query) do |results|
      results.each do |schedule|
        p schedule
        schedules << {
          id: schedule.values_at('id').first,
          text: "#{ schedule.values_at('name').first.strip }\n#{ schedule.values_at('room').first.strip }",
          start_date: DateTime.parse(schedule.values_at('start_time').first).strftime('%m/%d/%Y %H:%M'),
          end_date: DateTime.parse(schedule.values_at('end_time').first).strftime('%m/%d/%Y %H:%M')
        }
      end
    end


    json schedules
  end

  get '/groups' do
    groups = Array.new

    query = ''
    if params[:course]
      query = "SELECT schedules.group FROM schedules WHERE schedules.course_id = #{ params[:course] } GROUP BY schedules.group"
    else
      query = 'SELECT schedules.group FROM schedules GROUP BY schedules.group'
    end

    @db.exec(query) do |results|
      results.each do |group|
        groups << group.values_at('group').first.strip
      end
    end

    json groups
  end


end
