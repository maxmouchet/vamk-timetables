class Course < ActiveRecord::Base
  has_many :schedules, dependent: :destroy

  validates :name, presence: true
  validates_associated :schedule
end
