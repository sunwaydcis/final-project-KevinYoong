package checkers.model

import checkers.MainApp

import scala.collection.mutable

class Board {
  private val board: mutable.Map[(Int, Int), Piece] = mutable.Map()

  // Initialize pieces on the board
  def initializePieces(selectedColor: String): Unit = {
    for (row <- 0 until 8; col <- 0 until 8 if (row + col) % 2 != 0) {
      if (MainApp.getSelectedColor().toLowerCase() == "white") {
        if (row < 3) {
          board((row, col)) = new Piece(PieceType.Standard, PieceColor.Black)
        } else if (row > 4) {
          board((row, col)) = new Piece(PieceType.Standard, PieceColor.White)
        }
      } else if (MainApp.getSelectedColor().toLowerCase() == "black") {
        if (row < 3) {
          board((row, col)) = new Piece(PieceType.Standard, PieceColor.White)
        } else if (row > 4) {
          board((row, col)) = new Piece(PieceType.Standard, PieceColor.Black)
        }
      }
    }
  }

  // Get the piece at a specific position
  def getPiece(row: Int, col: Int): Option[Piece] = {
    board.get((row, col))
  }

  // Move a piece from one position to another
  def movePiece(startRow: Int, startCol: Int, endRow: Int, endCol: Int): Unit = {
    val piece = board.remove((startRow, startCol))
    piece.foreach(board((endRow, endCol)) = _)
  }

  // Remove a piece from the board
  def removePiece(row: Int, col: Int): Unit = {
    board.remove((row, col))
  }

  // Handle jump logic
  def handleJump(startRow: Int, startCol: Int, endRow: Int, endCol: Int): Unit = {
    val rowDiff = Math.abs(endRow - startRow)
    if (rowDiff == 2) {
      val middleRow = (startRow + endRow) / 2
      val middleCol = (startCol + endCol) / 2
      removePiece(middleRow, middleCol)
    }
  }
}