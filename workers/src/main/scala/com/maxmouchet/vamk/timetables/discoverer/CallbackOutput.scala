package com.maxmouchet.vamk.timetables.discoverer

class CallbackOutput(callback: (String) => Any) extends Output {

  def write(string: String): Unit = callback(string)

}
