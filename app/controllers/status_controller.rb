class StatusController < ApplicationController
  def index
    TimetableParserWorker.perform_async(params[:url])
  end
end
