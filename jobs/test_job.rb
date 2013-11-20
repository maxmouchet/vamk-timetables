class TestJob < TrinidadScheduler.Simple :start => Time.now, :end => Time.now + 240, :repeat 3, :interval => 5000
  def run
    _logger.info "I am inside this block" #=> prints "I am inside this block" every 5 seconds
  end
end
