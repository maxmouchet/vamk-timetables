require 'sinatra/base'
require 'sinatra/json'

module LegacyAPI
  class Core < Sinatra::Base

    set :cache, Dalli::Client.new

    before do
      # Enable CORS
      headers['Access-Control-Allow-Origin'] = '*'
    end

    get '/courses' do
      @courses ||= settings.cache.fetch('courses') do
        courses = Course.select('id', 'name')
        settings.cache.set('courses', @courses, 5 * 60) # cache for 5 min.
        courses
      end
      json @courses
    end

    get '/courses/:id' do
      @course ||= settings.cache.fetch("course:#{params[:id]}") do
        course = Course.find(params[:id])
        settings.cache.set("course:#{params[:id]}", course, 5 * 60) # cache for 5 min.
        course
      end

      @results ||= settings.cache.fetch("course:schedules:#{params[:id]}:#{params[:group]}") do
        course = Course.find(params[:id])
        results = course.schedules.where(group: params[:group]).distinct if params[:group]
        results ||= course.schedules.distinct
        settings.cache.set("course:schedules:#{params[:id]}:#{params[:group]}", results, 5 * 60) # cache for 5 min.
        results
      end

      schedules = []

      @results.each do |schedule|
        schedules << {
            id: schedule.id,
            text: "#{ @course.name.strip }\n#{ schedule.room.strip }",
            start_date: schedule.start_datetime.strftime('%m/%d/%Y %H:%M'),
            end_date: schedule.end_datetime.strftime('%m/%d/%Y %H:%M')
          }
      end

      json schedules
    end

    get '/groups' do
      groups = []
      results = nil

      if params[:course]
        results = Schedule.where(course_id: params[:course]).select('"group"').distinct
      else
        results = Schedule.select('"group"').distinct
      end

      results.each do |result|
        groups << result.group unless result.group == 'Unknown'
      end

      json groups
    end
  end
end
