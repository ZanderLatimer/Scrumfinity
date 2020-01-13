package seng302.group5.controller.dialogControllers;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import seng302.group5.Main;
import seng302.group5.controller.enums.CreateOrEdit;
import seng302.group5.controller.enums.DialogMode;
import seng302.group5.model.AgileController;
import seng302.group5.model.Backlog;
import seng302.group5.model.Person;
import seng302.group5.model.Role;
import seng302.group5.model.Skill;
import seng302.group5.model.Team;
import seng302.group5.model.undoredo.Action;
import seng302.group5.model.undoredo.UndoRedoObject;
import seng302.group5.model.util.Settings;

/**
 * The controller for the person dialog when creating a new person or editing an existing one.
 *
 * Created by Zander on 18/03/2015.
 */
public class PersonDialogController implements AgileController {

  @FXML private TextField personLabelField;
  @FXML private TextField personFirstNameField;
  @FXML private TextField personLastNameField;
  @FXML private Button btnCreatePerson;
  @FXML private Button btnCancel;
  @FXML private ListView<Skill> skillsList;
  @FXML private ListView<Skill> personSkillList;
  @FXML private HBox btnContainer;
  @FXML private Button btnNewSkill;

  private Main mainApp;
  private Stage thisStage;
  private CreateOrEdit createOrEdit;
  private DialogMode dialogMode;
  private Person person;
  private Person lastPerson;
  private Skill poSkill;        // the product owner skill
  private Skill smSkill;
  private boolean ownsBacklog;  // person owns a backlog

  private Boolean isSM = false;

  private ObservableList<Skill> availableSkills = FXCollections.observableArrayList();
  private ObservableList<Skill> selectedSkills = FXCollections.observableArrayList();


  /**
   * Setup the person dialog controller
   *
   * @param mainApp      The main application object
   * @param thisStage    The stage of the dialog
   * @param createOrEdit If dialog is for creating or editing a person
   * @param person       The person object if editing, null otherwise
   */
  public void setupController(Main mainApp,
                              Stage thisStage,
                              CreateOrEdit createOrEdit,
                              Person person) {
    this.mainApp = mainApp;
    this.thisStage = thisStage;
    this.dialogMode = DialogMode.DEFAULT_MODE;

    String os = System.getProperty("os.name");

    if (!os.startsWith("Windows")) {
      btnContainer.getChildren().remove(btnCreatePerson);
      btnContainer.getChildren().add(btnCreatePerson);
    }

    if (createOrEdit == CreateOrEdit.CREATE) {
      thisStage.setTitle("Create New Person");
      btnCreatePerson.setText("Create");

      initialiseLists();
    } else if (createOrEdit == CreateOrEdit.EDIT) {
      thisStage.setTitle("Edit Person");
      btnCreatePerson.setText("Save");

      personLabelField.setText(person.getLabel());
      personFirstNameField.setText(person.getFirstName());
      personLastNameField.setText(person.getLastName());
      selectedSkills = FXCollections.observableArrayList(person.getSkillSet());
      personSkillList.setItems(selectedSkills);
      initialiseLists();
      btnCreatePerson.setDisable(true);
    }
    this.createOrEdit = createOrEdit;

    if (person != null) {
      this.person = person;
      this.lastPerson = new Person(person);
    } else {
      this.person = null;
      this.lastPerson = null;
    }

    btnCreatePerson.setDefaultButton(true);
    thisStage.setResizable(false);

    poSkill = null;
    for (Role role : mainApp.getRoles()) {
      if (role.getLabel().equals("PO")) {
        poSkill = role.getRequiredSkill();
        break;
      }
    }

    smSkill = null;
    for (Role role : mainApp.getRoles()) {
      if (role.getLabel().equals("SM")) {
        smSkill = role.getRequiredSkill();
        break;
      }
    }

    ownsBacklog = false;
    if (createOrEdit == CreateOrEdit.EDIT) {
      for (Backlog backlog : mainApp.getBacklogs()) {
        if (backlog.getProductOwner().equals(person)) {
          ownsBacklog = true;
          break;
        }
      }
    }

    thisStage.setOnCloseRequest(event -> {
      mainApp.popControllerStack();
    });

    // Handle TextField text changes.
    personLabelField.textProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if(createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
      if (newValue.trim().length() > 20) {
        personLabelField.setStyle("-fx-text-inner-color: red;");
      } else {
        personLabelField.setStyle("-fx-text-inner-color: black;");
      }
    });

