package checkers

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
    val root = loader.load[Parent]() // Load the FXML file and get the root node
    val controller = loader.getController[checkers.view.CheckersBoardController]
    controller.initializeGame(isAI)
    stage.setScene(new Scene(root))
    stage.setTitle("Checkers Game")
    stage.show()
  }

  def showVictoryDialog(winnerName: String): Unit = {
      // Load VictoryDialog.fxml
      val resource = getClass.getResource("/view/VictoryDialog.fxml")
      if (resource == null) {
        throw new RuntimeException("VictoryDialog.fxml not found")
      }
      val loader = new FXMLLoader(resource)
      val root = loader.load[Parent]()
      val controller = loader.getController[checkers.view.VictoryDialogController]
      controller.setWinner(winnerName)
      // Set up the dialog stage
      val dialogStage = new Stage() {
        title = "Victory Dialog"
        initModality(Modality.ApplicationModal)
        scene = new Scene(root)
      }
      controller.dialogStage = dialogStage
      dialogStage.showAndWait()
    }

  def isGameAI(): Boolean = {
    isAI
  }
}