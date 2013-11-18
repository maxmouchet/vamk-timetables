require 'java'
require "#{ Rails.root }/lib/scala/target/scala-2.10/vamk-timetables-workers-assembly-1.0.jar"

java_import 'com.maxmouchet.vamk.timetables.parser.list.week.vamk.ITVAMKWeekListParser'

it_week_list_parser = ITVAMKWeekListParser.new
it_weeks = it_week_list_parser.parse('http://www.bet.puv.fi/schedule/P1_13_14/mfw.htm')

it_weeks.each do |week|
  p week.number
  week.getTimetableList.each do |timetable|
    TimetableParserWorker.perform_async(timetable.url, 'IT')
  end
end

ib_week_list_parser = IBVAMKWeekListParser.new
ib_weeks = ib_week_list_parser.parse('http://www.bet.puv.fi/studies/lukujarj/LV_13_14/syksy.htm')

ib_weeks.each do |week|
  p week.number
  week.getTimetableList.each do |timetable|
    TimetableParserWorker.perform_async(timetable.url, 'IB')
  end
end
