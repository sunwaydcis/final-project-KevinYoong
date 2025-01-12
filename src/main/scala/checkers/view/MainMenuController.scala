package checkers.view

import checkers.MainApp
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import scalafx.application.Platform

class MainMenuController {

  @FXML private var playAgainstAIButton: Button = _
  @FXML private var playAgainstFriendButton: Button = _
  @FXML private var quitGameButton: Button = _
  @FXML private var mediaView: MediaView = _

  @FXML
  def initialize(): Unit = {
    playAgainstAIButton.setOnAction(_ => handlePlayAgainstAI())
    playAgainstFriendButton.setOnAction(_ => handlePlayAgainstFriend())
    quitGameButton.setOnAction(_ => handleQuitGame())
    setupMediaPlayer()
  }

  // Reference for setting up mediaView
  // Inspired by ChatGPT and GitHub Copilot
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

  // User will play against AI
  @FXML
  private def handlePlayAgainstAI(): Unit = {
    val okClicked = MainApp.showColourSelectionDialog()
    if (okClicked) {
      // Logic to move to the game scene
      val selectedColor = MainApp.getSelectedColor()
      println(s"Color selected: $selectedColor, moving to game scene")
      MainApp.showCheckersBoard()
    } else {
      // Logic to stay in the main menu
      println("Color selection canceled, staying in main menu")
    }
  }

  // User will play against another player
  @FXML
  private def handlePlayAgainstFriend(): Unit = {
    val okClicked = MainApp.showColourSelectionDialog()
    if (okClicked) {
      // Logic to move to the game scene without AI
      val selectedColor = MainApp.getSelectedColor()
      println(s"Color selected: $selectedColor, moving to game scene for two players")
      MainApp.showCheckersBoard(isAI = false)
    } else {
      // Logic to stay in the main menu
      println("Color selection canceled, staying in main menu")
    }
  }

  // Close the game
  @FXML
  private def handleQuitGame(): Unit = {
    println("Quit Game button clicked")
    Platform.exit()
  }
}