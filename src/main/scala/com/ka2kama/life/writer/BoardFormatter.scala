package com.ka2kama.life.writer

import com.ka2kama.life.model.Board

trait BoardFormatter[T] {
  def format(board: Board): String
}

object BoardFormatter {
  implicit val consoleBoardFormatter: BoardFormatter[ConsoleWriter.type] = {
    case Board(table, generation, createdAt) =>
      val sb = new StringBuilder

      sb.append(s"${generation}世代目($createdAt)")
      sb.toString()
  }
}
