require 'sinatra/base'
require 'sinatra/json'
require 'sinatra/reloader'

require 'newrelic_rpm'
require 'json'

require './db_client'

class API < Sinatra::Base

  configure do
    uri = ENV['DATABASE_URL'] ? URI.parse(ENV['DATABASE_URL']) : URI.parse('postgres://maxmouchet:1234@localhost:5432/timetables')

    username = uri.userinfo
    password = nil

    if (uri.userinfo.split(':').length > 0)
      username = uri.userinfo.split(':')[0]
      password = uri.userinfo.split(':')[1]
    end

    @@connect_hash = { host: uri.host, dbname: uri.path[1..-1], user: username }
    @@connect_hash[:password] = password if password
  end

  configure :development do
    register Sinatra::Reloader
  end

  before do
    @db ||= DBClient.new(@@connect_hash)

    # Enable CORS
    headers['Access-Control-Allow-Origin'] = '*'
  end

  get '/' do
    redirect '/index.html'
  end

  get '/courses' do
    json @db.get_courses
  end

  get '/courses/:id' do
    json @db.get_course(params[:id], params[:group])
  end

  get '/groups' do
    json @db.get_groups(params[:course])
  end


end
