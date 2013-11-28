require 'json'
require 'httparty'
require 'thread/pool'
require 'awesome_print'

target = 'http://146.185.153.218'

i = 0
hit_count = 0
error_count = 0

times = []

pool = Thread.pool(200)

5000.times do
  pool.process do
    puts "Begin hit #{ i }"
    i += 1
    begin
      t1 = Time.now
      response = HTTParty.get(target + '/legacy_api/courses')
      courses = JSON.load(response.body)
      HTTParty.get(target + '/legacy_api/courses' + "/#{ courses.sample["id"] }")
      t2 = Time.now
      times << t2 - t1
    if response.code != 200
      error_count += 1
    end
    rescue Exception => e
      puts e
      error_count +=1
    end

    hit_count += 1
  end
end

pool.shutdown

puts "Hit: #{ hit_count }"
puts "Error: #{ error_count } (#{ (error_count / hit_count) * 100 }%)"

File.open("#{ Time.now.to_s }_results.csv", 'w') do |io|
  i = 0
  times.each do |k|
    io.puts "#{i};#{k}"
    i += 1
  end
end


