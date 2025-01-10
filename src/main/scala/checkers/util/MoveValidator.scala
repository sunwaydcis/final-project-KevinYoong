package checkers.util

import checkers.MainApp
import checkers.model.{Board, PieceColor}

object MoveValidator {

  def isValidMove(startRow: Int, startCol: Int, endRow: Int, endCol: Int, board: Board, pieceColor: String, currentTurn: PieceColor.Value, isKing: Boolean): Boolean = {
    // Ensure the move stays within the board boundaries
    if (!isWithinBounds(endRow, endCol)) {
      return false
    }

    // Check if the piece color matches the current turn
    if (PieceColor.withName(pieceColor) != currentTurn) {
      return false
    }

    val rowDiff = Math.abs(endRow - startRow)
    val colDiff = Math.abs(endCol - startCol)

    // Determine if the move is forward based on the current player's perspective
    val isForwardMove = isKing || (
      (pieceColor == "White" && currentTurn == PieceColor.White && MainApp.getSelectedColor() == "White" && endRow < startRow) ||
      (pieceColor == "Black" && currentTurn == PieceColor.Black && MainApp.getSelectedColor() == "White" && endRow > startRow) ||
      (pieceColor == "White" && currentTurn == PieceColor.White && MainApp.getSelectedColor() == "Black" && endRow > startRow) ||
      (pieceColor == "Black" && currentTurn == PieceColor.Black && MainApp.getSelectedColor() == "Black" && endRow < startRow)
    )

    // Check if the move is a valid diagonal move with the right distance
    if (isForwardMove && rowDiff == colDiff) {
      if (rowDiff == 1) {
        // Regular move (not a jump)
        board.getPiece(endRow, endCol).isEmpty
      } else if (rowDiff == 2) {
        // Jump move (captures an opponent's piece)
        val middleRow = (startRow + endRow) / 2
        val middleCol = (startCol + endCol) / 2
        board.getPiece(middleRow, middleCol).exists(_.color != PieceColor.withName(pieceColor)) &&
          board.getPiece(endRow, endCol).isEmpty
      } else if (isKing) {
        // King move (multiple spaces)
        val stepRow = (endRow - startRow) / rowDiff
        val stepCol = (endCol - startCol) / colDiff
        (1 until rowDiff).forall { i =>
          board.getPiece(startRow + i * stepRow, startCol + i * stepCol).isEmpty
        } && board.getPiece(endRow, endCol).isEmpty
      } else {
        false
      }
    } else {
      false
    }
  }

  def getValidMoves(startRow: Int, startCol: Int, board: Board, pieceColor: String, currentTurn: PieceColor.Value, isKing: Boolean): List[(Int, Int)] = {
    // Possible movement directions for the piece (diagonals)
    val directions = List((-1, -1), (-1, 1), (1, -1), (1, 1))

    directions.flatMap { case (rowOffset, colOffset) =>
      if (isKing) {
        (1 until 8).flatMap { distance =>
          val endRow = startRow + distance * rowOffset
          val endCol = startCol + distance * colOffset
          if (isWithinBounds(endRow, endCol) && isValidMove(startRow, startCol, endRow, endCol, board, pieceColor, currentTurn, isKing)) {
            Some((endRow, endCol))
          } else {
            None
          }
        }
      } else {
        val potentialMoves = List(
          (startRow + rowOffset, startCol + colOffset), // Regular move
          (startRow + 2 * rowOffset, startCol + 2 * colOffset) // Jump move
        )

        // Filter valid moves using isValidMove and ensure moves are within bounds
        potentialMoves.filter { case (endRow, endCol) =>
          isWithinBounds(endRow, endCol) &&
            isValidMove(startRow, startCol, endRow, endCol, board, pieceColor, currentTurn, isKing)
        }
      }
    }
  }

  private def isWithinBounds(row: Int, col: Int): Boolean = {
    row >= 0 && row < 8 && col >= 0 && col < 8
  }
}