package checkers.view

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import scalafx.scene.Scene
import scalafx.scene.layout.AnchorPane

class MainMenu {

  @FXML private var mediaView: MediaView = _

  def start(): Scene = {
    val loader = new FXMLLoader(getClass.getResource("/view/MainMenu.fxml"))
    loader.setController(this)
    val root: AnchorPane = loader.load()

    val media = new Media(getClass.getResource("/videos/background.mp4").toExternalForm)
    val mediaPlayer = new MediaPlayer(media)
    mediaView.setMediaPlayer(mediaPlayer)
    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE) // Loop the video
    mediaPlayer.play()

    new Scene(root, 600, 400)
  }
}