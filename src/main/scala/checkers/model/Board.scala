package checkers.model

import checkers.MainApp

import scala.collection.mutable

class Board {
  private val board: mutable.Map[(Int, Int), Piece] = mutable.Map()
  private var capturedPieces: List[(Int, Int)] = List()
  var player1: Player = _
  var player2: Player = _
  var remainingWhitePieces: Int = 12
  var remainingBlackPieces: Int = 12

  def setPlayers(player1: Player, player2: Player): Unit = {
    this.player1 = player1
    this.player2 = player2
  }

  def initializePieces(selectedColor: String): Unit = {
    for (row <- 0 until 8; col <- 0 until 8 if (row + col) % 2 != 0) {
      if (MainApp.getSelectedColor().toLowerCase() == "white") {
        if (row < 3) {
          board((row, col)) = Piece(PieceType.Standard, PieceColor.Black)
        } else if (row > 4) {
          board((row, col)) = Piece(PieceType.Standard, PieceColor.White)
        }
      } else if (MainApp.getSelectedColor().toLowerCase() == "black") {
        if (row < 3) {
          board((row, col)) = Piece(PieceType.Standard, PieceColor.White)
        } else if (row > 4) {
          board((row, col)) = Piece(PieceType.Standard, PieceColor.Black)
        }
      }
    }
  }

  def getPiece(row: Int, col: Int): Option[Piece] = {
    board.get((row, col))
  }

  def movePiece(startRow: Int, startCol: Int, endRow: Int, endCol: Int): Unit = {
    val piece = board.get((startRow, startCol))
    board.remove((startRow, startCol))
    board.update((endRow, endCol), piece.get)

    // Check if the piece should be promoted to a king
    if (MainApp.getSelectedColor().toLowerCase() == "white") {
      if ((piece.get.color == PieceColor.White && endRow == 0) || (piece.get.color == PieceColor.Black && endRow == 7)) {
        board.update((endRow, endCol), piece.get.promoteToKing())
        println(s"Piece at ($endRow, $endCol) promoted to king")
      }
    } else if (MainApp.getSelectedColor().toLowerCase() == "black") {
      if ((piece.get.color == PieceColor.White && endRow == 7) || (piece.get.color == PieceColor.Black && endRow == 0)) {
        board.update((endRow, endCol), piece.get.promoteToKing())
        println(s"Piece at ($endRow, $endCol) promoted to king")
      }
    }
  }

  def getCapturedPieces(): List[(Int, Int)] = {
    val pieces = capturedPieces
    capturedPieces = List() // Clear the list after returning
    pieces
  }

  private def removePiece(row: Int, col: Int): Unit = {
    board.remove((row, col))
  }

  def handleStandardCapture(startRow: Int, startCol: Int, endRow: Int, endCol: Int, updateBoardVisuals: (Int, Int, Int, Int) => Unit): Unit = {
    val rowDiff = Math.abs(endRow - startRow)
    if (rowDiff == 2) { // Check if this is a leap
      val middleRow = (startRow + endRow) / 2
      val middleCol = (startCol + endCol) / 2
      getPiece(middleRow, middleCol).foreach { capturedPiece =>
        updateBoardVisuals(startRow, startCol, endRow, endCol)
        // Update the visual representation of the captured piece
        updateBoardVisuals(middleRow, middleCol, middleRow, middleCol)
        // Remove the middle piece
        removePiece(middleRow, middleCol)
        println(s"Removed piece at ($middleRow, $middleCol)")
      }
    } else {
      println("Invalid move: Standard capture requires a leap of 2 rows")
    }
  }

  def handleKingCapture(startRow: Int, startCol: Int, endRow: Int, endCol: Int, occupiedSpaces: List[(Int, Int)]): Unit = {
    // Remove the pieces at the occupied spaces
    occupiedSpaces.foreach { case (row, col) =>
      removePiece(row, col)
    }

    // Add the occupied spaces to the captured pieces list
    capturedPieces ++= occupiedSpaces
  }

  def winner(): Option[Player] = {
    if (player1.remainingPieces == 0) {
      Some(player2)
    } else if (player2.remainingPieces == 0) {
      Some(player1)
    } else {
      None
    }
  }
}