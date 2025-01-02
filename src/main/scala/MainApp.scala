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
    control.okClicked
  }
}