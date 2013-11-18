require 'java'
require "#{ Rails.root }/lib/scala/target/scala-2.10/vamk-timetables-workers-assembly-1.0.jar"

java_import 'com.maxmouchet.vamk.timetables.parser.table.HTMLTableParser'
java_import 'com.maxmouchet.vamk.timetables.parser.table.sources.RemoteTableSource'
java_import 'com.maxmouchet.vamk.timetables.parser.table.settings.Settings'
java_import 'com.maxmouchet.vamk.timetables.parser.timetable.cell.vamk.ITVAMKCellParser'
java_import 'com.maxmouchet.vamk.timetables.parser.timetable.vamk.VAMKTimetableParser'

class TimetableParserWorker
  include Sidekiq::Worker

  def perform(url, type)
    logger.info { "Initializing TimetableParserWorker with url = #{ url }" }

    settings = Settings.new('table[cellspacing=1]', 'tr', 'td')
    table_parser = HTMLTableParser.new(url, settings)
    table_source = RemoteTableSource.new(table_parser)

    cell_parser = ITVAMKCellParser.new if type == 'IT'
    cell_parser = IBVAMKCellParser.new if type == 'IB'

    timetable_parser = VAMKTimetableParser.new(table_source, cell_parser)

    logger.info { "Parsing timetable" }
    timetable = timetable_parser.parse
    schedules = timetable.schedules

    t = Timetable.where(group: timetable.group,
                        start_date: DateTime.new(timetable.startDate.getYear, timetable.startDate.getMonthOfYear, timetable.startDate.getDayOfMonth),
                        end_date:   DateTime.new(timetable.endDate.getYear, timetable.endDate.getMonthOfYear, timetable.endDate.getDayOfMonth)
                       ).first_or_create

    Schedule.destroy_all(timetable_id: t.id)

    schedules.each do |schedule|
      c = Course.where(name: schedule.course_name).first_or_create
      s = Schedule.new

      s.timetable_id = t.id
      s.course_id = c.id
      s.group = schedule.group
      s.room = schedule.room
      s.professor = schedule.professor
      s.start_datetime = DateTime.new(schedule.startDate.getYear, schedule.startDate.getMonthOfYear, schedule.startDate.getDayOfMonth, schedule.startDate.getHourOfDay, schedule.startDate.getMinuteOfHour)
      s.end_datetime = DateTime.new(schedule.endDate.getYear, schedule.endDate.getMonthOfYear, schedule.endDate.getDayOfMonth, schedule.endDate.getHourOfDay, schedule.endDate.getMinuteOfHour)

      s.save
    end

  end
end

# w = TimetableParserWorker.new
# w.perform('http://www.bet.puv.fi/schedule/P1_13_14/x3008i-it-4n13887.htm')
