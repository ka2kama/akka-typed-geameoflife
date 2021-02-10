package com.ka2kama.life.model

sealed trait CellState
object CellState {
  case object Dead  extends CellState
  case object Alive extends CellState
}
