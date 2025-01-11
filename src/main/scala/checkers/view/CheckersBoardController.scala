package checkers.view

import checkers.MainApp
import checkers.model.{Board, Piece, PieceColor, Player}
import checkers.util.{Checkers_AI, MoveValidator}
import javafx.application.Platform
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.Button
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

import scala.jdk.CollectionConverters.*

class CheckersBoardController {

  @FXML private var boardGrid: GridPane = _
  @FXML private var pauseButton: Button = _
  private var selectedPiece: Piece = _
  private var selectedPieceRow: Int = _
  private var selectedPieceCol: Int = _
  private var selectedColor: String = _
  private val pieceMap = scala.collection.mutable.Map[Button, (Int, Int)]()
  private val board = new Board()
  private var currentPlayer: Player = _
  private var ai: Checkers_AI = _
  var isAI: Boolean = _
  private var player1: Player = _
  private var player2: Player = _
  private var aiTurnTaken: Boolean = false

  private def setSelectedColor(color: String): Unit = {
    selectedColor = MainApp.getSelectedColor()
    if (selectedColor == null) {
      throw new IllegalStateException("Selected color must be set before initializing players")
    } else {
      println(s"selectedColor is: $selectedColor")
    }
  }

  def initializeGame(isAI: Boolean): Unit = {
    this.isAI = isAI
    initializePlayers()
    if (isAI && currentPlayer == player2 && !aiTurnTaken) {
      handleAITurn()
      aiTurnTaken = true
    }
  }

  private def initializePlayers(): Unit = {
    if (selectedColor == null) {
      throw new IllegalStateException("Selected color must be set before initializing players")
    }

    selectedColor.toLowerCase() match {
      case "white" =>
        player1 = new Player("Player 1", PieceColor.White)
        player2 = new Player("Player 2", PieceColor.Black)
      case "black" =>
        player1 = new Player("Player 1", PieceColor.Black)
        player2 = new Player("Player 2", PieceColor.White)
      case _ =>
        throw new IllegalArgumentException("Invalid color selected")
    }

    // Ensure the black player always starts
    if (player1.color == PieceColor.Black) {
      currentPlayer = player1
      player1.isTurn = true
    } else if (player2.color == PieceColor.Black) {
      currentPlayer = player2
      player2.isTurn = true
    }

    board.setPlayers(player1, player2)
    if (isAI) {
      ai = new Checkers_AI(board, player2.color, player1.color)
    }
    println(s"Player 1 is ${player1.name} with color ${player1.color}")
    println(s"Player 2 is ${player2.name} with color ${player2.color}")
    println(s"Current player is ${currentPlayer.name} moving ${currentPlayer.color} pieces")
  }

  @FXML
  def initialize(): Unit = {
    setSelectedColor(selectedColor)
    initializePieces()
    boardGrid.getChildren.forEach {
      case button: Button =>
        button.setOnMouseClicked((event: MouseEvent) => {
          val row = Option(GridPane.getRowIndex(button)).map(_.intValue()).getOrElse(0)
          val col = Option(GridPane.getColumnIndex(button)).map(_.intValue()).getOrElse(0)
          board.getPiece(row, col) match {
            case Some(piece) if piece.isKing =>
              println(s"Using handleKingPieceMovement for piece at ($row, $col)")
              handleKingPieceMovement(event)
            case Some(_) =>
              println(s"Using handleStandardPieceMovement for piece at ($row, $col)")
              handleStandardPieceMovement(event)
            case None =>
              if (selectedPiece != null && selectedPiece.isKing) {
                println(s"Using handleKingPieceMovement for empty space at ($row, $col)")
                handleKingPieceMovement(event)
              } else {
                println(s"Using handleStandardPieceMovement for empty space at ($row, $col)")
                handleStandardPieceMovement(event)
              }
          }
        })
      case _ =>
    }
  }

  private def initializePieces(): Unit = {
    board.initializePieces(selectedColor)

    for (row <- 0 until 8) {
      for (col <- 0 until 8 if (row + col) % 2 != 0) {
        board.getPiece(row, col).foreach { piece =>
          val button = boardGrid.getChildren
            .filtered(node => Option(GridPane.getRowIndex(node)).map(_.intValue()).getOrElse(0) == row && Option(GridPane.getColumnIndex(node)).map(_.intValue()).getOrElse(0) == col)
            .get(0).asInstanceOf[Button]
          val imagePath = piece.color match {
            case PieceColor.White => "/images/white_standard.png"
            case PieceColor.Black => "/images/black_standard.png"
          }
          button.setGraphic(new ImageView(new Image(getClass.getResourceAsStream(imagePath))))
          pieceMap(button) = (row, col)
        }
      }
    }
  }

