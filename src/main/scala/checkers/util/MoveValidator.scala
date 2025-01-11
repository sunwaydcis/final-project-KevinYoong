package checkers.util

import checkers.MainApp
import checkers.model.{Board, PieceColor}

object MoveValidator {

  def isValidStandardMove(startRow: Int, startCol: Int, endRow: Int, endCol: Int, board: Board, pieceColor: String, currentTurn: PieceColor.Value, isKing: Boolean): Boolean = {
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

    val isForwardMove = isKing || ( // Allow both directions for king pieces
      (pieceColor == "White" && currentTurn == PieceColor.White && MainApp.getSelectedColor() == "White" && endRow < startRow) ||
        (pieceColor == "Black" && currentTurn == PieceColor.Black && MainApp.getSelectedColor() == "White" && endRow > startRow) ||
        (pieceColor == "White" && currentTurn == PieceColor.White && MainApp.getSelectedColor() == "Black" && endRow > startRow) ||
        (pieceColor == "Black" && currentTurn == PieceColor.Black && MainApp.getSelectedColor() == "Black" && endRow < startRow)
      )

    if ((isForwardMove || isKing) && rowDiff == colDiff) {
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

  def isValidKingMove(startRow: Int, startCol: Int, endRow: Int, endCol: Int, board: Board, pieceColor: String, currentTurn: PieceColor.Value): Boolean = {
    if (PieceColor.withName(pieceColor) != currentTurn) {
      println(s"Not $pieceColor's turn")
      return false
    }

    val rowDiff = Math.abs(endRow - startRow)
    val colDiff = Math.abs(endCol - startCol)

    if (rowDiff != colDiff) return false // Ensure the move is diagonal

    val rowStep = if (endRow > startRow) 1 else -1
    val colStep = if (endCol > startCol) 1 else -1

    var row = startRow + rowStep
    var col = startCol + colStep

    while (row != endRow && col != endCol) {
      if (board.getPiece(row, col).isDefined) return false // Path is blocked
      row += rowStep
      col += colStep
    }

    // Ensure the destination is empty
    if (board.getPiece(endRow, endCol).isDefined) {
      println(s"Destination ($endRow, $endCol) is occupied")
      return false
    }

    true
  }

  def getValidMoves(startRow: Int, startCol: Int, board: Board, pieceColor: String, currentTurn: PieceColor.Value, isKing: Boolean): List[(Int, Int)] = {
    val directions = if (isKing) {
      List((-1, -1), (-1, 1), (1, -1), (1, 1))
    } else if (pieceColor == "White") {
      List((-1, -1), (-1, 1)) // White pieces move upwards
    } else {
      List((1, -1), (1, 1)) // Black pieces move downwards
    }

    val validMoves = directions.flatMap { case (rowOffset, colOffset) =>
      val potentialMoves = if (isKing) {
        (1 to 7).map(i => (startRow + i * rowOffset, startCol + i * colOffset)).toList
      } else {
        List(
          (startRow + rowOffset, startCol + colOffset), // Regular move
          (startRow + 2 * rowOffset, startCol + 2 * colOffset) // Jump move
        )
      }

      println(s"Evaluating potential moves for piece at ($startRow, $startCol): ${potentialMoves.mkString(", ")}")

      potentialMoves.filter { case (endRow, endCol) =>
        isWithinBounds(endRow, endCol) &&
          (if (isKing) {
            println(s"Using isValidKingMove for piece at ($startRow, $startCol) to ($endRow, $endCol)")
            isValidKingMove(startRow, startCol, endRow, endCol, board, pieceColor, currentTurn)
          } else {
            val isForwardMove = (pieceColor == "White" && endRow < startRow) || (pieceColor == "Black" && endRow > startRow)
            println(s"Using isValidStandardMove for piece at ($startRow, $startCol) to ($endRow, $endCol)")
            isForwardMove && isValidStandardMove(startRow, startCol, endRow, endCol, board, pieceColor, currentTurn, isKing)
          })
      }
    }

    println(s"Valid moves for piece at ($startRow, $startCol): ${validMoves.mkString(", ")}")
    validMoves
  }

  private def isWithinBounds(row: Int, col: Int): Boolean = {
    row >= 0 && row < 8 && col >= 0 && col < 8
  }
}