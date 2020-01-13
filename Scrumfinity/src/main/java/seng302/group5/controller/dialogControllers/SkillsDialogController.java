package seng302.group5.controller.dialogControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import seng302.group5.Main;
import seng302.group5.controller.enums.CreateOrEdit;
import seng302.group5.model.AgileController;
import seng302.group5.model.Skill;
import seng302.group5.model.undoredo.Action;
import seng302.group5.model.undoredo.UndoRedoObject;
import seng302.group5.model.util.Settings;

/**
 * The controller for the Skill dialog when creating a new Skill or editing an existing one
 *
 * @author Liang Ma
 */
public class SkillsDialogController implements AgileController {

  @FXML private TextField skillLabel;
  @FXML private TextArea skillDescription;
  @FXML private Button skillCreation;
  @FXML private HBox btnContainer;

  private Main mainApp;
  private Stage thisStage;
  private Skill skill;
  private Skill lastSkill;
  private CreateOrEdit createOrEdit;

  /**
   * Setup the skill dialog controller
   *
   * @param mainApp      The main application object
   * @param thisStage    The stage of the dialog
   * @param createOrEdit If dialog is for creating or editing a skill
   * @param skill        The skill object if editing, null otherwise
   */
  public void setupController(Main mainApp,
                              Stage thisStage,
                              CreateOrEdit createOrEdit,
                              Skill skill) {
    this.mainApp = mainApp;
    this.thisStage = thisStage;

    String os = System.getProperty("os.name");

    if (!os.startsWith("Windows")) {
      btnContainer.getChildren().remove(skillCreation);
      btnContainer.getChildren().add(skillCreation);
    }

    if (createOrEdit == CreateOrEdit.CREATE) {
      thisStage.setTitle("Create New Skill");
      skillCreation.setText("Create");
    } else if (createOrEdit == CreateOrEdit.EDIT) {
      thisStage.setTitle("Edit Skill");
      skillCreation.setText("Save");
      skillCreation.setDisable(true);

      skillLabel.setText(skill.getLabel());
      skillDescription.setText(skill.getSkillDescription());
    }
    this.createOrEdit = createOrEdit;

    if (skill != null) {
      this.skill = skill;
      // Make a copy for the undo stack
      this.lastSkill = new Skill(skill);
    } else {
      this.skill = null;
      this.lastSkill = null;
    }

    skillCreation.setDefaultButton(true);
    thisStage.setResizable(false);

    thisStage.setOnCloseRequest(event -> {
      mainApp.popControllerStack();
    });

    // Handle TextField text changes.
    skillLabel.textProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if(createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
      if (newValue.trim().length() > 20) {
        skillLabel.setStyle("-fx-text-inner-color: red;");
      } else {
        skillLabel.setStyle("-fx-text-inner-color: black;");
      }
    });

    skillDescription.textProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if(createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });
    thisStage.getIcons().add(new Image("Thumbnail.png"));
  }

  /**
   * checks if there are any changed fields and disables or enables the button accordingly
   */
  private void checkButtonDisabled() {
    if (skillLabel.getText().equals(skill.getLabel()) &&
        skillDescription.getText().equals(skill.getSkillDescription())) {
      skillCreation.setDisable(true);
    } else {
      skillCreation.setDisable(false);
    }
  }

  /**
   * Parse a string containing a skill label. Throws exceptions if input is not valid.
   *
   * @param inputSkillLabel String of skill label
   * @return Input project label if it is valid.
   * @throws Exception Exception with message explaining why input is invalid.
   */
  private String parseSkillLabel(String inputSkillLabel) throws Exception {
    inputSkillLabel = inputSkillLabel.trim();

    if (inputSkillLabel.isEmpty()) {
      throw new Exception("Skill label is empty.");
    } else {
      String lastSkillLabel;
      if (lastSkill == null) {
        lastSkillLabel = "";
      } else {
        lastSkillLabel = lastSkill.getLabel();
      }
      for (Skill aSkill : mainApp.getSkills()) {
        String aSkillLabel = aSkill.getLabel();
        if (aSkillLabel.equalsIgnoreCase(inputSkillLabel) &&
            !aSkillLabel.equalsIgnoreCase(lastSkillLabel)) {
          throw new Exception("Skill label is not unique.");
        }
      }
    }
    return inputSkillLabel;
  }

  /**
   * Generate an UndoRedoObject to place in the stack
   *
   * @return the UndoRedoObject to store
   */
  private UndoRedoObject generateUndoRedoObject() {
    UndoRedoObject undoRedoObject = new UndoRedoObject();

    if (createOrEdit == CreateOrEdit.CREATE) {
      undoRedoObject.setAction(Action.SKILL_CREATE);
    } else {
      undoRedoObject.setAction(Action.SKILL_EDIT);
      undoRedoObject.addDatum(lastSkill);
    }

    // Store a copy of skill to edit in stack to avoid reference problems
    undoRedoObject.setAgileItem(skill);
    Skill skillToStore = new Skill(skill);
    undoRedoObject.addDatum(skillToStore);

    return undoRedoObject;
  }

  /**
   * Handles when the create button is pushed
   *
   * @param e Event generated by event listener.
   */
  @FXML
  protected void SkillCreation(ActionEvent e) {
    String labelOfSkill = null;
    try {
      labelOfSkill = parseSkillLabel(skillLabel.getText());
    } catch (Exception e1) {
      // Error - Don't create the object
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Invalid field");
      alert.setHeaderText(null);
      alert.setContentText(e1.getMessage());
      alert.showAndWait();
      return;
    }
    if (createOrEdit == CreateOrEdit.CREATE) {
      skill = new Skill(labelOfSkill, skillDescription.getText());
      mainApp.addSkill(skill);
      if (Settings.correctList(skill)) {
        mainApp.refreshList(skill);
      }
    } else if (createOrEdit == CreateOrEdit.EDIT) {
      skill.setLabel(labelOfSkill);
      skill.setSkillDescription(skillDescription.getText());
      if (Settings.correctList(skill)) {
        mainApp.refreshList(skill);
      }
    }
    UndoRedoObject undoRedoObject = generateUndoRedoObject();
    mainApp.newAction(undoRedoObject);
    mainApp.popControllerStack();
    thisStage.close();
  }

  /**
   * Handles when the cancel button is pushed by not applying changes and closing the dialog
   *
   * @param event Event generated by event listener.
   */
  @FXML
  protected void CancelCreation(ActionEvent event) {
    mainApp.popControllerStack();
    thisStage.close();
  }

  /**
   * Returns the label of the backlog if a backlog is being edited.
   *
   * @return The label of the backlog as a string.
   */
  public String getLabel() {
    if (createOrEdit == CreateOrEdit.EDIT) {
      return skill.getLabel();
    }
    return "";
  }

}

