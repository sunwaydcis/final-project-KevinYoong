package checkers.view

import javafx.fxml.FXMLLoader
import scalafx.scene.Scene
import scalafx.scene.layout.AnchorPane

class MainMenu {
  def start(): Scene = {
    val loader = new FXMLLoader(getClass.getResource("/checkers/view/MainMenu.fxml"))
    val root: AnchorPane = loader.load()
    new Scene(root, 600, 400)
  }
}