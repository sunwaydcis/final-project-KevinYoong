package checkers.view

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.MouseEvent
import checkers.model.{Board, Piece, PieceColor, PieceType}
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent,Scene}
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

  def setSelectedColor(color: String): Unit = {
    selectedColor = color
  }

  @FXML
  private def initialize(): Unit = {
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

    boardGrid.getChildren.forEach {
      case button: Button =>
        val row = GridPane.getRowIndex(button)
        val col = GridPane.getColumnIndex(button)
        if ((row + col) % 2 != 0) {
          board.getPiece(row, col).foreach { piece =>
            val image = if (piece.color == PieceColor.White) whitePieceImage else blackPieceImage
            button.setGraphic(new ImageView(image))
            pieceMap(button) = (row, col)
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
        selectedPiece = piece
        selectedPieceRow = row
        selectedPieceCol = col
        println(s"Selected piece at row: $selectedPieceRow, col: $selectedPieceCol")
      }
    } else {
      if (MoveValidator.isValidMove(selectedPieceRow, selectedPieceCol, row, col, board, selectedPiece.color.toString)) {
        board.movePiece(selectedPieceRow, selectedPieceCol, row, col)
        board.handleJump(selectedPieceRow, selectedPieceCol, row, col)
        updateBoardVisuals(selectedPieceRow, selectedPieceCol, row, col)
        selectedPiece = null
      } else {
        println("Invalid move or destination occupied")
      }
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