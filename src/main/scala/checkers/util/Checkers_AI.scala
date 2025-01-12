package checkers.util

import checkers.model.{Board, PieceColor}
import scala.util.Random

// References for implementing the checkers AI using the minimax algorithm:
// https://github.com/techwithtim/Python-Checkers-AI/blob/master/minimax/algorithm.py
// https://www.youtube.com/watch?v=RjdrFHEgV2o
class Checkers_AI(board: Board, player2Color: PieceColor.Value, player1Color: PieceColor.Value) {

  def getBestMove(): ((Int, Int), (Int, Int)) = {
    val depth = 7
    val moves = generateAllMoves(player2Color)
    var bestMove: ((Int, Int), (Int, Int)) = null
    var bestValue = Int.MinValue
    val random = new Random()

    for (move <- moves) {
      val moveValue = evaluateMove(move, depth - 1, Int.MinValue, Int.MaxValue, maximizingPlayer = false)
      if (moveValue > bestValue || (moveValue == bestValue && random.nextBoolean())) {
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
        val eval = evaluateMove(move, depth - 1, alphaVar, beta, maximizingPlayer = false)
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
        val eval = evaluateMove(move, depth - 1, alpha, betaVar, maximizingPlayer = true)
        minEval = Math.min(minEval, eval)
        betaVar = Math.min(betaVar, eval)
        if (betaVar <= alpha) {
          return minEval
        }
      }
      minEval
    }
  }

  private def evaluateMove(move: ((Int, Int), (Int, Int)), depth: Int, alpha: Int, beta: Int, maximizingPlayer: Boolean): Int = {
    val (start, end) = move
    val piece = board.getPiece(start._1, start._2).get
    board.movePiece(start._1, start._2, end._1, end._2)
    val moveValue = minimax(board, depth, alpha, beta, maximizingPlayer)
    board.movePiece(end._1, end._2, start._1, start._2) // Undo move
    moveValue
  }

  private def evaluateBoard(board: Board): Int = {
    board.remainingBlackPieces - board.remainingWhitePieces
  }

  private def generateAllMoves(color: PieceColor.Value): Seq[((Int, Int), (Int, Int))] = {
    (for {
      row <- 0 until 8
      col <- 0 until 8
      piece <- board.getPiece(row, col) if piece.color == color
    } yield {
      if (piece.isKing) {
        MoveValidator.getValidKingMoves(row, col, board, piece.color.toString, color).map((row, col) -> _)
      } else {
        MoveValidator.getValidStandardMoves(row, col, board, piece.color.toString, color).map((row, col) -> _)
      }
    }).flatten
  }
}