package checkers.view

import checkers.MainApp
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.Button
import javafx.scene.layout.{BorderPane, GridPane, StackPane, VBox}
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import javafx.scene.{Parent, Scene}
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
    val okClicked = MainApp.showColourSelectionDialog()
    if (okClicked) {
      // Logic to move to the game scene
      println("Color selected, moving to game scene")
    } else {
      // Logic to stay in the main menu
      println("Color selection canceled, staying in main menu")
    }
  }

  @FXML
  def handleQuitGame(): Unit = {
    println("Quit Game button clicked")
    Platform.exit()
  }
}