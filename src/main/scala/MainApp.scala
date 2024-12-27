package checkers.view

import javafx.fxml.FXMLLoader
import javafx.scene.layout.AnchorPane
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

object MainApp extends JFXApp3 {
  override def start(): Unit = {
    val mainMenu = getClass.getResource("/view/MainMenu.fxml")
    if (mainMenu == null) {
      throw new RuntimeException("MainMenu.fxml not found")
    }
    val loader = new FXMLLoader(mainMenu)
    val root: AnchorPane = loader.load()

    stage = new PrimaryStage() {
      title = "Main Menu"
      scene = new Scene(new scalafx.scene.layout.AnchorPane(root), 600, 400)
    }
  }
}