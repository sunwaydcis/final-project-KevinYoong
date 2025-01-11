package checkers.model

object PieceType extends Enumeration {
  type PieceType = Value
  val Standard: Value = Value
}

object PieceColor extends Enumeration {
  type PieceColor = Value
  val White, Black = Value
}

import checkers.model.PieceColor.*

case class Piece(pieceType: PieceType.Value, color: PieceColor.Value, var isKing: Boolean = false)