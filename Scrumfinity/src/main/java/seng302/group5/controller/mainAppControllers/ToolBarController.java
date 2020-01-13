package seng302.group5.controller.mainAppControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import seng302.group5.Main;
import seng302.group5.model.undoredo.UndoRedoHandler;

/**
 * Controller that handles the Tool bar.
 * Created by Zander on 12/08/2015.
 */
public class ToolBarController {

  private Main mainApp;

  @FXML private Button saveButton;
  @FXML private Button saveAsButton;
  @FXML private Button loadButton;
  @FXML private Button undoButton;
  @FXML private Button redoButton;
  @FXML private Button editButton;
  @FXML private Button deleteButton;
  @FXML private Button dependenciesButton;

  public void setupController() {
    undoButton.setDisable(true);
    redoButton.setDisable(true);
  }

  public void checkUndoRedoToolbarButtons(UndoRedoHandler undoRedoHandler) {
    // undo menu item
    if (undoRedoHandler.peekUndoStack() == null) {
      undoButton.setDisable(true);
    } else {
      undoButton.setDisable(false);
    }

    // redo menu item
    if (undoRedoHandler.peekRedoStack() == null) {
      redoButton.setDisable(true);
    } else {
      redoButton.setDisable(false);
    }
  }

  /**
   * Handles the save button click event.
   */
  @FXML
  protected void btnSaveClick() {
    mainApp.getMBC().btnClickSave(new ActionEvent());
  }

  /**
   * Handles the save as button click event.
   */
  @FXML
  protected void btnSaveAsClick() {
    mainApp.getMBC().btnClickSaveAs(new ActionEvent());
  }

  /**
   * Handles the load button click event.
   */
  @FXML
  protected void btnLoadClick() {
    mainApp.getMBC().btnClickOpen(new ActionEvent());
  }

  /**
   * Handles the undo button click event.
   */
  @FXML
  protected void btnUndoClick() {
    mainApp.undo();
  }

  /**
   * Handles the redo button click event.
   */
  @FXML
  protected void btnRedoClick() {
    mainApp.redo();
  }

  /**
   * Handles the edit button click event.
   */
  @FXML
  protected void btnEditClick() {
    mainApp.getMBC().editItem(new ActionEvent());
  }

  /**
   * Handles the delete button click event.
   */
  @FXML
  protected void btnDeleteClick() {
    mainApp.getMBC().btnDelete();
  }

  /**
   * Handles the dependencies button click event.
   */
  @FXML
  protected void btnDependenciesClick() {
    mainApp.getMBC().launchDependantsDialog(new ActionEvent());
  }

  /**
   * Sets the main app to the given one.
   *
   * @param mainApp The main application object.
   */
  public void setMainApp(Main mainApp) {
    this.mainApp = mainApp;
  }
}
