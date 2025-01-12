package checkers.view

import checkers.MainApp
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.text.Text
import javafx.scene.control.Button
import javafx.stage.Stage

class VictoryDialogController {

  @FXML private var winnerText: Text = _
  @FXML private var restartGameButton: Button = _
  @FXML private var mainMenuButton: Button = _

  val winnerName = new SimpleStringProperty()
  var dialogStage: Stage =_

  @FXML
  def initialize(): Unit = {
    winnerText.textProperty().bind(winnerName)
    restartGameButton.setOnAction(_ => handleRestartGame(restartGameButton))
    mainMenuButton.setOnAction(_ => handleMainMenu(mainMenuButton))
  }

  // Sets the name of the winner
  def setWinner(playerName: String): Unit = {
    winnerName.set(s"$playerName is the winner")
  }

  // Restarts the game
  private def handleRestartGame(button: Button): Unit = {
    dialogStage.close()
    MainApp.showCheckersBoard(MainApp.isGameAI())
  }

  // Returns the user back to main menu
  private def handleMainMenu(button: Button): Unit = {
    dialogStage.close()
    MainApp.start()
  }
}