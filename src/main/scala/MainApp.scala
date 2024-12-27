package checkers

import java.util.logging.{ConsoleHandler, Level, Logger, SimpleFormatter}
import javafx.fxml.FXMLLoader
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.AnchorPane

object MainApp extends JFXApp3 {
  override def start(): Unit = {
    val mainMenu = getClass.getResource("/view/MainMenu.fxml")
    if (mainMenu == null) {
      throw new RuntimeException("MainMenu.fxml not found")
    }

    val loader = new FXMLLoader(mainMenu)
    val root: javafx.scene.layout.AnchorPane = loader.load()

    val mediaUrl = getClass.getResource("/videos/background.mp4")
    if (mediaUrl == null) throw new RuntimeException("Video file not found!")

    val media = new Media(mediaUrl.toExternalForm)
    val mediaPlayer = new MediaPlayer(media)
    val mediaView = new MediaView(mediaPlayer)
    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE) // Loop the video
    mediaPlayer.play()

    root.getChildren.add(mediaView) // Add MediaView to the root layout

    stage = new PrimaryStage() {
      title = "Main Menu"
      scene = new Scene(new AnchorPane(root), 600, 400)
    }
  }
}