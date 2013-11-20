source 'https://rubygems.org'

#ruby=jruby-1.7.8
ruby '1.9.3', :engine => 'jruby', :engine_version => '1.7.8'

# Bundle edge Rails instead: gem 'rails', github: 'rails/rails'
gem 'rails', '4.0.0'

gem 'rails_12factor'

# Use sqlite3 as the database for Active Record
group :test do
  gem 'activerecord-jdbcsqlite3-adapter'
end

gem 'activerecord-jdbcpostgresql-adapter'

# Use SCSS for stylesheets
gem 'sass-rails', '~> 4.0.0'

gem 'slim-rails'
gem 'twitter-bootstrap-rails'

# Use Uglifier as compressor for JavaScript assets
gem 'uglifier', '>= 1.3.0'

# Use CoffeeScript for .js.coffee assets and views
gem 'coffee-rails', '~> 4.0.0'

# See https://github.com/sstephenson/execjs#readme for more supported runtimes
gem 'therubyrhino'

# Use jquery as the JavaScript library
gem 'jquery-rails'

# Turbolinks makes following links in your web application faster. Read more: https://github.com/rails/turbolinks
gem 'turbolinks'

# Build JSON APIs with ease. Read more: https://github.com/rails/jbuilder
gem 'jbuilder', '~> 1.2'

gem 'sidekiq', '~> 2.16'
gem 'sinatra', '>= 1.3.0', :require => nil
gem 'sinatra-contrib', :require => nil

group :doc do
  # bundle exec rake doc:rails generates the API under doc/api.
  gem 'sdoc', require: false
end

# Use ActiveModel has_secure_password
# gem 'bcrypt-ruby', '~> 3.0.0'

group :server do
  gem 'foreman'

  platform :jruby do
    gem 'trinidad', :require => false
  end
end

gem 'newrelic_rpm'

# Use Capistrano for deployment
# gem 'capistrano', group: :development

# Use debugger
# gem 'debugger', group: [:development, :test]