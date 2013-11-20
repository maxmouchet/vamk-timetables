class SchedulesController < ApplicationController
  respond_to :json

  def index
    @schedules = Course.find(params[:course_id]).schedules
  end

  def show
    @schedule = Course.find(params[:course_id]).schedules.find(params[:id])
  end

end
