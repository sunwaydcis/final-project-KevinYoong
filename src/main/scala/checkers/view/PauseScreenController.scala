package checkers.view

import javafx.fxml.FXML
import javafx.stage.Stage
import checkers.MainApp
import javafx.scene.control.Button

class PauseScreenController {
  @FXML private var resumeGameButton: Button = _
  @FXML private var restartGameButton: Button = _
  @FXML private var exitGameButton: Button = _
  var dialogStage: Stage = _

  @FXML
  private def initialize(): Unit = {
    resumeGameButton.setOnAction(_ => handleResume())
    restartGameButton.setOnAction(_ => handleRestart())
    exitGameButton.setOnAction(_ => handleQuit())
  }
  
  // Resumes game 
  @FXML
  private def handleResume(): Unit = {
    dialogStage.close()
  }

  // Restarts the game 
  // Pieces are back in their initial positions
  @FXML
  private def handleRestart(): Unit = {
    dialogStage.close()
    MainApp.showCheckersBoard(MainApp.isGameAI())
  }

  // Sends the user back to main menu
  @FXML
  private def handleQuit(): Unit = {
    dialogStage.close()
    MainApp.start()
  }
}