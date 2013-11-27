require 'sidekiq/web'

Api::Application.routes.draw do

  get "status/index"
  get "home/index"

  mount LegacyAPI::Core => '/legacy_api'
  mount Sidekiq::Web => '/sidekiq'

  resources :courses, only: [ :index, :show ], :defaults => { :format => 'json' } do
    resources :schedules, only: [ :index, :show ], :defaults => { :format => 'json' }
  end

end
