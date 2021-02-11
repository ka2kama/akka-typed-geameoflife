package com.ka2kama.life

import java.time.LocalDateTime
import scala.collection.immutable.ArraySeq

case class Board(
    table: ArraySeq[ArraySeq[CellState]],
    generation: BigInt,
    createdAt: LocalDateTime,
)

sealed trait CellState
object CellState {
  case object Dead  extends CellState
  case object Alive extends CellState
}
