class CreateSchedules < ActiveRecord::Migration
  def change
    create_table :schedules do |t|
      t.integer :course_id
      t.string :group
      t.string :room
      t.string :professor
      t.datetime :start_datetime
      t.datetime :end_datetime

      t.timestamps
    end
  end
end
