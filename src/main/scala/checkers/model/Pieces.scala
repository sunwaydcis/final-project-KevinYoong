package checkers.model

object PieceType extends Enumeration {
  type PieceType = Value
  val Standard, King = Value
}

object PieceColor extends Enumeration {
  type PieceColor = Value
  val White, Black = Value
}

import checkers.model.PieceType._
import checkers.model.PieceColor._

class Piece(val pieceType: PieceType, val color: PieceColor) {
  // Method to promote a piece to a king
  def promoteToKing(): Piece = {
    new Piece(PieceType.King, this.color)
  }

  // Method to check if the piece is a king
  def isKing: Boolean = {
    this.pieceType == PieceType.King
  }
}