package checkers.model

import checkers.model.PieceColor.PieceColor

class Player(val name: String, val color: PieceColor) {
  var pieces: List[Piece] = List()
  var isTurn: Boolean = false

  // Check if its player's turn 
  def checkTurn: Boolean = isTurn

  // Check remaining pieces of each player
  def remainingPieces: Int = {
    val count = pieces.size
    println(s"$name remaining pieces: $count")
    count
  }
}