class Timetable < ActiveRecord::Base
  has_many :schedules, dependent: :destroy
  has_many :courses, through: :schedules
end
