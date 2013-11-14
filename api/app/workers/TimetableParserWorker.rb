require 'java'
require './vamk-timetables-workers-assembly-1.0.jar'

java_import 'com.maxmouchet.vamk.timetables.parser.table.HTMLTableParser'
java_import 'com.maxmouchet.vamk.timetables.parser.table.sources.RemoteTableSource'
java_import 'com.maxmouchet.vamk.timetables.parser.table.settings.Settings'
java_import 'com.maxmouchet.vamk.timetables.parser.timetable.cell.vamk.ITVAMKCellParser'
java_import 'com.maxmouchet.vamk.timetables.parser.timetable.vamk.VAMKTimetableParser'

class TimetableParserWorker
  #include Sidekiq::Worker

  def perform(url)
    settings = Settings.new('table[cellspacing=1]', 'tr', 'td')
    table_parser = HTMLTableParser.new(url, settings)
    table_source = RemoteTableSource.new(table_parser)

    cell_parser = ITVAMKCellParser.new
    timetable_parser = VAMKTimetableParser.new(table_source, cell_parser)

    schedules = timetable_parser.parse.schedules

    schedules.each do |schedule|
      puts schedule.group
    end

  end
end

w = TimetableParserWorker.new
w.perform('http://www.bet.puv.fi/schedule/P1_13_14/x3008i-it-4n13887.htm')
