require 'java'
require './vamk-timetables-workers-assembly-1.0.jar'

java_import 'com.maxmouchet.vamk.timetables.parser.table'
java_import 'com.maxmouchet.vamk.timetables.parser.table.settings'

class TimetableParserWorker
  #include Sidekiq::Worker

  def perform(url)
    settings = Settings.new('table[cellspacing=1]', 'tr', 'td')
    table_parser = TableParser.new(url, settings)


  end



  p parser.parse
end
