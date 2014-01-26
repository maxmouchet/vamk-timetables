require 'httparty'
require 'icalendar'
require 'date'

include Icalendar

@cal = Calendar.new

def convert_date(string)
  regex = /(?<month>\d{2})\/(?<day>\d{2})\/(?<year>\d{4})\s+(?<hour>\d{2}):(?<minute>\d{2})/
  match = regex.match(string)
  DateTime.new(match['year'].to_i, match['month'].to_i, match['day'].to_i, match['hour'].to_i, match['minute'].to_i, 0, '+3')
end

def add_schedule(schedule)
  @cal.event do
    dtstart       convert_date(schedule['start_date'])
    dtend         convert_date(schedule['end_date'])
    summary       schedule['text']
  end
end

urls = []
urls << 'http://146.185.153.218/legacy_api/courses/1151?group=I-IT-2N1'
urls << 'http://146.185.153.218/legacy_api/courses/1180?group=I-IT-2N2'
urls << 'http://146.185.153.218/legacy_api/courses/1140?group=I-IT-2N2'
urls << 'http://146.185.153.218/legacy_api/courses/1213?group=I-IT-2N1'
urls << 'http://146.185.153.218/legacy_api/courses/1095?group=I-IT-4N1'
urls << 'http://146.185.153.218/legacy_api/courses/1110?group=I-IT-3N1'
urls << 'http://146.185.153.218/legacy_api/courses/1101?group=I-IT-3N1'

urls.each do |url|
  schedules = HTTParty.get(url)
  schedules.each { |schedule| add_schedule(schedule) }
end

File.open('VAMK.ics', 'w+') { |f| f.puts(@cal.to_ical) }
