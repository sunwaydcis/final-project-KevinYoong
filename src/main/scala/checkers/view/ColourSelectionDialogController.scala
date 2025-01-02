package checkers.view

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.stage.Stage
import javafx.scene.image.{Image, ImageView}

class ColourSelectionDialogController {

  @FXML private var whiteButton: Button = _
  @FXML private var blackButton: Button = _
  @FXML private var cancelButton: Button = _
  @FXML private var whiteStandard : ImageView = _
  @FXML private var blackStandard : ImageView = _

  var dialogStage: Stage = null
  var okClicked:Boolean = false
  private var selectedColor: String = _

  @FXML
  private def initialize(): Unit = {
    whiteButton.setOnAction(_ => handleColorSelection("white"))
    blackButton.setOnAction(_ => handleColorSelection("black"))
    cancelButton.setOnAction(_ => handleCancel())

    whiteStandard.setImage(new javafx.scene.image.Image(getClass.getResourceAsStream("/images/whitestandard.png")))
    blackStandard.setImage(new javafx.scene.image.Image(getClass.getResourceAsStream("/images/blackstandard.png")))
  }

  private def handleColorSelection(color: String): Unit = {
    selectedColor = color
    if (color == "white") {
      whiteButton.setStyle("-fx-background-color: #ADD8E6;")
      blackButton.setStyle("")
    } else {
      blackButton.setStyle("-fx-background-color: #ADD8E6;")
      whiteButton.setStyle("")
    }
  }

  def handleOk(): Unit = {
    okClicked = true
    dialogStage.close()
  }

  def handleCancel(): Unit = {
    okClicked = false
    dialogStage.close()
  }
}