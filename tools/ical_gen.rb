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
urls << 'http://api.olamas.me/courses/1560?group=I-IT-4N1'
urls << 'http://api.olamas.me/courses/1908?group=I-IT-3N1'
urls << 'http://api.olamas.me/courses/1563?group=I-IT-4N2'
urls << 'http://api.olamas.me/courses/1863?group=I-IT-3N1'
urls << 'http://api.olamas.me/courses/1552?group=I-IT-3N1'
urls << 'http://api.olamas.me/courses/1559?group=I-IT-4N1'
urls << 'http://api.olamas.me/courses/1907?group=I-IT-3N1'

urls.each do |url|
  schedules = HTTParty.get(url)
  schedules.each { |schedule| add_schedule(schedule) }
end

File.open('VAMK.ics', 'w+') { |f| f.puts(@cal.to_ical) }
