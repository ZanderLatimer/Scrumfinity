package seng302.group5.controller.dialogControllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import seng302.group5.Main;
import seng302.group5.controller.enums.CreateOrEdit;
import seng302.group5.controller.enums.DialogMode;
import seng302.group5.model.AgileController;
import seng302.group5.model.AgileHistory;
import seng302.group5.model.Backlog;
import seng302.group5.model.Project;
import seng302.group5.model.Sprint;
import seng302.group5.model.Team;
import seng302.group5.model.undoredo.Action;
import seng302.group5.model.undoredo.UndoRedoObject;
import seng302.group5.model.util.Settings;

/**
 * The controller for the project dialog when creating a new project or editing an existing one
 *
 * @author Alex Woo
 */
public class ProjectDialogController implements AgileController {

  @FXML private TextField projectLabelField;
  @FXML private TextField projectNameField;
  @FXML private TextArea projectDescriptionField;
  @FXML private ListView<Team> availableTeamsList;
  @FXML private ListView<AgileHistory> allocatedTeamsList;
  @FXML private DatePicker teamStartDate;
  @FXML private DatePicker teamEndDate;
  @FXML private Button btnConfirm;
  @FXML private HBox btnContainer;
  @FXML private Label backlogContainer;
  @FXML private ComboBox<Backlog> backlogComboBox;
  @FXML private Button btnNewTeam;
  @FXML private Button btnNewBacklog;
  @FXML private Button btnEditBacklog;
  @FXML private Button btnAddTeam;
  @FXML private Button btnRemoveTeam;


  private Main mainApp;
  private Stage thisStage;
  private CreateOrEdit createOrEdit;
  private DialogMode dialogMode;
  private Project project = new Project();
  private Project lastProject;
  private ObservableList<AgileHistory> allocatedTeams = FXCollections.observableArrayList();
  private ObservableList<Team> availableTeams = FXCollections.observableArrayList();
  private AgileHistory projectHistory = new AgileHistory();
  private ObservableList<Backlog> availableBacklogs;
  private Backlog noBacklog;    // Signal for unassigned a backlog

  /**
   * Setup the project dialog controller
   *
   * @param mainApp      The main application object
   * @param thisStage    The stage of the dialog
   * @param createOrEdit If dialog is for creating or editing a project
   * @param project      The project object if editing, null otherwise
   */
  public void setupController(Main mainApp,
                              Stage thisStage,
                              CreateOrEdit createOrEdit,
                              Project project) {
    this.mainApp = mainApp;
    this.thisStage = thisStage;
    this.dialogMode = DialogMode.DEFAULT_MODE;
    teamStartDate.setValue(LocalDate.now());

    String os = System.getProperty("os.name");

    if (!os.startsWith("Windows")) {
      btnContainer.getChildren().remove(btnConfirm);
      btnContainer.getChildren().add(btnConfirm);
    }

    if (createOrEdit == CreateOrEdit.CREATE) {
      thisStage.setTitle("Create New Project");
      btnConfirm.setText("Create");
      Project p = new Project();
      p.setLabel("");
      p.setProjectName("");
      btnEditBacklog.setDisable(true);


      initialiseLists(CreateOrEdit.CREATE, project);
    } else if (createOrEdit == CreateOrEdit.EDIT) {
      thisStage.setTitle("Edit Project");
      btnConfirm.setText("Save");
      btnConfirm.setDisable(true);

      initialiseLists(CreateOrEdit.EDIT, project);
      projectLabelField.setText(project.getLabel());
      projectNameField.setText(project.getProjectName());
      projectDescriptionField.setText(project.getProjectDescription());
      backlogComboBox.setValue(project.getBacklog());
      if (project.getBacklog() != null) {
        btnEditBacklog.setDisable(false);
      } else {
        btnEditBacklog.setDisable(true);
      }

      for (Sprint sprint : mainApp.getSprints()) {
        if (sprint.getSprintProject().equals(project)) {
          backlogComboBox.setDisable(true);
          btnNewBacklog.setDisable(true);
          backlogContainer.setTooltip(new Tooltip("Backlog cannot be changed if it is assigned to a sprint."));
        }
      }
    }
    this.createOrEdit = createOrEdit;

    if (project != null) {
      this.project = project;
      // Make a copy for the undo stack
      this.lastProject = new Project(project);
    } else {
      this.project = null;
      this.lastProject = null;
    }

    btnConfirm.setDefaultButton(true);
    thisStage.setResizable(false);

    thisStage.setOnCloseRequest(event -> {
      mainApp.popControllerStack();
    });

    // Handle TextField text changes.
    projectLabelField.textProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if(createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
      if (newValue.trim().length() > 20) {
        projectLabelField.setStyle("-fx-text-inner-color: red;");
      } else {
        projectLabelField.setStyle("-fx-text-inner-color: black;");
      }
    });

