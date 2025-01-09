package checkers.util

import checkers.model.{Board, PieceColor}

object MoveValidator {
  def isValidMove(startRow: Int, startCol: Int, endRow: Int, endCol: Int, board: Board, pieceColor: String, currentTurn: PieceColor.Value): Boolean = {
    if (PieceColor.withName(pieceColor) != currentTurn) {
      return false
    }

    val rowDiff = Math.abs(endRow - startRow)
    val colDiff = Math.abs(endCol - startCol)

    val isForwardMove = pieceColor match {
      case "White" => endRow > startRow
      case "Black" => endRow < startRow
      case _ => false
    }

    if (isForwardMove && rowDiff == colDiff && rowDiff <= 2) {
      if (rowDiff == 1) {
        board.getPiece(endRow, endCol).isEmpty
      } else if (rowDiff == 2) {
        val middleRow = (startRow + endRow) / 2
        val middleCol = (startCol + endCol) / 2
        board.getPiece(middleRow, middleCol).exists(_.color != PieceColor.withName(pieceColor)) &&
          board.getPiece(endRow, endCol).isEmpty
      } else {
        false
      }
    } else {
      false
    }
  }
}