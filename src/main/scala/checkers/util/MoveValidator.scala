package checkers.util

import checkers.MainApp
import checkers.model.{Board, PieceColor}

object MoveValidator {

  def isValidMove(startRow: Int, startCol: Int, endRow: Int, endCol: Int, board: Board, pieceColor: String, currentTurn: PieceColor.Value): Boolean = {
    if (!isWithinBounds(endRow, endCol)) {
      println(s"Move out of bounds: ($endRow, $endCol)")
      return false
    }

    if (PieceColor.withName(pieceColor) != currentTurn) {
      println(s"Not $pieceColor's turn")
      return false
    }

    val rowDiff = Math.abs(endRow - startRow)
    val colDiff = Math.abs(endCol - startCol)

    val isForwardMove = (
      (pieceColor == "White" && currentTurn == PieceColor.White && MainApp.getSelectedColor() == "White" && endRow < startRow) ||
      (pieceColor == "Black" && currentTurn == PieceColor.Black && MainApp.getSelectedColor() == "White" && endRow > startRow) ||
      (pieceColor == "White" && currentTurn == PieceColor.White && MainApp.getSelectedColor() == "Black" && endRow > startRow) ||
      (pieceColor == "Black" && currentTurn == PieceColor.Black && MainApp.getSelectedColor() == "Black" && endRow < startRow)
    )

    if (isForwardMove && rowDiff == colDiff) {
      if (rowDiff == 1) {
        val isEmpty = board.getPiece(endRow, endCol).isEmpty
        isEmpty
      } else if (rowDiff == 2) {
        val middleRow = (startRow + endRow) / 2
        val middleCol = (startCol + endCol) / 2
        val canCapture = board.getPiece(middleRow, middleCol).exists(_.color != PieceColor.withName(pieceColor))
        val isEmpty = board.getPiece(endRow, endCol).isEmpty
        canCapture && isEmpty
      } else {
        println(s"Invalid move: not a valid diagonal move for ($startRow, $startCol) to ($endRow, $endCol)")
        false
      }
    } else {
      false
    }
  }

  def getValidMoves(startRow: Int, startCol: Int, board: Board, pieceColor: String, currentTurn: PieceColor.Value): List[(Int, Int)] = {
    val directions = List((-1, -1), (-1, 1), (1, -1), (1, 1))

    val validMoves = directions.flatMap { case (rowOffset, colOffset) =>
      val potentialMoves = List(
        (startRow + rowOffset, startCol + colOffset), // Regular move
        (startRow + 2 * rowOffset, startCol + 2 * colOffset) // Jump move
      )

      potentialMoves.filter { case (endRow, endCol) =>
        isWithinBounds(endRow, endCol) &&
          isValidMove(startRow, startCol, endRow, endCol, board, pieceColor, currentTurn)
      }
    }

    println(s"Valid moves for piece at ($startRow, $startCol): ${validMoves.mkString(", ")}")
    validMoves
  }

  private def isWithinBounds(row: Int, col: Int): Boolean = {
    row >= 0 && row < 8 && col >= 0 && col < 8
  }
}