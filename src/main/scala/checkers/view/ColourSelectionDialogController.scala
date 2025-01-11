package checkers.view

import javafx.fxml.FXML
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.{Alert, Button}
import javafx.stage.Stage
import javafx.scene.image.{Image, ImageView}
import javafx.fxml.FXMLLoader
import javafx.scene.Parent

class ColourSelectionDialogController {

  @FXML private var whiteButton: Button = _
  @FXML private var blackButton: Button = _
  @FXML private var cancelButton: Button = _
  @FXML private var whiteStandard : ImageView = _
  @FXML private var blackStandard : ImageView = _

  var dialogStage: Stage = _
  var okClicked:Boolean = false
  var selectedColor: String = _

  @FXML
  private def initialize(): Unit = {
    whiteButton.setOnAction(_ => handleColorSelection(whiteButton))
    blackButton.setOnAction(_ => handleColorSelection(blackButton))
    cancelButton.setOnAction(_ => handleCancel())

    whiteStandard.setImage(new javafx.scene.image.Image(getClass.getResourceAsStream("/images/white_standard.png")))
    blackStandard.setImage(new javafx.scene.image.Image(getClass.getResourceAsStream("/images/black_standard.png")))
  }

  private def handleColorSelection(button: Button): Unit = {
    if (button == whiteButton) {
      selectedColor = "White"
      whiteButton.setStyle("-fx-background-color: #ADD8E6;")
      blackButton.setStyle("")
    } else if (button == blackButton) {
      selectedColor = "Black"
      blackButton.setStyle("-fx-background-color: #ADD8E6;")
      whiteButton.setStyle("")
    }
  }

  def handleOk(): Unit = {
    if (selectedColor == null) {
      val alert = new Alert(AlertType.ERROR)
      alert.initOwner(dialogStage)
      alert.setTitle("No Color Selected")
      alert.setContentText("Please select a color before pressing OK.")
      alert.showAndWait()
    } else {
      okClicked = true
      dialogStage.close()
    }
  }

  def handleCancel(): Unit = {
    okClicked = false
    dialogStage.close()
  }

  def getSelectedColor(): String = {
    selectedColor
  }
}