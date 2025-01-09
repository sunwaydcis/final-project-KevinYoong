package checkers.util

import checkers.MainApp
import checkers.model.{Board, PieceColor}

object MoveValidator {

  def isValidMove(startRow: Int, startCol: Int, endRow: Int, endCol: Int, board: Board, pieceColor: String, currentTurn: PieceColor.Value): Boolean = {
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
    val isForwardMove = (pieceColor, currentTurn, MainApp.getSelectedColor()) match {
      case ("White", PieceColor.White, "White") => endRow < startRow // White moves upwards
      case ("Black", PieceColor.Black, "White") => endRow > startRow // Black moves downwards
      case ("White", PieceColor.White, "Black") => endRow > startRow // White moves downwards
      case ("Black", PieceColor.Black, "Black") => endRow < startRow // Black moves upwards
      case _ => false
    }



    // Check if the move is a valid diagonal move with the right distance
    if (isForwardMove && rowDiff == colDiff && rowDiff <= 2) {
      if (rowDiff == 1) {
        // Regular move (not a jump)
        board.getPiece(endRow, endCol).isEmpty
      } else if (rowDiff == 2) {
        // Jump move (captures an opponent's piece)
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

  def getValidMoves(startRow: Int, startCol: Int, board: Board, pieceColor: String, currentTurn: PieceColor.Value): List[(Int, Int)] = {
    // Possible movement directions for the piece (diagonals)
    val directions = List((-1, -1), (-1, 1), (1, -1), (1, 1)) // Add more directions if necessary for kings

    directions.flatMap { case (rowOffset, colOffset) =>
      val potentialMoves = List(
        (startRow + rowOffset, startCol + colOffset), // Regular move
        (startRow + 2 * rowOffset, startCol + 2 * colOffset) // Jump move
      )

      // Filter valid moves using isValidMove and ensure moves are within bounds
      potentialMoves.filter { case (endRow, endCol) =>
        isWithinBounds(endRow, endCol) &&
          isValidMove(startRow, startCol, endRow, endCol, board, pieceColor, currentTurn)
      }
    }
  }

  private def isWithinBounds(row: Int, col: Int): Boolean = {
    row >= 0 && row < 8 && col >= 0 && col < 8
  }
}