  private def handleStandardPieceMovement(event: MouseEvent): Unit = {
    val button = event.getSource.asInstanceOf[Button]
    val row = Option(GridPane.getRowIndex(button)).map(_.intValue()).getOrElse(0)
    val col = Option(GridPane.getColumnIndex(button)).map(_.intValue()).getOrElse(0)

    // Clear all previous highlights
    boardGrid.getChildren.forEach {
      case btn: Button =>
        if (btn.getGraphic.isInstanceOf[Circle]) {
          btn.setGraphic(null)
        }
      case _ =>
    }

    // Get the piece associated with the clicked button
    pieceMap.get(button).flatMap { case (r, c) => board.getPiece(r, c) }.foreach { piece =>
      if (piece.color == currentPlayer.color) {
        // Update the selected piece regardless of whether a piece was already selected
        selectedPiece = piece
        selectedPieceRow = row
        selectedPieceCol = col
        println(s"Selected ${selectedPiece.color} piece at row: $selectedPieceRow, col: $selectedPieceCol")

        // Highlight valid moves for the newly selected piece
        val validMoves = MoveValidator.getValidStandardMoves(selectedPieceRow, selectedPieceCol, board, selectedPiece.color.toString, currentPlayer.color)
        validMoves.foreach { case (validRow, validCol) =>
          val validButton = boardGrid.getChildren
            .filtered(node => Option(GridPane.getRowIndex(node)).map(_.intValue()).getOrElse(0) == validRow && Option(GridPane.getColumnIndex(node)).map(_.intValue()).getOrElse(0) == validCol)
            .get(0).asInstanceOf[Button]
          highlightValidMove(validButton)
        }
        return // Exit early if a piece was selected
      }
    }

    // If no valid piece was selected but a piece is already selected, attempt to move it
    if (selectedPiece != null) {
      println(s"Attempting to move ${selectedPiece.color} piece from ($selectedPieceRow, $selectedPieceCol) to ($row, $col)")
      if (MoveValidator.isValidStandardMove(selectedPieceRow, selectedPieceCol, row, col, board, selectedPiece.color.toString, currentPlayer.color, selectedPiece.isKing)) {
        val rowDiff = Math.abs(row - selectedPieceRow)
        if (rowDiff == 2) {
          board.handleStandardCapture(selectedPieceRow, selectedPieceCol, row, col, updateBoardVisuals)
        } else {
          updateBoardVisuals(selectedPieceRow, selectedPieceCol, row, col)
        }
        selectedPiece = null // Reset the selection to allow new selections
        switchTurn()
      } else {
        println("Invalid move or destination occupied")
      }
    } else {
      println("No piece selected or invalid selection")
    }
  }

  private def handleKingPieceMovement(event: MouseEvent): Unit = {
    val button = event.getSource.asInstanceOf[Button]
    val row = Option(GridPane.getRowIndex(button)).map(_.intValue()).getOrElse(0)
    val col = Option(GridPane.getColumnIndex(button)).map(_.intValue()).getOrElse(0)

    // Clear all previous highlights
    boardGrid.getChildren.forEach {
      case btn: Button =>
        if (btn.getGraphic.isInstanceOf[Circle]) {
          btn.setGraphic(null)
        }
      case _ =>
    }

    // Get the piece associated with the clicked button
    pieceMap.get(button).flatMap { case (r, c) => board.getPiece(r, c) }.foreach { piece =>
      if (piece.color == currentPlayer.color && piece.isKing) {
        // Update the selected piece regardless of whether a piece was already selected
        selectedPiece = piece
        selectedPieceRow = row
        selectedPieceCol = col
        println(s"Selected ${selectedPiece.color} king piece at row: $selectedPieceRow, col: $selectedPieceCol")

        // Highlight valid moves for the newly selected piece
        val validMoves = MoveValidator.getValidKingMoves(selectedPieceRow, selectedPieceCol, board, selectedPiece.color.toString, currentPlayer.color)
        validMoves.foreach { case (validRow, validCol) =>
          val validButton = boardGrid.getChildren
            .filtered(node => Option(GridPane.getRowIndex(node)).map(_.intValue()).getOrElse(0) == validRow && Option(GridPane.getColumnIndex(node)).map(_.intValue()).getOrElse(0) == validCol)
            .get(0).asInstanceOf[Button]
          highlightValidMove(validButton)
        }
        return // Exit early if a piece was selected
      }
    }

    // If no valid piece was selected but a piece is already selected, attempt to move it
    if (selectedPiece != null && selectedPiece.isKing) {
      println(s"Attempting to move ${selectedPiece.color} king piece from ($selectedPieceRow, $selectedPieceCol) to ($row, $col)")
      println(s"Validating king move: ($selectedPieceRow, $selectedPieceCol) to ($row, $col)")
      if (MoveValidator.isValidKingMove(selectedPieceRow, selectedPieceCol, row, col, board, selectedPiece.color.toString, currentPlayer.color)) {
        board.movePiece(selectedPieceRow, selectedPieceCol, row, col)
        updateBoardVisuals(selectedPieceRow, selectedPieceCol, row, col)
        selectedPiece = null // Reset the selection to allow new selections
        switchTurn()
      } else {
        println("Invalid move or destination occupied")
      }
    } else {
      println("No piece selected or invalid selection")
    }
  }

