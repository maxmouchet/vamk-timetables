class Course < ActiveRecord::Base
  has_many :schedules, dependent: :destroy
  has_many :timetables, through: :schedules

  validates :name, presence: true
end
