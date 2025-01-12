package checkers

import checkers.view.VictoryDialogController
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.stage.Modality.ApplicationModal
import scalafx.stage.{Modality, Stage}

object MainApp extends JFXApp3 {

  private var selectedColor: String = _
  private var isAI: Boolean = true

  override def start(): Unit = {
    // Load MainMenu.fxml
    val resource = getClass.getResource("/view/MainMenu.fxml")
    if (resource == null) {
      throw new RuntimeException("MainMenu.fxml not found")
    }
    val loader = new FXMLLoader(resource)
    loader.load()
    val roots = loader.getRoot[javafx.scene.layout.StackPane]

    // Set up the primary stage
    stage = new PrimaryStage() {
      title = "Main Menu"
      scene = new Scene(roots)
    }
  }

  def showColourSelectionDialog(): Boolean = {
    val resource = getClass.getResource("/view/ColourSelectionDialog.fxml")
    if (resource == null) {
      throw new RuntimeException("ColourSelectionDialog.fxml not found")
    }
    val loader = new FXMLLoader(resource)
    loader.load()
    val roots2 = loader.getRoot[Parent]
    val control = loader.getController[checkers.view.ColourSelectionDialogController]

    val dialog = new Stage() {
      initModality(ApplicationModal)
      initOwner(stage)
      scene = new Scene() {
        root = roots2
      }
    }

    control.dialogStage = dialog
    dialog.showAndWait()

    // Check if the user clicked OK and update the selectedColor
    if (control.okClicked) {
      selectedColor = control.getSelectedColor() // Assign the selected color from the controller
    }
    control.okClicked
  }

  def getSelectedColor(): String = {
    selectedColor
  }

  def showCheckersBoard(isAI: Boolean = true): Unit = {
    this.isAI = isAI
    val resource = getClass.getResource("/view/CheckersBoard.fxml")
    if (resource == null) {
      throw new RuntimeException("CheckersBoard.fxml not found")
    }
    val loader = new FXMLLoader(resource)
    val root3 = loader.load[Parent]() // Load the FXML file and get the root node
    val controller = loader.getController[checkers.view.CheckersBoardController]
    controller.initializeGame(isAI)
    stage.setScene(new Scene(root3))
    stage.setTitle("Checkers Game")
    stage.show()
  }

  def isGameAI(): Boolean = {
    isAI
  }

  def showVictoryDialog(winnerName: String, owner: Stage): Unit = {
    val resource = getClass.getResource("/view/VictoryDialog.fxml")
    if (resource == null) {
      throw new RuntimeException("VictoryDialog.fxml not found")
    }
    val loader = new FXMLLoader(resource)
    val root = loader.load[Parent]()
    val controller = loader.getController[VictoryDialogController]
    controller.setWinner(winnerName)
    val dialog = new Stage()
    dialog.initOwner(owner)
    controller.dialogStage = dialog
    dialog.setScene(new Scene(root))
    dialog.showAndWait()
  }
}