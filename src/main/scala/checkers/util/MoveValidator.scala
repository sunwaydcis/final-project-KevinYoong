package checkers.util

import checkers.MainApp
import checkers.model.{Piece, Board, PieceColor}

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

    val isForwardMove = isKing || (// Allow both directions for king pieces
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

  def isValidKingMove(startRow: Int, startCol: Int, endRow: Int, endCol: Int, board: Board, pieceColor: String, currentTurn: PieceColor.Value): (Boolean, List[(Int, Int)]) = {
    val rowDiff = Math.abs(endRow - startRow)
    val colDiff = Math.abs(endCol - startCol)

    // Check if the move is diagonal
    if (rowDiff == colDiff && rowDiff > 0) {
      val rowStep = (endRow - startRow) / rowDiff
      val colStep = (endCol - startCol) / colDiff

      var row = startRow + rowStep
      var col = startCol + colStep
      var encounteredPiece = false
      var adjacentOpponentPiece = false
      val occupiedPieces = scala.collection.mutable.ListBuffer[(Int, Int)]()

      while (row != endRow && col != endCol) {
        board.getPiece(row, col) match {
          case Some(piece) =>
            // If an encountered piece is of the same color, the move is invalid
            if (piece.color.toString == pieceColor) {
              return (false, List())
            }
            // If an encountered piece is of the opposite color and we have already encountered a piece
            if (encounteredPiece) {
              // Check if the pieces are adjacent
              if (adjacentOpponentPiece) {
                return (false, List())
              }
              adjacentOpponentPiece = true
            }
            encounteredPiece = true
            occupiedPieces += ((row, col))
          case None =>
            // If an empty space is encountered after a piece, reset the adjacent opponent piece flag
            adjacentOpponentPiece = false
        }
        row += rowStep
        col += colStep
      }
      // Ensure the destination is empty
      if (board.getPiece(endRow, endCol).isEmpty) {
        (true, occupiedPieces.toList)
      } else {
        (false, List())
      }
    } else {
      (false, List())
    }
  }

  def getValidStandardMoves(startRow: Int, startCol: Int, board: Board, pieceColor: String, currentTurn: PieceColor.Value): List[(Int, Int)] = {
    val directions = if (MainApp.getSelectedColor() == "White") {
      if (pieceColor == "White") {
        List((-1, -1), (-1, 1)) // White pieces move upwards
      } else {
        List((1, -1), (1, 1)) // Black pieces move downwards
      }
    } else {
      if (pieceColor == "White") {
        List((1, -1), (1, 1)) // White pieces move downwards
      } else {
        List((-1, -1), (-1, 1)) // Black pieces move upwards
      }
    }

    val validMoves = directions.flatMap { case (rowOffset, colOffset) =>
      List(
        (startRow + rowOffset, startCol + colOffset), // Regular move
        (startRow + 2 * rowOffset, startCol + 2 * colOffset) // Jump move
      ).filter { case (endRow, endCol) =>
        isWithinBounds(endRow, endCol) &&
          ((pieceColor == "White" && MainApp.getSelectedColor() == "White" && endRow < startRow) ||
          (pieceColor == "Black" && MainApp.getSelectedColor() == "White" && endRow > startRow) ||
          (pieceColor == "White" && MainApp.getSelectedColor() == "Black" && endRow > startRow) ||
          (pieceColor == "Black" && MainApp.getSelectedColor() == "Black" && endRow < startRow)) &&
          isValidStandardMove(startRow, startCol, endRow, endCol, board, pieceColor, currentTurn, isKing = false)
      }
    }

    println(s"Valid moves for piece at ($startRow, $startCol): ${validMoves.mkString(", ")}")
    validMoves
  }

  def getValidKingMoves(startRow: Int, startCol: Int, board: Board, pieceColor: String, currentTurn: PieceColor.Value): List[(Int, Int)] = {
    val directions = List((-1, -1), (-1, 1), (1, -1), (1, 1))

    val validMoves = directions.flatMap { case (rowOffset, colOffset) =>
      (1 to 7).map(i => (startRow + i * rowOffset, startCol + i * colOffset)).toList.filter { case (endRow, endCol) =>
        isWithinBounds(endRow, endCol) && {
          val (isValid, _) = isValidKingMove(startRow, startCol, endRow, endCol, board, pieceColor, currentTurn)
          if (!isValid) {
            board.getPiece(endRow, endCol).foreach(piece => println(s"Detected occupied space at ($endRow, $endCol) with ${piece.color.toString.toLowerCase} piece"))
          }
          isValid
        }
      }
    }

    println(s"Valid moves for piece at ($startRow, $startCol): ${validMoves.mkString(", ")}")
    validMoves
  }

  private def isWithinBounds(row: Int, col: Int): Boolean = {
    row >= 0 && row < 8 && col >= 0 && col < 8
  }
}