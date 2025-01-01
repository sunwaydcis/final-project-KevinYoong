package checkers

import javafx.fxml.FXMLLoader
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, GridPane, StackPane}

object MainApp extends JFXApp3 {
  override def start(): Unit = {
    // Load RootLayout.fxml
    val rootLayoutUrl = getClass.getResource("/view/RootLayout.fxml")
    if (rootLayoutUrl == null) {
      throw new RuntimeException("RootLayout.fxml not found")
    }

    val rootLoader = new FXMLLoader(rootLayoutUrl)
    val root: javafx.scene.layout.BorderPane = rootLoader.load()
    val mediaView: MediaView = rootLoader.getNamespace.get("mediaView").asInstanceOf[MediaView]
    val mainMenuContainer: javafx.scene.layout.GridPane = rootLoader.getNamespace.get("mainMenuContainer").asInstanceOf[javafx.scene.layout.GridPane]

    // Bind MediaView size to StackPane size
    val stackPane: javafx.scene.layout.StackPane = mediaView.getParent.asInstanceOf[javafx.scene.layout.StackPane]
    mediaView.fitWidthProperty().bind(stackPane.widthProperty())
    mediaView.fitHeightProperty().bind(stackPane.heightProperty())

    // Load and play background video
    val mediaUrl = getClass.getResource("/videos/background.mp4")
    if (mediaUrl == null) throw new RuntimeException("Video file not found!")

    val media = new Media(mediaUrl.toExternalForm)
    val mediaPlayer = new MediaPlayer(media)
    mediaView.setMediaPlayer(mediaPlayer)
    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE) // Loop the video
    mediaPlayer.play()

    // Load MainMenu.fxml and add it to the mainMenuContainer
    val mainMenuUrl = getClass.getResource("/view/MainMenu.fxml")
    if (mainMenuUrl == null) {
      throw new RuntimeException("MainMenu.fxml not found")
    }

    val mainMenuLoader = new FXMLLoader(mainMenuUrl)
    val mainMenu: javafx.scene.layout.GridPane = mainMenuLoader.load()
    mainMenuContainer.getChildren.add(mainMenu)

    // Set up the primary stage
    stage = new PrimaryStage() {
      title = "Main Menu"
      scene = new Scene(new BorderPane(root), 600, 400)
    }
  }
}