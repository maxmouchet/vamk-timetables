package com.maxmouchet.vamk.timetables.parser.table.sources

import com.maxmouchet.vamk.timetables.parser.table.HTMLTableParser

class RemoteTableSource(parser: HTMLTableParser) extends TableSource {
  def getTable: Array[Array[String]] = parser.parse
}
