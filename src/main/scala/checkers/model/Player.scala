package checkers.model

import checkers.model.PieceColor.PieceColor

class Player(val name: String, val color: PieceColor) {
  var pieces: List[Piece] = List()
  var isTurn: Boolean = false

  def checkTurn: Boolean = isTurn

  def remainingPieces: Int = pieces.size
}