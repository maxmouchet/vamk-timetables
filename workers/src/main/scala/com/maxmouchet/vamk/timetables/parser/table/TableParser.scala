package com.maxmouchet.vamk.timetables.parser.table

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

import scala.collection.JavaConversions._
import java.net.URL
import java.io.InputStream
import com.maxmouchet.vamk.timetables.parser.table.settings.Settings

class TableParser(url: String, settings: Settings) {

  def parse: Array[Array[String]] = {
    val document = Jsoup.parse(new URL(url).openStream(), "ISO-8859-1", url)
    val table = document.select(settings.tableExpression).first

    val (height, width) = getTableDimensions(table)

    val array = Array.ofDim[String](height, width)

    for ((row, currentRow) <- table.select(settings.rowExpression).view.zipWithIndex) {
      var currentColumn = 0

      for (cell <- row.select(settings.columnExpression)) {
        var rowspan = 0

        try {
          rowspan = Integer.parseInt(cell.attr("rowspan"))
        } catch {
          case e: NumberFormatException => {} // Do nothing. Cell don't have a rowspan.
        }

        while (array(currentRow)(currentColumn) != null && currentColumn < width) {
          currentColumn += 1
        }

        val cellText = Jsoup.parse(cell.html.replaceAll("(?i)<br[^>]*>", "br2n")).text().replaceAll("br2n", "\n")

        array(currentRow)(currentColumn) = cellText

        if (rowspan > 0) {
          for (i <- currentRow + 1 until currentRow + rowspan) {
            array(i)(currentColumn) = cellText
          }
        }

        currentColumn += 1
      }
    }

    array
  }

  def getTableDimensions(table: Element): (Int, Int) = {
    val height = table.select(settings.rowExpression).size
    var width = 0

    for (row <- table.select(settings.rowExpression)) {
      val length = row.select(settings.columnExpression).size
      if (length > width) { width = length }
    }

    (height, width)
  }

}