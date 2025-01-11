package checkers.util

import checkers.model.{Board, PieceColor}

class Checkers_AI(board: Board, player2Color: PieceColor.Value, player1Color: PieceColor.Value) {

  def getBestMove(): ((Int, Int), (Int, Int)) = {
    val depth = 3
    val moves = generateAllMoves(player2Color)
    var bestMove: ((Int, Int), (Int, Int)) = null
    var bestValue = Int.MinValue

    for (move <- moves) {
      val (start, end) = move
      val piece = board.getPiece(start._1, start._2).get
      board.movePiece(start._1, start._2, end._1, end._2)
      val moveValue = minimax(board, depth - 1, Int.MinValue, Int.MaxValue, maximizingPlayer = false)
      board.movePiece(end._1, end._2, start._1, start._2) // Undo move

      if (moveValue > bestValue) {
        bestValue = moveValue
        bestMove = move
      }
    }
    bestMove
  }

  private def minimax(board: Board, depth: Int, alpha: Int, beta: Int, maximizingPlayer: Boolean): Int = {
    if (depth == 0 || board.winner().isDefined) {
      return evaluateBoard(board)
    }

    if (maximizingPlayer) {
      var maxEval = Int.MinValue
      val moves = generateAllMoves(player2Color)
      var alphaVar = alpha

      for (move <- moves) {
        val (start, end) = move
        val piece = board.getPiece(start._1, start._2).get
        board.movePiece(start._1, start._2, end._1, end._2)
        val eval = minimax(board, depth - 1, alphaVar, beta, maximizingPlayer = false)
        board.movePiece(end._1, end._2, start._1, start._2) // Undo move
        maxEval = Math.max(maxEval, eval)
        alphaVar = Math.max(alphaVar, eval)
        if (beta <= alphaVar) {
          return maxEval
        }
      }
      maxEval
    } else {
      var minEval = Int.MaxValue
      val moves = generateAllMoves(player1Color)
      var betaVar = beta

      for (move <- moves) {
        val (start, end) = move
        val piece = board.getPiece(start._1, start._2).get
        board.movePiece(start._1, start._2, end._1, end._2)
        val eval = minimax(board, depth - 1, alpha, betaVar, maximizingPlayer = true)
        board.movePiece(end._1, end._2, start._1, start._2) // Undo move
        minEval = Math.min(minEval, eval)
        betaVar = Math.min(betaVar, eval)
        if (betaVar <= alpha) {
          return minEval
        }
      }
      minEval
    }
  }

  private def evaluateBoard(board: Board): Int = {
    // Simple evaluation function: difference in piece count
    board.remainingBlackPieces - board.remainingWhitePieces
  }

  private def generateAllMoves(color: PieceColor.Value): Seq[((Int, Int), (Int, Int))] = {
    (for {
      row <- 0 until 8
      col <- 0 until 8
      piece <- board.getPiece(row, col) if piece.color == color
    } yield MoveValidator.getValidMoves(row, col, board, piece.color.toString, color, piece.isKing).map((row, col) -> _)).flatten
  }
}