    personFirstNameField.textProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if(createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });

    personLastNameField.textProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if(createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });

    skillsList.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          //For disabling the button
          if (createOrEdit == CreateOrEdit.EDIT) {
            checkButtonDisabled();
          }
        });

    personSkillList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if(createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });
    thisStage.getIcons().add(new Image("Thumbnail.png"));
  }

  /**
   * Set up the dialog to be in backlog mode.
   */
  public void setupBacklogMode() {
    dialogMode = DialogMode.BACKLOG_MODE;
    if (createOrEdit == CreateOrEdit.CREATE) {
      this.selectedSkills.add(poSkill);
      this.availableSkills.remove(poSkill);
    }
  }

  /**
   * Set up the dialog to be in backlog mode.
   */
  public void setupTeamMode(Role role) {
    dialogMode = DialogMode.TEAM_MODE;
    if (role != null) {
      if (role.getLabel().equals("SM")) {
        isSM = true;
      } else {
        isSM = false;
      }
    }
  }

  /**
   * checks if there are any changed fields and disables or enables the button accordingly
   */
  private void checkButtonDisabled() {
    if (personLabelField.getText().equals(person.getLabel()) &&
        personFirstNameField.getText().equals(person.getFirstName()) &&
        personLastNameField.getText().equals(person.getLastName()) &&
        personSkillList.getItems().equals(person.getSkillSet())) {
      btnCreatePerson.setDisable(true);
    } else {
      btnCreatePerson.setDisable(false);
    }
  }

  /**
   * Generate an UndoRedoObject to place in the stack
   *
   * @return the UndoRedoObject to store
   */
  private UndoRedoObject generateUndoRedoObject() {
    UndoRedoObject undoRedoObject = new UndoRedoObject();

    if (createOrEdit == CreateOrEdit.CREATE) {
      undoRedoObject.setAction(Action.PERSON_CREATE);
    } else {
      undoRedoObject.setAction(Action.PERSON_EDIT);
      undoRedoObject.addDatum(lastPerson);
    }

    // Store a copy of person to edit in stack to avoid reference problems
    undoRedoObject.setAgileItem(person);
    Person personToStore = new Person(person);
    undoRedoObject.addDatum(personToStore);

    return undoRedoObject;
  }

  /**
   * Creates a new Person from the textfield data on click of 'Create' button.
   *
   * @param event Event generated by event listener.
   */
  @FXML
  protected void btnCreatePersonClick(ActionEvent event) {
    StringBuilder errors = new StringBuilder();
    int noErrors = 0;

    String personLabel = "";
    String personFirstName = personFirstNameField.getText().trim();
    String personLastName = personLastNameField.getText().trim();
    ObservableList<Skill> personSkillSet =
        FXCollections.observableArrayList(personSkillList.getItems());

    try {
      personLabel = parsePersonLabel(personLabelField.getText());
    } catch (Exception e) {
      noErrors++;
      errors.append(String.format("%s\n", e.getMessage()));
    }

    // Display all errors if they exist
    if (noErrors > 0) {
      String title = String.format("%d invalid field", noErrors);
      if (noErrors > 1) {
        title += "s";  // plural
      }
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle(title);
      alert.setHeaderText(null);
      alert.setContentText(errors.toString());
      alert.showAndWait();
    } else {
      if (createOrEdit == CreateOrEdit.CREATE) {
        person = new Person(personLabel, personFirstName, personLastName, personSkillSet);
        mainApp.addPerson(person);
        if (Settings.correctList(person)) {
          mainApp.refreshList(person);
        }
      } else if (createOrEdit == CreateOrEdit.EDIT) {
        person.setLabel(personLabel);
        person.setFirstName(personFirstName);
        person.setLastName(personLastName);
        person.setSkillSet(personSkillSet);
        if (Settings.correctList(person)) {
          mainApp.refreshList(person);
        }
      }
      UndoRedoObject undoRedoObject = generateUndoRedoObject();
      mainApp.newAction(undoRedoObject);
      mainApp.popControllerStack();
      thisStage.close();
    }
  }

  /**
   * Closes the CreatePerson dialog box in click of 'Cancel' button.
   *
   * @param event Event generated by event listener.
   */
  @FXML
  protected void btnCancelClick(ActionEvent event) {
    if (createOrEdit == CreateOrEdit.EDIT && Settings.correctList(person)) {
      mainApp.refreshList(person);
    }
    mainApp.popControllerStack();
    thisStage.close();
  }

  /**
   * Checks that the Person label entry box contains valid input.
   *
   * @param inputPersonLabel Person label from entry field.
   * @return Person label if label is valid.
   * @throws Exception Any invalid input.
   */
  private String parsePersonLabel(String inputPersonLabel) throws Exception {
    inputPersonLabel = inputPersonLabel.trim();

    if (inputPersonLabel.isEmpty()) {
      throw new Exception("Person label is empty.");
    } else {
      String lastPersonLabel;
      if (lastPerson == null) {
        lastPersonLabel = "";
      } else {
        lastPersonLabel = lastPerson.getLabel();
      }
      for (Person personInList : mainApp.getPeople()) {
        String personLabel = personInList.getLabel();
        if (personLabel.equalsIgnoreCase(inputPersonLabel) &&
            !personLabel.equalsIgnoreCase(lastPersonLabel)) {
          throw new Exception("Person label is not unique.");
        }
      }
    }
    return inputPersonLabel;
  }

  /**
   * Populates a list of available skills for assigning them to people
   */
  private void initialiseLists() {
    try {

      // loop for adding the skills that you can assign to someone.
      for (Skill item : mainApp.getSkills()) {
        if (!selectedSkills.contains(item)) {
          availableSkills.add(item);
        }
      }

      this.skillsList.setItems(availableSkills.sorted(Comparator.<Skill>naturalOrder()));
      this.personSkillList.setItems(selectedSkills.sorted(Comparator.<Skill>naturalOrder()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    setupListView();
  }

  /**
   * Adds in a skill to the person once add is click and a skill is selected.
   *
   * @param event Event generated by event listener.
   */
  @FXML
  protected void btnAddSkillClick(ActionEvent event) {
    try {
      Skill selectedSkill = skillsList.getSelectionModel().getSelectedItem();
      if (selectedSkill != null) {
        this.selectedSkills.add(selectedSkill);
        this.availableSkills.remove(selectedSkill);

        this.skillsList.getSelectionModel().clearSelection();

        if (createOrEdit == CreateOrEdit.EDIT) {
          checkButtonDisabled();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * removes the selected skill from the person.
   *
   * @param event Event generated by event listener.
   */
  @FXML
  protected void btnRemoveSkillClick(ActionEvent event) {
    try {
      Skill selectedSkill = personSkillList.getSelectionModel().getSelectedItem();

      if (selectedSkill != null) {
        // check person (if exists) is in a team with a non-null role
        if (person != null && person.getTeam() != null &&
            person.getTeam().getMembersRole().get(person) != null) {
          Team team = person.getTeam();
          Role role = team.getMembersRole().get(person);
          Skill roleSkill = role.getRequiredSkill();
          if (selectedSkill.equals(roleSkill)) {
            // selected skill is required by the person's current role in their team
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot remove skill");
            alert.setHeaderText(null);
            alert.setContentText(String.format(
                "%s already has the role %s in the team %s which requires a member have the skill %s.",
                person, role, team, roleSkill));
            alert.getDialogPane().setPrefHeight(150);
            alert.showAndWait();
            return;
          }
        }
        if (dialogMode == DialogMode.BACKLOG_MODE || ownsBacklog) {
          if (selectedSkill.equals(poSkill)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot remove product owner skill");
            alert.setHeaderText(null);
            alert.setContentText("This person is or will be the product owner of a backlog.");
            alert.showAndWait();
            return;
          }
        } else if (DialogMode.TEAM_MODE.equals(dialogMode) && isSM) {
          if (selectedSkill.equals(smSkill)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot Remove Scrum Master Skill");
            alert.setHeaderText(null);
            alert.setContentText("This person is or will be a scrum master for a team.");
            alert.showAndWait();
            return;
          }
        }
        this.availableSkills.add(selectedSkill);
        this.selectedSkills.remove(selectedSkill);

        if (createOrEdit == CreateOrEdit.EDIT) {
          checkButtonDisabled();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets the custom behaviour for the skills ListView.
   */
  private void setupListView() {
    //Sets the cell being populated with custom settings defined in the ListViewCell class.
    this.skillsList.setCellFactory(listView -> new AvailableSkillsListViewCell());
    this.personSkillList.setCellFactory(listView -> new PersonSkillsListViewCell());
  }

  /**
   * Returns the label of the backlog if a backlog is being edited.
   *
   * @return The label of the backlog as a string.
   */
  public String getLabel() {
    if (createOrEdit == CreateOrEdit.EDIT) {
      return person.getLabel();
    }
    return "";
  }

  /**
   * Allows us to override a ListViewCell - a single cell in a ListView.
   */
  private class AvailableSkillsListViewCell extends TextFieldListCell<Skill> {

    public AvailableSkillsListViewCell() {
      super();

      // double click for editing
      this.setOnMouseClicked(click -> {
        if (click.getClickCount() == 2 &&
            click.getButton() == MouseButton.PRIMARY &&
            !isEmpty()) {
          Skill selectedSkill = skillsList.getSelectionModel().getSelectedItem();
          mainApp.showSkillDialogWithinPerson(selectedSkill, thisStage);
          availableSkills.remove(selectedSkill);
          availableSkills.add(selectedSkill);
          skillsList.getSelectionModel().select(selectedSkill);
        }
      });
    }

    @Override
    public void updateItem(Skill item, boolean empty) {
      // calling super here is very important - don't skip this!
      super.updateItem(item, empty);

      setText(item == null ? "" : item.getLabel());
    }
  }

  /**
   * Allows us to override the a ListViewCell - a single cell in a ListView.
   */
  private class PersonSkillsListViewCell extends TextFieldListCell<Skill> {

    public PersonSkillsListViewCell() {
      super();

      // double click for editing
      this.setOnMouseClicked(click -> {
        if (click.getClickCount() == 2 &&
            click.getButton() == MouseButton.PRIMARY &&
            !isEmpty()) {
          Skill selectedSkill = personSkillList.getSelectionModel().getSelectedItem();
          mainApp.showSkillDialogWithinPerson(selectedSkill, thisStage);
          selectedSkills.remove(selectedSkill);
          selectedSkills.add(selectedSkill);
          personSkillList.getSelectionModel().select(selectedSkill);
          if (createOrEdit == CreateOrEdit.EDIT) {
            checkButtonDisabled();
          }
        }
      });
    }

    @Override
    public void updateItem(Skill item, boolean empty) {
      // calling super here is very important - don't skip this!
      super.updateItem(item, empty);

      setText(item == null ? "" : item.getLabel());
    }
  }

  /**
   * A button which when clicked can add a new skill to the system.
   * Also adds to undo/redo stack so creation is undoable
   * @param event Button click
   */
  @FXML
  protected void addNewSkill(ActionEvent event) {
    mainApp.showSkillDialog(CreateOrEdit.CREATE);
    Set<Skill> currentSkills = new HashSet<>();
    for (Skill skill : selectedSkills) {
      currentSkills.add(skill);
    }
    for (Skill skill : mainApp.getSkills()) {
      if (!availableSkills.contains(skill) && !currentSkills.contains(skill)) {
        availableSkills.add(skill);
      }
    }
  }
}

