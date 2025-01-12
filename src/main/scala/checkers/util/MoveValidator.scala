package checkers.util

import checkers.MainApp
import checkers.model.{Piece, Board, PieceColor}

object MoveValidator {

  // Reference for finding standard piece's valid move
  // Code inspired by ChatGPT
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
        isMoveValid(endRow, endCol, board)
      } else if (rowDiff == 2) {
        val middleRow = (startRow + endRow) / 2
        val middleCol = (startCol + endCol) / 2
        val canCapture = board.getPiece(middleRow, middleCol).exists(_.color != PieceColor.withName(pieceColor))
        canCapture && isMoveValid(endRow, endCol, board)
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

    if (rowDiff == colDiff && rowDiff > 0) { // Check if the move is diagonal
      val rowStep = (endRow - startRow) / rowDiff
      val colStep = (endCol - startCol) / colDiff

      var row = startRow + rowStep
      var col = startCol + colStep
      var encounteredPiece = false
      var consecutiveOpponentPieces = 0 // Track consecutive opponent pieces
      val occupiedPieces = scala.collection.mutable.ListBuffer[(Int, Int)]()

      while (row != endRow && col != endCol) {
        board.getPiece(row, col) match {
          case Some(piece) =>
            if (piece.color.toString == pieceColor) {
              // Same-color piece blocks the path
              return (false, List())
            }

            // Opponent piece encountered
            if (encounteredPiece) {
              consecutiveOpponentPieces += 1
            } else {
              consecutiveOpponentPieces = 1
            }

            if (consecutiveOpponentPieces >= 2) {
              // Block the move if there are two consecutive opponent pieces
              return (false, List())
            }

            encounteredPiece = true
            occupiedPieces += ((row, col))
          case None =>
            // Reset consecutive opponent count if an empty space is encountered
            consecutiveOpponentPieces = 0
        }
        row += rowStep
        col += colStep
      }
      // Ensure the destination is empty
      if (isMoveValid(endRow, endCol, board)) {
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

  // Reference for finding further capture moves
  // Code inspired by ChatGPT
  def findCaptureMoves(startRow: Int, startCol: Int, board: Board, pieceColor: String, isKing: Boolean): List[List[(Int, Int)]] = {
    val directions = if (isKing) {
      List((-1, -1), (-1, 1), (1, -1), (1, 1)) // Diagonal directions for kings
    } else if (MainApp.getSelectedColor() == "White") {
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

    val capturePaths = scala.collection.mutable.ListBuffer[List[(Int, Int)]]()

    def exploreCapturePath(currentRow: Int, currentCol: Int, visited: Set[(Int, Int)], currentPath: List[(Int, Int)]): Unit = {
      var canCaptureFurther = false

      for ((rowStep, colStep) <- directions) {
        var midRow = currentRow + rowStep
        var midCol = currentCol + colStep
        var endRow = currentRow + 2 * rowStep
        var endCol = currentCol + 2 * colStep

        while (isWithinBounds(endRow, endCol) && !visited.contains((midRow, midCol)) && !visited.contains((endRow, endCol))) {
          board.getPiece(midRow, midCol) match {
            case Some(midPiece) if midPiece.color != PieceColor.withName(pieceColor) =>
              if (isKing) {
                val (isValid, _) = MoveValidator.isValidKingMove(currentRow, currentCol, endRow, endCol, board, pieceColor, PieceColor.withName(pieceColor))
                if (isValid && isMoveValid(endRow, endCol, board)) {
                  canCaptureFurther = true
                  val newVisited = visited + ((midRow, midCol), (endRow, endCol))
                  exploreCapturePath(endRow, endCol, newVisited, currentPath :+ (endRow, endCol))
                }
              } else {
                if (isMoveValid(endRow, endCol, board)) {
                  canCaptureFurther = true
                  val newVisited = visited + ((midRow, midCol), (endRow, endCol))
                  exploreCapturePath(endRow, endCol, newVisited, currentPath :+ (endRow, endCol))
                }
              }
            case _ => // No capture possible in this direction
          }
          midRow += rowStep
          midCol += colStep
          endRow += 2 * rowStep
          endCol += 2 * colStep
        }
      }
      // If no further captures, add the current path
      if (!canCaptureFurther && currentPath.nonEmpty) {
        capturePaths += currentPath
      }
    }
    // Start exploring from the initial position
    exploreCapturePath(startRow, startCol, Set((startRow, startCol)), List())
    capturePaths.toList
  }

  private def isWithinBounds(row: Int, col: Int): Boolean = {
    row >= 0 && row < 8 && col >= 0 && col < 8
  }

  private def isMoveValid(row: Int, col: Int, board: Board): Boolean = {
    board.getPiece(row, col).isEmpty
  }
}