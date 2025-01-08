package checkers.util

import javafx.scene.layout.GridPane
import javafx.scene.control.Button
import javafx.scene.image.ImageView

object MoveValidator {
  def isValidMove(startRow: Int, startCol: Int, endRow: Int, endCol: Int, boardGrid: GridPane, pieceColor: String): Boolean = {
    val rowDiff = Math.abs(endRow - startRow)
    val colDiff = Math.abs(endCol - startCol)

    // Check if the move is forward
    val isForwardMove = pieceColor match {
      case "white" => endRow > startRow
      case "black" => endRow < startRow
      case _ => false
    }

    // Check if the move is diagonal and within the allowed distance
    if (isForwardMove && rowDiff == colDiff && rowDiff <= 2) {
      if (rowDiff == 1) {
        // Regular move
        true
      } else if (rowDiff == 2) {
        // Check if jumping over a piece
        val middleRow = (startRow + endRow) / 2
        val middleCol = (startCol + endCol) / 2
        val middleButtonList = boardGrid.getChildren
          .filtered(node => Option(GridPane.getRowIndex(node)).map(_.intValue()).getOrElse(0) == middleRow && Option(GridPane.getColumnIndex(node)).map(_.intValue()).getOrElse(0) == middleCol)
        if (!middleButtonList.isEmpty) {
          val middleButton = middleButtonList.get(0).asInstanceOf[Button]
          middleButton.getGraphic != null && middleButton.getGraphic.isInstanceOf[ImageView]
        } else {
          false
        }
      } else {
        false
      }
    } else {
      false
    }
  }
}