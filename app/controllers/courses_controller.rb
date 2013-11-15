class CoursesController < ApplicationController
  respond_to :json

  def index
    @courses = Course.all
  end

  def show
    @course = Course.find(params[:id])
  end

end