  private def handleAITurn(): Unit = {
    new Thread(() => {
      try {
        Thread.sleep(1000) // 1 second delay
      } catch {
        case e: InterruptedException => e.printStackTrace()
      }
      Platform.runLater(() => {
        val bestMove = ai.getBestMove()
        if (bestMove != null) {
          board.movePiece(bestMove._1._1, bestMove._1._2, bestMove._2._1, bestMove._2._2)
          if (board.getPiece(bestMove._2._1, bestMove._2._2).exists(_.isKing)) {
            board.handleKingCapture(bestMove._1._1, bestMove._1._2, bestMove._2._1, bestMove._2._2)
          } else {
            board.handleStandardCapture(bestMove._1._1, bestMove._1._2, bestMove._2._1, bestMove._2._2, updateBoardVisuals)
          }
          switchTurn() // Switch back to the user after AI move
        } else {
          println("AI has no valid moves left")
          // Handle the scenario where the AI has no valid moves left
        }
      })
    }).start()
  }

  private def highlightValidMove(button: Button): Unit = {
    val circle = new Circle(5, Color.WHITE)
    circle.setMouseTransparent(true) // Make sure the circle does not interfere with button clicks
    button.setGraphic(circle)
  }

  private def switchTurn(): Unit = {
    currentPlayer.isTurn = false
    currentPlayer = if (currentPlayer == player1) player2 else player1
    currentPlayer.isTurn = true
    println(s"Switched turn. It's now ${currentPlayer.name}'s turn")

    if (isAI && currentPlayer == player2) {
      handleAITurn()
    }
  }

  private def checkForLoss(): Unit = {
    if (player1.remainingPieces == 0) {
      println(s"${player1.name} has lost!")
    } else if (player2.remainingPieces == 0) {
      println(s"${player2.name} has lost!")
    }
  }

  // Update the visual representation of the board
  private def updateBoardVisuals(startRow: Int, startCol: Int, endRow: Int, endCol: Int): Unit = {
    // Move the piece
    board.movePiece(startRow, startCol, endRow, endCol)

    // Update visuals for the moved piece
    val newButton = boardGrid.getChildren
      .filtered(node => Option(GridPane.getRowIndex(node)).map(_.intValue()).getOrElse(0) == endRow && Option(GridPane.getColumnIndex(node)).map(_.intValue()).getOrElse(0) == endCol)
      .get(0).asInstanceOf[Button]

    val movedPiece = board.getPiece(endRow, endCol).get
    val imagePath = if (movedPiece.color == PieceColor.White) {
      if (movedPiece.isKing) "/images/white_king.png" else "/images/white_standard.png"
    } else {
      if (movedPiece.isKing) "/images/black_king.png" else "/images/black_standard.png"
    }
    newButton.setGraphic(new ImageView(new Image(getClass.getResourceAsStream(imagePath))))
    pieceMap(newButton) = (endRow, endCol)

    // Clear the visual representation of the start position
    val oldButton = boardGrid.getChildren
      .filtered(node => Option(GridPane.getRowIndex(node)).map(_.intValue()).getOrElse(0) == startRow && Option(GridPane.getColumnIndex(node)).map(_.intValue()).getOrElse(0) == startCol)
      .get(0).asInstanceOf[Button]
    oldButton.setGraphic(null)
    pieceMap.remove(oldButton)

    // Update the visual representation of the jumped piece
    val capturedPieces = board.getCapturedPieces()
    capturedPieces.foreach { case (capturedRow, capturedCol) =>
      val capturedButtonList = boardGrid.getChildren
        .filtered(node => Option(GridPane.getRowIndex(node)).map(_.intValue()).getOrElse(0) == capturedRow && Option(GridPane.getColumnIndex(node)).map(_.intValue()).getOrElse(0) == capturedCol)
        .asScala
      if (capturedButtonList.nonEmpty) {
        val capturedButton = capturedButtonList.head.asInstanceOf[Button]
        capturedButton.setGraphic(null)
        pieceMap.remove(capturedButton)
        println(s"Removed captured piece at row: $capturedRow, col: $capturedCol")
      }
    }
  }

  @FXML
  private def openPauseMenu(): Unit = {
    val resource = getClass.getResource("/view/PauseScreen.fxml")
    if (resource == null) {
      throw new RuntimeException("PauseScreen.fxml not found")
    }
    val loader = new FXMLLoader(resource)
    val root = loader.load[Parent]()
    val controller = loader.getController[PauseScreenController]
    val dialog = new Stage()
    dialog.initOwner(pauseButton.getScene.getWindow)
    dialog.setScene(new Scene(root))
    controller.dialogStage = dialog
    dialog.showAndWait()
  }
}