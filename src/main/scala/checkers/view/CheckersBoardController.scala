package checkers.view

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.scene.Parent
import javafx.fxml.FXMLLoader
import javafx.scene.image.ImageView

class CheckersBoardController {

  @FXML private var boardGrid: GridPane = _
  @FXML private var pauseButton: Button = _

  private var selectedColor: String = _

  def setSelectedColor(color: String): Unit = {
    selectedColor = color
  }

  @FXML
  private def initialize(): Unit = {
    // Initialize the checkers board
    for (row <- 0 until 8; col <- 0 until 8) {
      val cell = new StackPane()
      cell.setStyle(if ((row + col) % 2 == 0) "-fx-background-color: white;" else "-fx-background-color: black;")
      boardGrid.add(cell, col, row)
    }
    // Initialize the pieces
    initializePieces()
  }

  def initializePieces(): Unit = {
    if (selectedColor == "white") {
      for (row <- 0 until 3; col <- 0 until 8 if (row + col) % 2 != 0) {
        val piece = new ImageView(new javafx.scene.image.Image(getClass.getResourceAsStream("/images/black_standard.png")))
        val cell = new StackPane(piece)
        boardGrid.add(cell, col, row)
      }
      for (row <- 5 until 8; col <- 0 until 8 if (row + col) % 2 != 0) {
        val piece = new ImageView(new javafx.scene.image.Image(getClass.getResourceAsStream("/images/white_standard.png")))
        val cell = new StackPane(piece)
        boardGrid.add(cell, col, row)
      }
    } else {
      for (row <- 0 until 3; col <- 0 until 8 if (row + col) % 2 != 0) {
        val piece = new ImageView(new javafx.scene.image.Image(getClass.getResourceAsStream("/images/white_standard.png")))
        val cell = new StackPane(piece)
        boardGrid.add(cell, col, row)
      }
      for (row <- 5 until 8; col <- 0 until 8 if (row + col) % 2 != 0) {
        val piece = new ImageView(new javafx.scene.image.Image(getClass.getResourceAsStream("/images/black_standard.png")))
        val cell = new StackPane(piece)
        boardGrid.add(cell, col, row)
      }
    }
  }

  @FXML
  private def openPauseMenu(): Unit = {
    // Handle the pause button action
    println("Pause button clicked")
    // TBA
  }
}