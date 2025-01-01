package checkers.view

import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.Button
import javafx.scene.layout.{VBox, BorderPane, StackPane, GridPane}
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import javafx.scene.{Scene, Parent}
import javafx.stage.{Modality, Stage}
import scalafx.application.Platform

class MainMenuController {

  @FXML private var playAgainstAIButton: Button = _
  @FXML private var quitGameButton: Button = _
  @FXML private var mediaView: MediaView = _

  @FXML
  def initialize(): Unit = {
    playAgainstAIButton.setOnAction(_ => handlePlayAgainstAI())
    quitGameButton.setOnAction(_ => handleQuitGame())
    setupMediaPlayer()
  }

  private def setupMediaPlayer(): Unit = {
    if (mediaView != null) {
      try {
        // Bind MediaView size to its parent StackPane
        mediaView.fitWidthProperty().bind(mediaView.getParent.asInstanceOf[StackPane].widthProperty())
        mediaView.fitHeightProperty().bind(mediaView.getParent.asInstanceOf[StackPane].heightProperty())

        // Load and play background video
        val mediaUrl = getClass.getResource("/videos/background.mp4")
        if (mediaUrl == null) throw new RuntimeException("Video file not found!")

        val media = new Media(mediaUrl.toExternalForm)
        val mediaPlayer = new MediaPlayer(media)
        mediaView.setMediaPlayer(mediaPlayer)
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE) // Loop the video
        mediaPlayer.play()
      } catch {
        case e: Exception => println(s"Error setting up media player: ${e.getMessage}")
      }
    }
  }

  @FXML
  def handlePlayAgainstAI(): Unit = {
    // Load the color selection page
    val loader = new FXMLLoader(getClass.getResource("/view/ColorSelection.fxml"))
    val colorSelectionPane = loader.load[VBox]()

    // Create a new stage for the color selection dialog
    val dialogStage = new Stage()
    dialogStage.initOwner(playAgainstAIButton.getScene.getWindow)
    dialogStage.initModality(Modality.WINDOW_MODAL)
    dialogStage.setScene(new Scene(colorSelectionPane))
    dialogStage.showAndWait()
  }

  @FXML
  def handleQuitGame(): Unit = {
    println("Quit Game button clicked")
    Platform.exit()
  }
}