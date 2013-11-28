class SchedulesController < ApplicationController
  respond_to :json

  def index
    expires_in 5.minutes
    @schedules = Course.find(params[:course_id]).schedules
  end

  def show
    expires_in 5.minutes
    @schedule = Course.find(params[:course_id]).schedules.find(params[:id])
  end

end
