require 'sinatra/base'
require 'sinatra/json'

module LegacyAPI
  class Core < Sinatra::Base

    before do
      # Enable CORS
      headers['Access-Control-Allow-Origin'] = '*'
    end

    get '/courses' do
      json Course.select('id', 'name')
    end

    get '/courses/:id' do
      schedules = []
      course = Course.find(params[:id])
      results = course.schedules.where(group: params[:group]).distinct if params[:group]
      results ||= course.schedules.distinct

      results.each do |schedule|
        schedules << {
            id: schedule.id,
            text: "#{ course.name.strip }\n#{ schedule.room.strip }",
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
