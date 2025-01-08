package checkers.view

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.stage.Stage
import javafx.scene.Scene

class CheckersBoardController {

  @FXML private var boardGrid: GridPane = _
  @FXML private var pauseButton: Button = _
  private var selectedPiece: ImageView = _
  private var selectedPieceRow: Int = _
  private var selectedPieceCol: Int = _
  private var selectedColor: String = _

  def setSelectedColor(color: String): Unit = {
    selectedColor = color
  }

  @FXML
  private def initialize(): Unit = {
    // Initialize the pieces
    initializePieces()
    // Set up event handlers for buttons
    boardGrid.getChildren.forEach {
      case button: Button =>
        button.setOnMouseClicked((event: MouseEvent) => handleCheckersMovement(event))
      case _ =>
    }
  }

  def initializePieces(): Unit = {
    val blackPieceImage = new javafx.scene.image.Image(getClass.getResourceAsStream("/images/black_standard.png"))
    val whitePieceImage = new javafx.scene.image.Image(getClass.getResourceAsStream("/images/white_standard.png"))

    boardGrid.getChildren.forEach {
      case button: Button =>
        val row = GridPane.getRowIndex(button)
        val col = GridPane.getColumnIndex(button)
        if ((row + col) % 2 != 0) {
          if (selectedColor == "white") {
            if (row < 3) {
              button.setGraphic(new ImageView(blackPieceImage))
            } else if (row > 4) {
              button.setGraphic(new ImageView(whitePieceImage))
            }
          } else {
            if (row < 3) {
              button.setGraphic(new ImageView(whitePieceImage))
            } else if (row > 4) {
              button.setGraphic(new ImageView(blackPieceImage))
            }
          }
        }
      case _ =>
    }
  }

  @FXML
  private def handleCheckersMovement(event: MouseEvent): Unit = {
    val button = event.getSource.asInstanceOf[Button]
    val row = GridPane.getRowIndex(button)
    val col = GridPane.getColumnIndex(button)
    if (selectedPiece == null) {
      // Select a piece
      if (button.getGraphic != null && button.getGraphic.isInstanceOf[ImageView]) {
        selectedPiece = button.getGraphic.asInstanceOf[ImageView]
        selectedPieceRow = row
        selectedPieceCol = col
        println(s"Selected piece at row: $selectedPieceRow, col: $selectedPieceCol")
      }
    } else {
      // Move the selected piece
      if (button.getGraphic == null) {
        button.setGraphic(new ImageView(selectedPiece.getImage))
        val filteredList = boardGrid.getChildren
          .filtered(node => GridPane.getRowIndex(node) == selectedPieceRow && GridPane.getColumnIndex(node) == selectedPieceCol)
        if (!filteredList.isEmpty) {
          val oldButton = filteredList.get(0).asInstanceOf[Button]
          oldButton.setGraphic(null)
          println(s"Moved piece to row: $row, col: $col")
          selectedPiece = null
        } else {
          throw new RuntimeException(s"No button found at row $selectedPieceRow, col $selectedPieceCol")
        }
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