class Schedule < ActiveRecord::Base
  belongs_to :course
  belongs_to :timetable

  validates :course_id, numericality: { only_integer: true }
  validates :timetable_id, numericality: { only_integer: true }
  validates :group, presence: true
  validates :room, presence: true
  validates :professor, presence: true
  validates :start_datetime, presence: true
  validates :end_datetime, presence: true
end
