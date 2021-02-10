package com.ka2kama.life.model

import java.time.LocalDateTime
import scala.collection.immutable.ArraySeq

object Board {
  type Table      = ArraySeq[ArraySeq[CellState]]
  type Generation = BigInt
}

case class Board(
    table: Board.Table,
    generation: Board.Generation,
    createdAt: LocalDateTime,
)
