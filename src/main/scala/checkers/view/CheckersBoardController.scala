package checkers.view

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.scene.Parent
import javafx.fxml.FXMLLoader

class CheckersBoardController {

  @FXML private var boardGrid: GridPane = _
  @FXML private var pauseButton: Button = _

  @FXML
  private def initialize(): Unit = {
    // Initialize the checkers board
    for (row <- 0 until 8; col <- 0 until 8) {
      val cell = new StackPane()
      cell.setStyle(if ((row + col) % 2 == 0) "-fx-background-color: white;" else "-fx-background-color: black;")
      boardGrid.add(cell, col, row)
    }
  }

  @FXML
  private def handleCheckersBoard(): Unit = {
    // Handle state of checkers board

    // TBA
  }

  @FXML
  private def openPauseMenu(): Unit = {
    // Handle the pause button action
    println("Pause button clicked")
    // TBA
  }
}