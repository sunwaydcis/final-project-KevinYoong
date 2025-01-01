package checkers.view

import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.scene.{Scene, Parent}
import javafx.stage.{Modality, Stage}
import scalafx.application.Platform

class MainMenuController {

  @FXML private var playAgainstAIButton: Button = _
  @FXML private var quitGameButton: Button = _

  @FXML
  private def initialize(): Unit = {
    playAgainstAIButton.setOnAction(_ => handlePlayAgainstAI())
    quitGameButton.setOnAction(_ => handleQuitGame())
  }

  @FXML
  private def handlePlayAgainstAI(): Unit = {
    // Load the color selection page
    val loader = new FXMLLoader(getClass.getResource("/view/ColorSelection.fxml"))
    val colorSelectionPane = loader.load[VBox]()

    // Create a new stage for the color selection dialog
    val dialogStage = new Stage()
    dialogStage.initOwner(playAgainstAIButton.getScene.getWindow)
    dialogStage.initModality(Modality.WINDOW_MODAL)
    dialogStage.setScene(new Scene(colorSelectionPane))
    dialogStage.showAndWait()
  }

  @FXML
  private def handleQuitGame(): Unit = {
    // Code to quit the game
    println("Quit Game button clicked")
    Platform.exit()
  }
}