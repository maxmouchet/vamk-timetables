class CreateTimetables < ActiveRecord::Migration
  def change
    create_table :timetables do |t|
      t.string :group
      t.date :start_date
      t.date :end_date

      t.timestamps
    end
    change_table :schedules do |t|
      t.integer :timetable_id
    end
  end
end
