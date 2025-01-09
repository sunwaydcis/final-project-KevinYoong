package checkers.view

import checkers.MainApp
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.MouseEvent
import checkers.model.{Board, Piece, PieceColor, Player}
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage
import checkers.util.MoveValidator

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
  private var player1: Player = _
  private var player2: Player = _

  def setSelectedColor(color: String): Unit = {
    selectedColor = MainApp.getSelectedColor()
    if (selectedColor == null) {
      throw new IllegalStateException("Selected color must be...")
    } else{
      println(s"selectedColor is: $selectedColor")
    }
  }

  def initializePlayers(): Unit = {
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

    println(s"Player 1 is ${player1.name} with color ${player1.color}")
    println(s"Player 2 is ${player2.name} with color ${player2.color}")
    println(s"Current player is ${currentPlayer.name}")
  }

  @FXML
  def initialize(): Unit = {
    setSelectedColor(selectedColor)
    if (player1 == null || player2 == null) {
      initializePlayers()
    }
    initializePieces()
    boardGrid.getChildren.forEach {
      case button: Button =>
        button.setOnMouseClicked((event: MouseEvent) => handleCheckersMovement(event))
      case _ =>
    }
  }

  def initializePieces(): Unit = {
    board.initializePieces(selectedColor)
    val blackPieceImage = new Image(getClass.getResourceAsStream("/images/black_standard.png"))
    val whitePieceImage = new Image(getClass.getResourceAsStream("/images/white_standard.png"))

    val isWhiteSelected = selectedColor.toLowerCase == "white"

    boardGrid.getChildren.forEach {
      case button: Button =>
        val row = GridPane.getRowIndex(button)
        val col = GridPane.getColumnIndex(button)
        val adjustedRow = if (isWhiteSelected) 7 - row else row // Adjust row index if white is selected
        if ((adjustedRow.asInstanceOf[Int] + col) % 2 != 0) { // Convert adjustedRow to Int
          board.getPiece(adjustedRow.asInstanceOf[Int], col).foreach { piece => // Convert adjustedRow to Int
            val image = if (piece.color == PieceColor.White) whitePieceImage else blackPieceImage
            button.setGraphic(new ImageView(image))
            pieceMap(button) = (adjustedRow.asInstanceOf[Int], col) // Convert adjustedRow to Int
            if (piece.color == player1.color) player1.pieces = piece :: player1.pieces else player2.pieces = piece :: player2.pieces
          }
        }
      case _ =>
    }
  }

  @FXML
  private def handleCheckersMovement(event: MouseEvent): Unit = {
    val button = event.getSource.asInstanceOf[Button]
    val row = Option(GridPane.getRowIndex(button)).map(_.intValue()).getOrElse(0)
    val col = Option(GridPane.getColumnIndex(button)).map(_.intValue()).getOrElse(0)

    if (selectedPiece == null) {
      pieceMap.get(button).flatMap { case (r, c) => board.getPiece(r, c) }.foreach { piece =>
        if (piece.color == currentPlayer.color && currentPlayer.checkTurn) {
          selectedPiece = piece
          selectedPieceRow = row
          selectedPieceCol = col
          println(s"Selected ${selectedPiece.color} piece at row: $selectedPieceRow, col: $selectedPieceCol")
        } else {
          println(s"It's ${currentPlayer.name}'s turn")
        }
      }
    } else {
      if (MoveValidator.isValidMove(selectedPieceRow, selectedPieceCol, row, col, board, selectedPiece.color.toString, currentPlayer.color)) {
        board.movePiece(selectedPieceRow, selectedPieceCol, row, col)
        board.handleJump(selectedPieceRow, selectedPieceCol, row, col)
        updateBoardVisuals(selectedPieceRow, selectedPieceCol, row, col)
        selectedPiece = null
        switchTurn()
        println(s"It's now ${currentPlayer.name}'s turn")
        checkForLoss()
      } else {
        println("Invalid move or destination occupied")
      }
    }
  }

  private def switchTurn(): Unit = {
    currentPlayer.isTurn = false
    currentPlayer = if (currentPlayer == player1) player2 else player1
    currentPlayer.isTurn = true
    println(s"Switched turn. It's now ${currentPlayer.name}'s turn")
    println(s"Player 1 turn: ${player1.isTurn}, Player 2 turn: ${player2.isTurn}")
  }

  private def checkForLoss(): Unit = {
    if (player1.remainingPieces == 0) {
      println(s"${player1.name} has lost!")
    } else if (player2.remainingPieces == 0) {
      println(s"${player2.name} has lost!")
    }
  }

  private def updateBoardVisuals(startRow: Int, startCol: Int, endRow: Int, endCol: Int): Unit = {
    val button = boardGrid.getChildren
      .filtered(node => Option(GridPane.getRowIndex(node)).map(_.intValue()).getOrElse(0) == endRow && Option(GridPane.getColumnIndex(node)).map(_.intValue()).getOrElse(0) == endCol)
      .get(0).asInstanceOf[Button]

    button.setGraphic(new ImageView(if (selectedPiece.color == PieceColor.White) new Image(getClass.getResourceAsStream("/images/white_standard.png")) else new Image(getClass.getResourceAsStream("/images/black_standard.png"))))
    pieceMap(button) = (endRow, endCol)

    val oldButtonList = boardGrid.getChildren
      .filtered(node => Option(GridPane.getRowIndex(node)).map(_.intValue()).getOrElse(0) == startRow && Option(GridPane.getColumnIndex(node)).map(_.intValue()).getOrElse(0) == startCol)
    if (!oldButtonList.isEmpty) {
      val oldButton = oldButtonList.get(0).asInstanceOf[Button]
      oldButton.setGraphic(null)
      pieceMap.remove(oldButton)
      println(s"Moved piece to row: $endRow, col: $endCol")
    } else {
      println("Old button not found")
    }

    // Update the visual representation of the jumped piece
    val rowDiff = Math.abs(endRow - startRow)
    if (rowDiff == 2) {
      val middleRow = (startRow + endRow) / 2
      val middleCol = (startCol + endCol) / 2
      val middleButtonList = boardGrid.getChildren
        .filtered(node => Option(GridPane.getRowIndex(node)).map(_.intValue()).getOrElse(0) == middleRow && Option(GridPane.getColumnIndex(node)).map(_.intValue()).getOrElse(0) == middleCol)
      if (!middleButtonList.isEmpty) {
        val middleButton = middleButtonList.get(0).asInstanceOf[Button]
        middleButton.setGraphic(null)
        pieceMap.remove(middleButton)
        println(s"Removed jumped piece at row: $middleRow, col: $middleCol")
        val jumpedPiece = board.getPiece(middleRow, middleCol).get
        if (jumpedPiece.color == player1.color) player1.pieces = player1.pieces.filterNot(_ == jumpedPiece) else player2.pieces = player2.pieces.filterNot(_ == jumpedPiece)
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