    projectNameField.textProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if(createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });

    projectDescriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if(createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });

    backlogComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
      if (newValue != null) {
        if (!newValue.getLabel().equals("No Backlog")) {
          btnEditBacklog.setDisable(false);
        } else {
          btnEditBacklog.setDisable(true);
        }
      }
    });
    thisStage.getIcons().add(new Image("Thumbnail.png"));
  }

  /**
   * Set up the dialog to be in sprint mode.
   * @param backlog the backlog to automatically select.
   */
  public void setupSprintMode(Backlog backlog) {
    dialogMode = DialogMode.SPRINT_MODE;
    backlogComboBox.getSelectionModel().select(backlog);
    backlogComboBox.setDisable(true);
    btnNewBacklog.setDisable(true);
    btnEditBacklog.setDisable(true);
  }

  private void checkButtonDisabled() {
    Backlog selectedBacklog = backlogComboBox.getSelectionModel().getSelectedItem();
    Backlog projectBacklog = project.getBacklog();
    boolean isSameBacklog =
        (selectedBacklog != null && (selectedBacklog.equals(projectBacklog))) ||
        ((selectedBacklog == null || selectedBacklog.equals(noBacklog)) && projectBacklog == null);

    if (projectDescriptionField.getText().equals(project.getProjectDescription()) &&
        projectLabelField.getText().equals(project.getLabel()) &&
        isSameBacklog &&
        projectNameField.getText().equals(project.getProjectName())) {

      btnConfirm.setDisable(true);
    } else {
      btnConfirm.setDisable(false);
    }
  }

  /**
   * Initialise the contents of the lists according to whether the user is creating a new project or
   * editing an existing one.
   *
   * @param createOrEdit identifiy whether the user is creating or editing a project.
   * @param project      The project that is being created or edited.
   */
  private void initialiseLists(CreateOrEdit createOrEdit, Project project) {
    availableBacklogs = FXCollections.observableArrayList();
    try {
      if (createOrEdit == CreateOrEdit.CREATE) {
        availableTeams.addAll(mainApp.getTeams());
      } else if (createOrEdit == CreateOrEdit.EDIT) {

        allocatedTeams.addAll(project.getAllocatedTeams().stream().collect(Collectors.toList()));
        availableTeams.addAll(mainApp.getTeams().stream().collect(Collectors.toList()));
      }

      availableBacklogs.addAll(mainApp.getBacklogs());

      for (Project pro : mainApp.getProjects()) {
        if (pro.getBacklog() != null && pro != project) {
          availableBacklogs.remove(pro.getBacklog());
        }
      }

      this.backlogComboBox.setVisibleRowCount(5);
      this.backlogComboBox.setItems(availableBacklogs);
      this.availableTeamsList.setItems(availableTeams.sorted(Comparator.<Team>naturalOrder()));
      this.allocatedTeamsList.setItems(
          allocatedTeams.sorted(Comparator.<AgileHistory>naturalOrder()));

      noBacklog = new Backlog();
      noBacklog.setLabel("No Backlog");
      availableBacklogs.add(0, noBacklog);

    } catch (Exception e) {
      e.printStackTrace();
    }
    setupListView();
  }

  /**
   * Sets the custom behaviour for the team ListView.
   */
  private void setupListView() {
    //Sets the cell being populated with custom settings defined in the ListViewCell class.
    this.availableTeamsList.setCellFactory(listView -> new AllTeamsListViewCell());
  }

  /**
   * Parse a string containing a project label. Throws exceptions if input is not valid.
   *
   * @param inputProjectLabel String of project label
   * @return Inputted project label if it is valid.
   * @throws Exception Exception with message explaining why input is invalid.
   */
  private String parseProjectLabel(String inputProjectLabel) throws Exception {
    inputProjectLabel = inputProjectLabel.trim();

    if (inputProjectLabel.isEmpty()) {
      throw new Exception("Project label is empty.");
    } else {
      String lastProjectLabel;
      if (lastProject == null) {
        lastProjectLabel = "";
      } else {
        lastProjectLabel = lastProject.getLabel();
      }
      for (Project projectInList : mainApp.getProjects()) {
        String projectLabel = projectInList.getLabel();
        if (projectLabel.equalsIgnoreCase(inputProjectLabel) &&
            !projectLabel.equalsIgnoreCase(lastProjectLabel)) {
          throw new Exception("Project label is not unique.");
        }
      }
    }
    return inputProjectLabel;
  }

  /**
   * Parses the selected dates and checks that all inputs are valid, that the selected team is not
   * currently assigned during the chosen dates to any projects.
   *
   * @param startDate The selected start date for the selected team.
   * @param endDate   The selected end date for the selected team.
   * @param team      The selected team that is going to be assigned if dates are valid.
   * @throws Exception with message describing why the input is invalid.
   */
  private void parseProjectDates(LocalDate startDate, LocalDate endDate, Team team)
      throws Exception {
    if (startDate == null) {
      throw new Exception("Start date must be chosen.");
    } else if (endDate != null && startDate.isAfter(endDate)) {
      throw new Exception("Start date must be before end date.");
    } else if (team == null) {
      throw new Exception("Please select a team to assign.");
    } else {
      if (endDate == null) {
        endDate = LocalDate.MAX;
      }
      for (Project project1 : mainApp.getProjects()) {
        for (AgileHistory team1 : project1.getAllocatedTeams()) {
          String projectLabel;
          if (project == null) {
            projectLabel = "";
          } else {
            projectLabel = project.getLabel();
          }

          if (Objects.equals(team.toString(), team1.getAgileItem().toString()) &&
              !projectLabel.equals(project1.getLabel())) {
            if (team1.getEndDate() == null) {
              // team's end date has not been defined yet. assume it is for infinity
              if (startDate.isAfter(team1.getStartDate())) {
                throw new Exception(
                    String.format("The chosen start date is after an existing start date where "
                                  + "the end date is unknown for the project %s.", project1));
              } else if (endDate.isAfter(team1.getStartDate())) {
                if (endDate.equals(LocalDate.MAX)) {
                  throw new Exception(
                      String.format("Cannot have an uncertain end date when one already exists "
                                    + "for the team for the project %s.", project1));
                } else {
                  throw new Exception(
                      String.format("The chosen end date is after an existing start date where "
                                    + "the end date is unknown for the project %s.", project1));
                }
              }
            } else {
              String errorMessage = String.format(
                  "The selected team is already assigned during selected dates for the project %s.",
                  project1);
              if (team1.getStartDate().isEqual(startDate) || team1.getEndDate().isEqual(endDate)) {
                throw new Exception(errorMessage);
              }
              if (startDate.isAfter(team1.getStartDate()) && startDate
                  .isBefore(team1.getEndDate())) {
                throw new Exception(errorMessage);
              } else if (endDate.isAfter(team1.getStartDate()) && endDate
                  .isBefore(team1.getEndDate())) {
                throw new Exception(errorMessage);
              } else if (startDate.isBefore(team1.getStartDate()) && endDate
                  .isAfter(team1.getStartDate())) {
                throw new Exception(errorMessage);
              } else if (startDate.isEqual(team1.getEndDate())) {
                throw new Exception(errorMessage);
              } else if (endDate.isEqual(team1.getStartDate())) {
                throw new Exception(errorMessage);
              }
            }
          }
        }
      }
      for (AgileHistory team2 : allocatedTeams) {
        if (Objects.equals(team.toString(), team2.getAgileItem().toString())) {
          if (team2.getEndDate() == null) {
            // team's end date has not been defined yet. assume it is for infinity
            if (startDate.isAfter(team2.getStartDate())) {
              throw new Exception("The chosen start date is after an existing start date where "
                                  + "the end date is unknown.");
            } else if (endDate.isAfter(team2.getStartDate())) {
              if (endDate.equals(LocalDate.MAX)) {
                throw new Exception("Cannot have an uncertain end date when one already exists "
                                    + "for the team.");
              } else {
                throw new Exception("The chosen end date is after an existing start date where "
                                    + "the end date is unknown.");
              }
            }
          } else {
            if (team2.getStartDate().isEqual(startDate) || team2.getEndDate().isEqual(endDate)) {
              throw new Exception("The selected team is already assigned during selected dates.");
            }
            if (startDate.isAfter(team2.getStartDate()) && startDate.isBefore(team2.getEndDate())) {
              throw new Exception("The selected team is already assigned during selected dates.");
            } else if (endDate.isAfter(team2.getStartDate()) && endDate
                .isBefore(team2.getEndDate())) {
              throw new Exception("The selected team is already assigned during selected dates.");
            } else if (startDate.isBefore(team2.getStartDate()) && endDate
                .isAfter(team2.getStartDate())) {
              throw new Exception("The selected team is already assigned during selected dates.");
            } else if (startDate.isEqual(team2.getEndDate())) {
              throw new Exception("The selected team is already assigned during selected dates.");
            } else if (endDate.isEqual(team2.getStartDate())) {
              throw new Exception("The selected team is already assigned during selected dates.");
            }
          }
        }
      }
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
      undoRedoObject.setAction(Action.PROJECT_CREATE);
    } else {
      undoRedoObject.setAction(Action.PROJECT_EDIT);
      undoRedoObject.addDatum(lastProject);
    }

    // Store a copy of project to edit in stack to avoid reference problems
    undoRedoObject.setAgileItem(project);
    Project projectToStore = new Project(project);
    undoRedoObject.addDatum(projectToStore);

    return undoRedoObject;
  }


  /**
   * Handles when the add button is pushed
   */
  @FXML
  protected void btnAddTeam() {
    try {
      Team selectedTeam = availableTeamsList.getSelectionModel().getSelectedItem();
      parseProjectDates(teamStartDate.getValue(), teamEndDate.getValue(), selectedTeam);
      if (selectedTeam != null) {
        AgileHistory temp = new AgileHistory();
        temp.setAgileItem(selectedTeam);
        temp.setStartDate(teamStartDate.getValue());
        temp.setEndDate(teamEndDate.getValue());
        this.allocatedTeams.add(temp);

        projectHistory.setAgileItem(selectedTeam);
        projectHistory.setStartDate(teamStartDate.getValue());
        projectHistory.setEndDate(teamEndDate.getValue());
        btnConfirm.setDisable(false);
      }
    } catch (Exception e1) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Invalid fields");
      alert.setHeaderText(null);
      alert.setContentText(e1.getMessage());
      alert.getDialogPane().setPrefHeight(150);
      alert.showAndWait();
      mainApp.refreshList(null);
    }
  }

  /**
   * Handles when the remove button is pushed. Removes the selected team from the project.
   */
  @FXML
  protected void btnRemoveTeam() {
    try {
      AgileHistory selectedAgileHistory = allocatedTeamsList.getSelectionModel().getSelectedItem();

      if (selectedAgileHistory != null) {
        AgileHistory temp = new AgileHistory();
        for (AgileHistory agileHistory : allocatedTeams) {
          if (selectedAgileHistory.compareTo(agileHistory) == 0) {
            temp = agileHistory;
            break;
          }
        }
        this.allocatedTeams.remove(temp);
      }
    } catch (Exception e) {
//      System.out.println("Nothing selected");
    }
    btnConfirm.setDisable(false);
  }

  /**
   * Handles when the Create button is pushed by finalising the changes, creating the object and
   * adding it to the session and list.
   */
  @FXML
  protected void btnConfirmClick() {
    StringBuilder errors = new StringBuilder();
    int noErrors = 0;

    String projectLabel = "";
    String projectName = projectNameField.getText().trim();
    String projectDescription = projectDescriptionField.getText().trim();
    Backlog selectedBacklog = backlogComboBox.getValue();
    if (selectedBacklog == noBacklog) {   // Unassign a backlog
      selectedBacklog = null;
    }

    try {
      projectLabel = parseProjectLabel(projectLabelField.getText());
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
        project = new Project(projectLabel, projectName, projectDescription);
        project.setBacklog(selectedBacklog);  //bug fix: backlog null pointer exception
        //backlogComboBox.setValue(project.getBacklog());
        for (AgileHistory team : this.allocatedTeams) {
          project.addTeam(team);
        }
        mainApp.addProject(project);
        if (Settings.correctList(project)) {
          mainApp.refreshList(project);
        }
      } else {
        if (createOrEdit == CreateOrEdit.EDIT) {
          project.setLabel(projectLabel);
          project.setProjectName(projectName);
          project.setProjectDescription(projectDescription);
          project.setBacklog(selectedBacklog);  //bug fix: backlog null pointer exception
          //backlogComboBox.setValue(project.getBacklog());
          project.getAllocatedTeams().clear();
          for (AgileHistory team : this.allocatedTeams) {
            project.addTeam(team);
          }
        }
        if (Settings.correctList(project)) {
          mainApp.refreshList(project);
        }
      }
      UndoRedoObject undoRedoObject = generateUndoRedoObject();
      mainApp.newAction(undoRedoObject);
      mainApp.popControllerStack();
      thisStage.close();
    }
  }

  /**
   * Close the dialog
   */
  @FXML
  protected void btnCancelClick() {
    if (createOrEdit == createOrEdit.EDIT && Settings.correctList(project)) {
      mainApp.refreshList(project);
    }
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
      return project.getLabel();
    }
    return "";
  }

  /**
   * Allows us to override a ListViewCell - a single cell in a ListView.
   */
  private class AllTeamsListViewCell extends TextFieldListCell<Team> {

    public AllTeamsListViewCell() {
      super();

      // double click for editing
      this.setOnMouseClicked(click -> {
        if (click.getClickCount() == 2 &&
            click.getButton() == MouseButton.PRIMARY &&
            !isEmpty()) {
          Team selectedTeam = availableTeamsList.getSelectionModel().getSelectedItem();
          mainApp.showTeamDialogWithinNested(selectedTeam, thisStage);
          availableTeams.remove(selectedTeam);
          availableTeams.add(selectedTeam);
          availableTeamsList.getSelectionModel().select(selectedTeam);
          //This is a hella shitty fix to get the list to update when nested editing coz fml.
          AgileHistory ag = new AgileHistory(new Team(), LocalDate.now(), LocalDate.MAX);
          allocatedTeams.add(ag);
          allocatedTeams.remove(ag);
        }
      });
    }

    @Override
    public void updateItem(Team item, boolean empty) {
      // calling super here is very important - don't skip this!
      super.updateItem(item, empty);

      setText(item == null ? "" : item.getLabel());
    }
  }

  /**
   * A button which when clicked can add a team to the system.
   * Also adds to undo/redo stack so creation is undoable.
   * @param event Button click
   */
  @FXML
  protected void addNewTeam(ActionEvent event) {
    mainApp.showTeamDialog(CreateOrEdit.CREATE);
    availableTeams.setAll(mainApp.getTeams());
  }

  /**
   * A button which when clicked can edit the selected backlog in the backlog combo box.
   * Also adds to undo/redo stack so the edit is undoable.
   * @param event Button click
   */
  @FXML
  protected void editBacklog(ActionEvent event) {
    List<Backlog> tempBacklogList = new ArrayList<>(availableBacklogs);
    Backlog selectedBacklog = backlogComboBox.getSelectionModel().getSelectedItem();
    if (selectedBacklog != null) {
      mainApp.showBacklogDialogNested(selectedBacklog, thisStage);
      availableBacklogs.setAll(tempBacklogList);
      backlogComboBox.getSelectionModel().select(selectedBacklog);
    }
  }

  /**
   * A button which when clicked can add a backlog to the system.
   * Also adds to undo/redo stack so creation is undoable.
   * @param event Button click
   */
  @FXML
  protected void addNewBacklog(ActionEvent event) {
    List<Backlog> tempBacklogList = new ArrayList<>(availableBacklogs);
    tempBacklogList.remove(noBacklog);
    mainApp.showBacklogDialog(CreateOrEdit.CREATE);
    if (!backlogComboBox.isDisabled()) {
      List<Backlog> tempNewBacklogList = new ArrayList<>(mainApp.getBacklogs());
      for (Project pro : mainApp.getProjects()) {
        if (pro.getBacklog() != null && pro != project) {
          tempNewBacklogList.remove(pro.getBacklog());
        }
      }
      for (Backlog backlog : tempNewBacklogList) {
        if (!tempBacklogList.contains(backlog)) {
          availableBacklogs.setAll(tempNewBacklogList);
          availableBacklogs.add(0, noBacklog);
          backlogComboBox.getSelectionModel().select(backlog);
          break;
        }
      }
    }
  }
}

