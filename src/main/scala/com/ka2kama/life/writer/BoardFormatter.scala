package com.ka2kama.life.writer

import com.ka2kama.life.{Board, CellState}

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import scala.collection.mutable

trait BoardFormatter[T] {
  def format(board: Board): String
}

object BoardFormatter {

  private[this] val formatter = DateTimeFormatter
    .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx")
    .withZone(ZoneId.systemDefault())

  implicit val consoleBoardFormatter: BoardFormatter[ConsoleWriter.type] = {
    case Board(table, generation, createdAt) =>
      val sb = new mutable.StringBuilder

      sb ++= s"${generation}世代目(${formatter.format(createdAt)})\n"
      for (row <- table) {
        for (cell <- row) {
          val c = if (cell == CellState.Alive) '□' else '■'
          sb += c
        }
        sb += '\n'
      }

      sb.toString()
  }
}
