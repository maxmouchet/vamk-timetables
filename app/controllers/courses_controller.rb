class CoursesController < ApplicationController
  respond_to :json

  def index
    expires_in 5.minutes
    @courses = Course.all
  end

  def show
    expires_in 5.minutes
    @course = Course.find(params[:id])
  end

end
