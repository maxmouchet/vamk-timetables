require 'rubygems'
require 'bunny'
require 'pg'

conn = Bunny.new
conn.start

ch = conn.create_channel
q  = ch.queue('vamk.timetable.parser.results')
x  = ch.default_exchange

begin
  q.subscribe(:ack => true, :block => true) do |delivery_info, metadata, payload|
    puts "Received #{payload}"



    ch.ack(delivery_info.delivery_tag)
  end
rescue Interrupt => _
  conn.close
end
