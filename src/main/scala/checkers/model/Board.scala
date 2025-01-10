package checkers.model

import checkers.MainApp
import scala.collection.mutable

class Board {
  private val board: mutable.Map[(Int, Int), Piece] = mutable.Map()
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
  }

  private def removePiece(row: Int, col: Int): Unit = {
    board.remove((row, col))
  }

  def handleCapture(startRow: Int, startCol: Int, endRow: Int, endCol: Int): Unit = {
    val rowDiff = Math.abs(endRow - startRow)
    if (rowDiff == 2) {
      val middleRow = (startRow + endRow) / 2
      val middleCol = (startCol + endCol) / 2
      getPiece(middleRow, middleCol).foreach { jumpedPiece =>
        if (MainApp.getSelectedColor().toLowerCase() == "white") {
          if (jumpedPiece.color == PieceColor.White) {
            remainingWhitePieces -= 1
            println(s"Player 1 remaining pieces: $remainingWhitePieces")
          } else {
            remainingBlackPieces -= 1
            println(s"Player 2 remaining pieces: $remainingBlackPieces")
          }
        } else if (MainApp.getSelectedColor().toLowerCase() == "black") {
          if (jumpedPiece.color == PieceColor.White) {
            remainingWhitePieces -= 1
            println(s"Player 1 remaining pieces: $remainingWhitePieces")
          } else {
            remainingBlackPieces -= 1
            println(s"Player 2 remaining pieces: $remainingBlackPieces")
          }
        }
        removePiece(middleRow, middleCol)
      }
    }
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