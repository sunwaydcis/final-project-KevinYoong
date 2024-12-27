package checkers

import java.util.logging.{ConsoleHandler, Level, Logger, SimpleFormatter}
import javafx.fxml.FXMLLoader
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.AnchorPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

object MainApp extends JFXApp3 {
  override def start(): Unit = {
    val mainMenu = getClass.getResource("/view/MainMenu.fxml")
    if (mainMenu == null) {
      throw new RuntimeException("MainMenu.fxml not found")
    }

    val loader = new FXMLLoader(mainMenu)
    val root: javafx.scene.layout.AnchorPane = loader.load()
    val mediaView: MediaView = loader.getNamespace.get("mediaView").asInstanceOf[MediaView]

    val mediaUrl = getClass.getResource("/videos/background.mp4")
    if (mediaUrl == null) throw new RuntimeException("Video file not found!")

    val media = new Media(mediaUrl.toExternalForm)
    val mediaPlayer = new MediaPlayer(media)
    mediaView.setMediaPlayer(mediaPlayer)
    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE) // Loop the video
    mediaPlayer.play()

    // Bind MediaView size to AnchorPane size
    mediaView.fitWidthProperty().bind(root.widthProperty())
    mediaView.fitHeightProperty().bind(root.heightProperty())

    // Create a semi-transparent overlay
    val overlay = new Rectangle {
      width <== root.widthProperty()
      height <== root.heightProperty()
      fill = Color.rgb(0, 0, 0, 0.5)
    }
    root.getChildren.add(overlay) // Add overlay to the root layout

    stage = new PrimaryStage() {
      title = "Main Menu"
      scene = new Scene(new AnchorPane(root), 600, 400)
    }
  }
}