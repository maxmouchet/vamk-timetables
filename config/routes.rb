require 'sidekiq/web'

Api::Application.routes.draw do

  get "status/index"
  get "home/index"

  # get '/legacy_api' => LegacyAPI::Core
  # get '/legacy_api/log' => LegacyAPI::Core
  # get '/legacy_api/courses' => LegacyAPI::Core
  # get '/legacy_api/courses/:id' => LegacyAPI::Core
  # get '/legacy_api/groups' => LegacyAPI::Core
  # get '/legacy_api/status' => LegacyAPI::Core

  mount LegacyAPI::Core => '/legacy_api'
  mount Sidekiq::Web => '/sidekiq'

  resources :courses, only: [ :index, :show ], :defaults => { :format => 'json' } do
    resources :schedules, only: [ :index, :show ], :defaults => { :format => 'json' }
  end

end
