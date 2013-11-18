class StatusController < ApplicationController
  def index
    #TimetableParserWorker.perform_async(params[:url])
    @timetables = Timetable.all
  end
end
