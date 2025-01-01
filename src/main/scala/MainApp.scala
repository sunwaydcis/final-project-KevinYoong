package checkers

import javafx.fxml.FXMLLoader
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, StackPane}
import scalafx.Includes.*
import scalafx.scene.media.MediaView

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
}