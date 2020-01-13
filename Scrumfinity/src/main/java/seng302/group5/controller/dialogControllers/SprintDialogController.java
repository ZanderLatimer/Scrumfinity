package seng302.group5.controller.dialogControllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import seng302.group5.Main;
import seng302.group5.controller.enums.CreateOrEdit;
import seng302.group5.model.AgileController;
import seng302.group5.model.AgileHistory;
import seng302.group5.model.Backlog;
import seng302.group5.model.Project;
import seng302.group5.model.Release;
import seng302.group5.model.Sprint;
import seng302.group5.model.Story;
import seng302.group5.model.Task;
import seng302.group5.model.Team;
import seng302.group5.model.undoredo.Action;
import seng302.group5.model.undoredo.CompositeUndoRedo;
import seng302.group5.model.undoredo.UndoRedo;
import seng302.group5.model.undoredo.UndoRedoObject;
import seng302.group5.model.util.Settings;

/**
 * A controller for the sprint dialog which allows the creating or editing of sprints.
 * Note that all fields except the full name and description fields and the sprint can have no
 * stories assigned.
 *
 * Created by Michael Roman and Su-Shing Chen on 24/7/2015.
 */
public class SprintDialogController implements AgileController {

  @FXML private TextField sprintGoalField;
  @FXML private TextField sprintNameField;
  @FXML private TextArea sprintDescriptionField;
  @FXML private ComboBox<Backlog> sprintBacklogCombo;
  @FXML private Label sprintProjectLabel;
  @FXML private ComboBox<Team> sprintTeamCombo;
  @FXML private ComboBox<Release> sprintReleaseCombo;
  @FXML private DatePicker sprintStartDate;
  @FXML private DatePicker sprintEndDate;
  @FXML private ListView<Story> availableStoriesList;
  @FXML private ListView<Story> allocatedStoriesList;
  @FXML private Button btnAddStory;
  @FXML private Button btnRemoveStory;
  @FXML private HBox btnContainer;
  @FXML private Button btnConfirm;
  @FXML private Button btnCancel;
  @FXML private Button addTask;
  @FXML private Button removeTask;
  @FXML private ListView<Task> taskList;
  @FXML private Label releaseDate;
  @FXML private TextField sprintImpedimentsField;
  @FXML private Button btnNewStory;
  @FXML private Button btnNewBacklog;
  @FXML private Button btnEditBacklog;
  @FXML private Button btnNewProject;
  @FXML private Button btnEditProject;
  @FXML private Button btnEditTeam;
  @FXML private Button btnNewRelease;
  @FXML private Button btnEditRelease;

  private Main mainApp;
  private Stage thisStage;

  private CreateOrEdit createOrEdit;

  private Sprint sprint;
  private Sprint lastSprint;
  private Project project;

  private ObservableList<Story> availableStories;
  private ObservableList<Story> allocatedStoriesPrioritised;
  private ObservableList<Backlog> backlogs;
  private ObservableList<Team> teams;
  private ObservableList<Release> releases;
  private ObservableList<Task> tasks;
  private List<Task> originalTasks;

  private Map<Backlog, Project> projectMap;
  private List<Story> allocatedStories;   // use to maintain priority order
  private List<Story> otherSprintsStories;

  private CompositeUndoRedo tasksUndoRedo;

  /**
   * Sets up the controller on start up.
   * If editing it fills the fields with the values in that sprint object
   * Otherwise leaves fields empty.
   * Adds listeners to all fields to enable checking of changes when editing so that the save
   * button can be greyed out.
   *
   * @param mainApp The main class of the program. For checking the list of all existing sprints.
   * @param thisStage This is the window that will be displayed.
   * @param createOrEdit This is an ENUM object to determine if creating or editing.
   * @param sprint The object that will edited or created (made into a valid sprint).
   */
  public void setupController(Main mainApp, Stage thisStage, CreateOrEdit createOrEdit,
                              Sprint sprint) {
    this.mainApp = mainApp;
    this.thisStage = thisStage;

    if (sprint != null) {
      this.sprint = sprint;
      this.lastSprint = new Sprint(sprint);
    } else {
      this.sprint = new Sprint(); // different because tasks
      this.lastSprint = null;
    }

    String os = System.getProperty("os.name");

    if (!os.startsWith("Windows")) {
      btnContainer.getChildren().remove(btnConfirm);
      btnContainer.getChildren().add(btnConfirm);
    }

    if (createOrEdit == CreateOrEdit.CREATE) {
      thisStage.setTitle("Create New Sprint");
      btnConfirm.setText("Create");
      initialiseLists();

      sprintTeamCombo.setDisable(true);
      sprintReleaseCombo.setDisable(true);
      btnEditBacklog.setDisable(true);
      btnNewProject.setDisable(true);
      btnEditProject.setDisable(true);
      btnEditTeam.setDisable(true);
      btnEditRelease.setDisable(true);
      btnNewRelease.setDisable(true);
      btnNewStory.setDisable(true);

    } else if (createOrEdit == CreateOrEdit.EDIT) {
      thisStage.setTitle("Edit Sprint");
      btnConfirm.setText("Save");
      btnConfirm.setDisable(true);
      initialiseLists();
      btnNewProject.setDisable(true);

      sprintGoalField.setText(sprint.getLabel());
      sprintNameField.setText(sprint.getSprintFullName());
      sprintDescriptionField.setText(sprint.getSprintDescription());
      sprintBacklogCombo.getSelectionModel().select(sprint.getSprintBacklog()); // Updates Project
      sprintTeamCombo.getSelectionModel().select(sprint.getSprintTeam());
      sprintReleaseCombo.getSelectionModel().select(sprint.getSprintRelease());
      sprintImpedimentsField.setText(sprint.getSprintImpediments());
      String dateFormat = "dd/MM/yyy";
      releaseDate.setText(sprint.getSprintRelease().getReleaseDate().format(
          DateTimeFormatter.ofPattern(dateFormat)));
      sprintStartDate.setValue(sprint.getSprintStart());
      sprintEndDate.setValue(sprint.getSprintEnd());
      allocatedStories.addAll(sprint.getSprintStories());
      tasks.addAll(sprint.getTasks());
      originalTasks.addAll(sprint.getTasks());


      // Stories from other sprints with same backlog
      for (Sprint mainSprint : mainApp.getSprints()) {
        if (!mainSprint.equals(sprint) &&
            mainSprint.getSprintBacklog().equals(sprint.getSprintBacklog())) {
          otherSprintsStories.addAll(mainSprint.getSprintStories());
        }
      }

      refreshLists();
    }
    this.createOrEdit = createOrEdit;

    btnConfirm.setDefaultButton(true);
    thisStage.setResizable(false);

    this.tasksUndoRedo = new CompositeUndoRedo("Edit Multiple Tasks");

    thisStage.setOnCloseRequest(event -> {
      mainApp.popControllerStack();
    });

    sprintGoalField.textProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
      if (newValue.trim().length() > 20) {
        sprintGoalField.setStyle("-fx-text-inner-color: red;");
      } else {
        sprintGoalField.setStyle("-fx-text-inner-color: black;");
      }
    });

    sprintNameField.textProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });
    sprintImpedimentsField.textProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });
    sprintDescriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });
    sprintBacklogCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
      if (newValue != null) {
        btnEditBacklog.setDisable(false);
        btnNewStory.setDisable(false);
      } else {
        btnEditBacklog.setDisable(true);
        btnNewStory.setDisable(true);
      }
    });

    sprintTeamCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
      if (newValue != null) {
        btnEditTeam.setDisable(false);
      } else {
        btnEditTeam.setDisable(true);
      }
    });
    sprintReleaseCombo.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
                       String dateFormat = "dd/MM/yyy";
                       if (sprintReleaseCombo.getSelectionModel().getSelectedItem() != null &&
                           sprintReleaseCombo.getSelectionModel().getSelectedItem().getReleaseDate()
                           != null) {
                         releaseDate.setText(sprintReleaseCombo.getSelectionModel()
                                                 .getSelectedItem().getReleaseDate().
                                 format(DateTimeFormatter.ofPattern(dateFormat)));
                       }
                     }
        );
    sprintReleaseCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
      if (newValue != null) {
        btnEditRelease.setDisable(false);
      } else {
        btnEditRelease.setDisable(true);
      }
    });
    sprintStartDate.valueProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });
    sprintEndDate.valueProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });

    taskList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      //For disabling the button
      if(createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });
    thisStage.getIcons().add(new Image("Thumbnail.png"));
  }

  /**
   * Check if any of the fields has been changed. If nothing changed,
   * then confirm button is disabled.
   */
  private void checkButtonDisabled() {
    //Needs to check for null which occurs on creation of new task
    if (taskList.getItems() != null) {
      if (taskList.getItems().equals(sprint.getTasks())) {
        btnConfirm.setDisable(true);
      }
    }
    if (sprintGoalField.getText().equals(sprint.getLabel()) &&
        sprintNameField.getText().equals(sprint.getSprintFullName()) &&
        sprintDescriptionField.getText().equals(sprint.getSprintDescription()) &&
        sprintImpedimentsField.getText().equals(sprint.getSprintImpediments()) &&
        (sprintBacklogCombo.getValue() == null ||
         sprintBacklogCombo.getValue().equals(sprint.getSprintBacklog())) &&
        (sprintTeamCombo.getValue() == null ||
         sprintTeamCombo.getValue().equals(sprint.getSprintTeam())) &&
        (sprintReleaseCombo.getValue() == null ||
         sprintReleaseCombo.getValue().equals(sprint.getSprintRelease())) &&
        sprintStartDate.getValue().equals(sprint.getSprintStart()) &&
        sprintEndDate.getValue().equals(sprint.getSprintEnd()) &&
        tasks.equals(originalTasks) &&
        allocatedStoriesPrioritised.equals(sprint.getSprintStories()) &&
        tasksUndoRedo.getUndoRedos().isEmpty()) {
      btnConfirm.setDisable(true);
    } else {
      btnConfirm.setDisable(false);
    }
  }

  /**
   * Initialises the models lists and populates these with values from the main application,
   * such as available stories, allocated stories, backlogs, teams and releases. These values
   * are then populated into their respective GUI elements. The backlog combo box has a listener
   * to update other GUI elements which depend on the backlog.
   * Populates a list of available stories for assigning them to sprint
   */
  private void initialiseLists() {
    availableStories = FXCollections.observableArrayList();
    tasks = FXCollections.observableArrayList();
    allocatedStoriesPrioritised = FXCollections.observableArrayList();
    backlogs = FXCollections.observableArrayList();
    teams = FXCollections.observableArrayList();
    releases = FXCollections.observableArrayList();
    originalTasks = new ArrayList<>();

    // set up map from backlog to project
    projectMap = new IdentityHashMap<>();
    for (Project project : mainApp.getProjects()) {
      Backlog projectBacklog = project.getBacklog();
      if (projectBacklog != null) {
        projectMap.put(projectBacklog, project);
      }
    }

    allocatedStories = new ArrayList<>();
    otherSprintsStories = new ArrayList<>();

    backlogs.setAll(mainApp.getBacklogs());

    sprintBacklogCombo.setVisibleRowCount(5);
    sprintTeamCombo.setVisibleRowCount(5);
    sprintReleaseCombo.setVisibleRowCount(5);

    sprintBacklogCombo.setItems(backlogs);
    sprintTeamCombo.setItems(teams);
    sprintReleaseCombo.setItems(releases);

    availableStoriesList.setItems(availableStories);
    allocatedStoriesList.setItems(allocatedStoriesPrioritised);
    taskList.setItems(tasks);

    sprintBacklogCombo.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldBacklog, newBacklog) -> {

          // if the backlog is assigned to a project
          if (projectMap.containsKey(newBacklog)) {
            project = projectMap.get(newBacklog);
            sprintProjectLabel.setText(project.toString());

            btnNewProject.setDisable(true);
            btnEditProject.setDisable(false);
            btnNewRelease.setDisable(false);

            sprintTeamCombo.setDisable(false);
            sprintReleaseCombo.setDisable(false);

            // get project's current teams
            teams.clear();
            for (AgileHistory teamHistory : project.getAllocatedTeams()) {
              teams.add((Team) teamHistory.getAgileItem());
            }

            // get project's releases
            releases.clear();
            for (Release release : mainApp.getReleases()) {
              if (release.getProjectRelease().equals(project)) {
                releases.add(release);
              }
            }

            // reset interface
            sprintTeamCombo.getSelectionModel().select(null);
            sprintReleaseCombo.getSelectionModel().select(null);
            allocatedStories.clear();

            // Stories from other sprints with same backlog
            otherSprintsStories.clear();
            for (Sprint mainSprint : mainApp.getSprints()) {
              if ((sprint == null) ||
                  (!mainSprint.equals(sprint) &&
                   mainSprint.getSprintBacklog().equals(sprintBacklogCombo.getValue()))) {
                otherSprintsStories.addAll(mainSprint.getSprintStories());
              }
            }

            refreshLists();
          } else {
            project = null;
            sprintProjectLabel.setText("No Project Found");

            btnNewProject.setDisable(false);
            btnEditProject.setDisable(true);
            btnEditTeam.setDisable(true);
            btnNewRelease.setDisable(true);
            btnEditRelease.setDisable(true);

            sprintTeamCombo.setValue(null);
            sprintReleaseCombo.setValue(null);
            sprintTeamCombo.setDisable(true);
            sprintReleaseCombo.setDisable(true);
            availableStories.clear();
            allocatedStoriesPrioritised.clear();
            allocatedStories.clear();
          }
        });
    setupListView();
  }

  /**
   * Adds the selected story from the list of available stories in the selected product backlog to
   * the allocated stories for the sprint.
   *
   * @param event Action event
   */
  @FXML
  protected void btnAddStoryClick(ActionEvent event) {
    Story selectedStory = availableStoriesList.getSelectionModel().getSelectedItem();
    if (selectedStory != null) {
      int selectedIndex = availableStoriesList.getSelectionModel().getSelectedIndex();
      allocatedStories.add(selectedStory);
      refreshLists();
      allocatedStoriesList.getSelectionModel().select(selectedStory);
      if (!availableStories.isEmpty()) {
        availableStoriesList.getSelectionModel().select(
            Math.min(selectedIndex, availableStoriesList.getItems().size() - 1));
      }
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    }
  }

  /**
   * Remove the selected story from the list of allocated stories in the sprint and put it back in
   * the list of available stories.
   *
   * @param event Action event
   */
  @FXML
  protected void btnRemoveStoryClick(ActionEvent event) {
    Story selectedStory = allocatedStoriesList.getSelectionModel().getSelectedItem();
    if (selectedStory != null) {
      int selectedIndex = allocatedStoriesList.getSelectionModel().getSelectedIndex();
      allocatedStories.remove(selectedStory);
      refreshLists();
      availableStoriesList.getSelectionModel().select(selectedStory);
      if (!allocatedStoriesPrioritised.isEmpty()) {
        allocatedStoriesList.getSelectionModel().select(
            Math.min(selectedIndex, allocatedStoriesList.getItems().size() - 1));
      }
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    }
  }

  /**
   * Open the dialog for task dialog creation. The created task will be added to the tasks list,
   * not the story model.
   */
  @FXML
  private void addTask() {
    UndoRedo taskCreate;
    if (sprintTeamCombo.getSelectionModel().getSelectedItem() == null) {
      taskCreate = mainApp.showTaskDialog(sprint, null, null,
                             CreateOrEdit.CREATE, thisStage);
    } else {
      taskCreate = mainApp.showTaskDialog(sprint, null, sprintTeamCombo.getSelectionModel().getSelectedItem(),
                             CreateOrEdit.CREATE, thisStage);
    }
    if (taskCreate != null) {
      tasksUndoRedo.addUndoRedo(taskCreate);
      tasks.add(sprint.getTasks().get(sprint.getTasks().size() - 1));

      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    }
  }

  /**
   * Remove the selected task from the list.
   */
  @FXML
  private void removeTask() {
    Task selectedTask = taskList.getSelectionModel().getSelectedItem();
    if (selectedTask != null) {
      UndoRedo taskDelete = new UndoRedoObject();
      taskDelete.setAction(Action.TASK_DELETE);
      taskDelete.addDatum(new Task(selectedTask));
      taskDelete.addDatum(sprint);

      // Store a copy of task to edit in object to avoid reference problems
      taskDelete.setAgileItem(selectedTask);

      sprint.removeTask(selectedTask);
      tasksUndoRedo.addUndoRedo(taskDelete);
      tasks.remove(selectedTask);
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    }
  }

  /**
   * Moves the selected task up one position in the ListView.
   * @param event Event generated by the listener.
   */
  @FXML
  private void btnUpTask(ActionEvent event) {
    if (taskList.getSelectionModel().getSelectedItem() != null) {
      Object selectedItem = taskList.getSelectionModel().getSelectedItem();
      int itemIndex = tasks.indexOf(selectedItem);
      if (itemIndex != 0) {
        Task temp = tasks.get(itemIndex - 1);
        tasks.set(itemIndex - 1, (Task) selectedItem);
        tasks.set(itemIndex, temp);
        taskList.getSelectionModel().select(itemIndex - 1);
      }
      taskList.scrollTo(itemIndex - 8);
    }
  }

  /**
   * Moves the selected task down one position in the ListView.
   * @param event Event generated by the listener.
   */
  @FXML
  private void btnDownTask(ActionEvent event) {
    if (taskList.getSelectionModel().getSelectedItem() != null) {
      Object selectedItem = taskList.getSelectionModel().getSelectedItem();
      int itemIndex = tasks.indexOf(selectedItem);
      if (itemIndex != tasks.size() - 1) {
        Task temp = tasks.get(itemIndex + 1);
        tasks.set(itemIndex + 1, (Task) selectedItem);
        tasks.set(itemIndex, temp);
        taskList.getSelectionModel().select(itemIndex + 1);
      }
      taskList.scrollTo(itemIndex - 6);
    }
  }

  /**
   * Refresh the lists such that they maintain the original priority order specified in the backlog.
   * Call whenever the story allocation changes.
   */
  private void refreshLists() {
    Backlog selectedBacklog = sprintBacklogCombo.getSelectionModel().getSelectedItem();

    availableStories.clear();
    allocatedStoriesPrioritised.clear();

    for (Story story : selectedBacklog.getStories()) {
      // add story to either available or allocated stories in priority order
      if (allocatedStories.contains(story)) {
        allocatedStoriesPrioritised.add(story);
      } else {
        if (story.getStoryState() && !otherSprintsStories.contains(story)) {
          availableStories.add(story);
        }
      }
    }
  }

  /**
   * Handles the event of clicking the save button in this dialog.
   * Checks for errors with what was input into the fields and displays alerts if errors are found
   * Otherwise creates or updates sprint depending on if your creating or editing.
   * Then creates the undo/redo object.
   *
   * @param event This is the event of the save button being clicked
   */
  @FXML
  protected void btnConfirmClick(ActionEvent event) {
    StringBuilder errors = new StringBuilder();
    int noErrors = 0;

    String sprintGoal = "";
    String sprintName = sprintNameField.getText().trim();
    String sprintDescription = sprintDescriptionField.getText().trim();
    String sprintImpediments = sprintImpedimentsField.getText().trim();
    Backlog backlog = sprintBacklogCombo.getValue();
    Team team = null;
    Release release = sprintReleaseCombo.getValue();
    LocalDate startDate = null;
    LocalDate endDate = null;

    try {
      sprintGoal = parseSprintGoal(sprintGoalField.getText());
    } catch (Exception e) {
      noErrors++;
      errors.append(String.format("%s\n", e.getMessage()));
    }

    if (backlog == null) {
      noErrors++;
      errors.append(String.format("%s\n", "No backlog selected."));
    } else if (project == null) {
      noErrors++;
      errors.append(String.format("%s\n", "Selected backlog is not assigned to a project."));
    }

    if (release == null) {
      noErrors++;
      errors.append(String.format("%s\n", "No release selected."));
    }

    try {
      startDate = parseStartDate(sprintStartDate.getValue(), release);
    } catch (Exception e) {
      noErrors++;
      errors.append(String.format("%s\n", e.getMessage()));
    }

    try {
      endDate = parseEndDate(sprintEndDate.getValue(), sprintStartDate.getValue(), release);
    } catch (Exception e) {
      noErrors++;
      errors.append(String.format("%s\n", e.getMessage()));
    }

    try {
      team = parseTeam(sprintTeamCombo.getValue(), startDate, endDate);
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
      noErrors += 1;
      alert.getDialogPane().setPrefHeight(60 + 20 * noErrors);
      alert.setContentText(errors.toString());
      alert.showAndWait();
    } else {
      if (createOrEdit == CreateOrEdit.CREATE) {
        sprint.setSprintGoal(sprintGoal);
        sprint.setSprintFullName(sprintName);
        sprint.setSprintDescription(sprintDescription);
        sprint.setSprintBacklog(backlog);
        sprint.setSprintProject(project);
        sprint.setSprintTeam(team);
        sprint.setSprintRelease(release);
        sprint.setSprintStart(startDate);
        sprint.setSprintEnd(endDate);
        sprint.addAllStories(allocatedStoriesPrioritised);
        sprint.setSprintImpediments(sprintImpediments);
        // tasks are already in sprint
        mainApp.addSprint(sprint);

        if (Settings.correctList(sprint)) {
          mainApp.refreshList(sprint);
        }
      } else if (createOrEdit == CreateOrEdit.EDIT) {
        sprint.setSprintGoal(sprintGoal);
        sprint.setSprintFullName(sprintName);
        sprint.setSprintDescription(sprintDescription);
        sprint.setSprintBacklog(backlog);
        sprint.setSprintProject(project);
        sprint.setSprintTeam(team);
        sprint.setSprintRelease(release);
        sprint.setSprintImpediments(sprintImpediments);
        sprint.setSprintStart(startDate);
        sprint.setSprintEnd(endDate);
        sprint.removeAllStories();
        sprint.addAllStories(allocatedStoriesPrioritised);
        //Refreshes task list so that order changes are preserved
        sprint.removeAllTasks();
        sprint.addAllTasks(tasks);


        mainApp.refreshList(sprint);
      }
      UndoRedo undoRedoObject = generateUndoRedoObject();
      if (sprint != null && createOrEdit == CreateOrEdit.CREATE) {
        undoRedoObject.addDatum(sprint);
      } else {
        undoRedoObject.addDatum(null);
      }
      mainApp.newAction(undoRedoObject);
      mainApp.popControllerStack();
      thisStage.close();
    }
  }

  /**
   * Discards all changes made from within the dialog and exits the dialog.
   *
   * @param event Action event
   */
  @FXML
  protected void btnCancelClick(ActionEvent event) {
    Alert alert = null;
    if ((createOrEdit == CreateOrEdit.CREATE && !tasks.isEmpty()) ||
        (createOrEdit == CreateOrEdit.EDIT && (!tasks.equals(sprint.getTasks()) ||
                                               !tasksUndoRedo.getUndoRedos().isEmpty()))) {

      alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Changes have been made to the sprint's tasks");
      alert.setHeaderText(null);
      String message = "By cancelling this dialog you will lose all changes you have made "
                       + "to tasks since this sprint dialog was opened. Are you sure you wish "
                       + "to continue?";
      alert.getDialogPane().setPrefHeight(150);
      alert.setContentText(message);
      //checks response
      alert.showAndWait();
    }
    if (alert == null || alert.getResult() == ButtonType.OK) {
      if (createOrEdit == CreateOrEdit.EDIT && Settings.correctList(sprint)) {
        mainApp.refreshList(sprint);
      }
      // undo all editing of existing tasks made within this dialog
      mainApp.quickUndo(tasksUndoRedo);
      if (createOrEdit == CreateOrEdit.EDIT) {
        sprint.copyValues(lastSprint);
      }
      mainApp.popControllerStack();
      thisStage.close();
    }
  }

  /**
   * Generate an UndoRedoObject to place in the stack.
   *
   * @return The UndoRedoObject to store.
   */
  private UndoRedo generateUndoRedoObject() {
    Action action;
    UndoRedoObject sprintChanges = new UndoRedoObject();

    if (createOrEdit == CreateOrEdit.CREATE) {
      action = Action.SPRINT_CREATE;
      sprintChanges.setAction(action);
    } else {
      action = Action.SPRINT_EDIT;
      sprintChanges.setAction(action);
      sprintChanges.addDatum(lastSprint);
    }

    // Store a copy of sprint to edit in stack to avoid reference problems
    sprintChanges.setAgileItem(sprint);
    Sprint sprintToStore = new Sprint(sprint);
    sprintChanges.addDatum(sprintToStore);

    // Create composite undo/redo with original action string to handle sprint and task changes
    CompositeUndoRedo sprintAndTaskChanges = new CompositeUndoRedo(Action.getActionString(action));
    sprintAndTaskChanges.addUndoRedo(sprintChanges);
    for (UndoRedo taskChange : tasksUndoRedo.getUndoRedos()) {
      // only include edits to avoid doubling tasks
      UndoRedo actualTaskChange;
      if (taskChange instanceof CompositeUndoRedo) {
        // if it's composite then the actual task edit is the first item of the composite
        actualTaskChange = ((CompositeUndoRedo) taskChange).getUndoRedos().get(0);
      } else {
        actualTaskChange = taskChange;
      }
      if (actualTaskChange.getAction().equals(Action.TASK_EDIT)) {
        sprintAndTaskChanges.addUndoRedo(taskChange);
      }
    }

    return sprintAndTaskChanges;
  }

  /**
   * Checks if sprint goal field contains valid input.
   *
   * @param inputSprintGoal String sprint label or sprint goal.
   * @return sprint label/goal if sprint label/goal is valid.
   * @throws Exception If sprint label/goal is not valid.
   */
  private String parseSprintGoal(String inputSprintGoal) throws Exception {
    inputSprintGoal = inputSprintGoal.trim();

    if (inputSprintGoal.isEmpty()) {
      throw new Exception("Sprint goal is empty.");
    } else {
      String lastSprintGoal;
      if (lastSprint == null) {
        lastSprintGoal = "";
      } else {
        lastSprintGoal = lastSprint.getLabel();
      }
      for (Sprint sprint : mainApp.getSprints()) {
        String sprintGoal = sprint.getLabel();
        if (sprint.getLabel().equalsIgnoreCase(inputSprintGoal) &&
            !sprintGoal.equalsIgnoreCase(lastSprintGoal)) {
          throw new Exception("Sprint goal is not unique.");
        }
      }
      return inputSprintGoal;
    }
  }

  /**
   * Verifies that the team is not already assigned at this time and that the team is not null.
   *
   * @param team The team for checking
   * @param startDate The start date of the sprint.
   * @param endDate The end date of the sprint.
   * @return The team if it is valid
   * @throws Exception If the team is not valid.
   */
  private Team parseTeam(Team team, LocalDate startDate, LocalDate endDate) throws Exception {
    if (team == null) {
      throw new Exception("No team selected.");
    } else if (startDate != null && endDate != null) {
      //If team is already assigned to a sprint, make sure the dates don't overlap.
      for (Sprint sprint : mainApp.getSprints()) {
        if (sprint.getSprintTeam() == team && sprint != this.sprint) {
          //Sorry for the horrible if statement!
          //Checks if the dates overlap.
          if (((startDate.isAfter(sprint.getSprintStart()) ||
                startDate.isEqual(sprint.getSprintStart())) &&
                startDate.isBefore(sprint.getSprintEnd())) ||
              ((endDate.isBefore(sprint.getSprintEnd()) ||
               endDate.isEqual((sprint.getSprintEnd()))) &&
               endDate.isAfter(sprint.getSprintStart())) ||
              ((startDate.isBefore(sprint.getSprintStart()) ||
               startDate.isEqual(sprint.getSprintStart())) &&
               (endDate.isAfter(sprint.getSprintEnd()) ||
               endDate.isEqual(sprint.getSprintEnd())))) {
            throw new Exception("Team is already assigned to a sprint in selected time frame.");
          }
        }
      }
    }
    return team;
  }

  /**
   * Verify that the start date is valid in that it is not null and it is before the release date.
   *
   * @param startDate Start date of sprint.
   * @param release Release of sprint used to get date.
   * @return Start date of sprint if it is valid.
   * @throws Exception if sprint start date is not valid.
   */
  private LocalDate parseStartDate(LocalDate startDate, Release release) throws Exception {
    if (startDate == null) {
      throw new Exception("No start date selected.");
    } else if (release != null && startDate.isAfter(release.getReleaseDate())) {
      String dateFormat = "dd/MM/yyy";
      String releaseDate = release.getReleaseDate().format(DateTimeFormatter.ofPattern(dateFormat));
      throw new Exception("Start date must be before release date - " + releaseDate + ".");
    }
    return startDate;
  }

  /**
   * Verify that the end date is valid in that it is not null, it is after the start date, and
   * it is before the release date.
   *
   * @param endDate End date of sprint
   * @param startDate Start date of sprint
   * @param release Release of sprint used to get date.
   * @return End date of sprint if it is valid.
   * @throws Exception if sprint end date is not valid.
   */
  private LocalDate parseEndDate(LocalDate endDate, LocalDate startDate, Release release)
      throws Exception {
    if (endDate == null) {
      throw new Exception("No end date selected.");
    } else {
      boolean afterReleaseDate = release != null && endDate.isAfter(release.getReleaseDate());
      boolean beforeStartDate = endDate.isBefore(startDate);
      if (afterReleaseDate && beforeStartDate) {
        throw new Exception("End date must be before release date and after start date.");
      } else if (afterReleaseDate) {
        String dateFormat = "dd/MM/yyy";
        String releaseDate = release.getReleaseDate().format(DateTimeFormatter.ofPattern(dateFormat));
        throw new Exception("End date must be before release date - " + releaseDate + ".");
      } else if (beforeStartDate) {
        throw new Exception("End date must be after start date.");
      }
    }
    return endDate;
  }
  /**
   * Sets the custom behaviour for the stories ListView.
   */
  private void setupListView() {
    //Sets the cell being populated with custom settings defined in the ListViewCell class.
    this.availableStoriesList.setCellFactory(listView -> new AvailableStoriesListViewCell());
    this.allocatedStoriesList.setCellFactory(listView -> new SprintStoriesListViewCell());
    this.taskList.setCellFactory(listView -> new TaskListCell());
  }
  /**
   * Allows us to override a ListViewCell - a single cell in a ListView.
   */
  private class AvailableStoriesListViewCell extends TextFieldListCell<Story> {

    public AvailableStoriesListViewCell() {
      super();

      // double click for editing
      this.setOnMouseClicked(click -> {
        if (click.getClickCount() == 2 &&
            click.getButton() == MouseButton.PRIMARY &&
            !isEmpty()) {
          Story selectedStory = availableStoriesList.getSelectionModel().getSelectedItem();
          //Tells the controller not to disable the readiness checkbox.
          Backlog backlog = sprintBacklogCombo.getSelectionModel().getSelectedItem();
          mainApp.showStoryDialogWithinSprint(CreateOrEdit.EDIT, selectedStory, backlog, thisStage);
          Story selectedAllocated = allocatedStoriesList.getSelectionModel().getSelectedItem();
          refreshLists();
          allocatedStoriesList.getSelectionModel().select(selectedAllocated);
          availableStoriesList.getSelectionModel().select(selectedStory);
        }
      });
    }

    @Override
    public void updateItem(Story item, boolean empty) {
      // calling super here is very important - don't skip this!
      super.updateItem(item, empty);

      setText(item == null ? "" : item.getLabel());
    }
  }

  /**
   * Allows us to override the a ListViewCell - a single cell in a ListView.
   */
  private class SprintStoriesListViewCell extends TextFieldListCell<Story> {

    public SprintStoriesListViewCell() {
      super();

      // double click for editing
      this.setOnMouseClicked(click -> {
        if (click.getClickCount() == 2 &&
            click.getButton() == MouseButton.PRIMARY &&
            !isEmpty()) {
          Story selectedStory = allocatedStoriesList.getSelectionModel().getSelectedItem();
          //Tells the controller to disable the readiness checkbox.
          Backlog backlog = sprintBacklogCombo.getSelectionModel().getSelectedItem();
          mainApp.showStoryDialogWithinSprint(CreateOrEdit.EDIT, selectedStory, backlog, thisStage);
          Story selectedAvailable = availableStoriesList.getSelectionModel().getSelectedItem();
          refreshLists();
          availableStoriesList.getSelectionModel().select(selectedAvailable);
          allocatedStoriesList.getSelectionModel().select(selectedStory);
        }
      });
    }

    @Override
    public void updateItem(Story item, boolean empty) {
      // calling super here is very important - don't skip this!
      super.updateItem(item, empty);

      setText(item == null ? "" : item.getLabel());
    }
  }


  /**
   * Allow double clicking for editing a Task.
   */
  private class TaskListCell extends TextFieldListCell<Task> {

    public TaskListCell() {
      super();
      this.setOnMouseClicked(click -> {
        if (click.getClickCount() == 2 &&
            click.getButton() == MouseButton.PRIMARY &&
            !isEmpty()) {
          UndoRedo taskEdit =
              mainApp.showTaskDialog(sprint, getItem(), sprintTeamCombo.getSelectionModel().
                  getSelectedItem(), CreateOrEdit.EDIT, thisStage);
          if (taskEdit != null) {
            tasksUndoRedo.addUndoRedo(taskEdit);
            taskList.setItems(null);
            taskList.setItems(tasks);
            taskList.getSelectionModel().select(getItem());
            if (createOrEdit == CreateOrEdit.EDIT) {
              checkButtonDisabled();
            }
          }
        }
      });
    }
  }

  /**
   * A button which when clicked can add a new story to the system.
   * Also adds to undo/redo stack so creation is undoable
   * @param event Button click
   */
  @FXML
  protected void addNewStory(ActionEvent event) {
    Backlog selectedBacklog = sprintBacklogCombo.getSelectionModel().getSelectedItem();
    mainApp.showStoryDialogWithinSprint(CreateOrEdit.CREATE, null, selectedBacklog, thisStage);
    refreshLists();
  }

  /**
   * A button which when clicked can edit the selected backlog in the backlog combo box.
   * Also adds to undo/redo stack so the edit is undoable.
   * @param event Button click
   */
  @FXML
  protected void editBacklog(ActionEvent event) {
    List<Backlog> tempBacklogList = new ArrayList<>(backlogs);
    Team tempTeam = sprintTeamCombo.getSelectionModel().getSelectedItem();
    Release tempRelease = sprintReleaseCombo.getSelectionModel().getSelectedItem();
    List<Story> tempAllocatedStories = new ArrayList<>(allocatedStories);
    Backlog selectedBacklog = sprintBacklogCombo.getSelectionModel().getSelectedItem();
    if (selectedBacklog != null) {
      mainApp.showBacklogDialogNested(selectedBacklog, thisStage);
      backlogs.setAll(tempBacklogList); // this line deselects everything due to the listener
      sprintBacklogCombo.getSelectionModel().select(selectedBacklog);
      sprintTeamCombo.getSelectionModel().select(tempTeam);
      sprintReleaseCombo.getSelectionModel().select(tempRelease);
      allocatedStories.clear();
      for (Story story : selectedBacklog.getStories()) {
        // add story to allocated stories if it was previously allocated and is still in backlog
        if (tempAllocatedStories.contains(story)) {
          allocatedStories.add(story);
        }
      }
      refreshLists();
    }
  }

  /**
   * A button which when clicked can add a backlog to the system.
   * Also adds to undo/redo stack so creation is undoable.
   * @param event Button click
   */
  @FXML
  protected void addNewBacklog(ActionEvent event) {
    List<Backlog> tempBacklogList = new ArrayList<>(backlogs);
    mainApp.showBacklogDialog(CreateOrEdit.CREATE);
    List<Backlog> tempNewBacklogList = new ArrayList<>(mainApp.getBacklogs());
    for (Backlog backlog : tempNewBacklogList) {
      if (!tempBacklogList.contains(backlog)) {
        backlogs.setAll(tempNewBacklogList);
        sprintBacklogCombo.getSelectionModel().select(backlog);
        refreshLists();
        break;
      }
    }
  }

  /**
   * A button which when clicked can edit the displayed project.
   * Also adds to undo/redo stack so the edit is undoable.
   * @param event Button click
   */
  @FXML
  protected void editProject(ActionEvent event) {
    Backlog backlog = sprintBacklogCombo.getSelectionModel().getSelectedItem();
    Team team = sprintTeamCombo.getValue();
    mainApp.showProjectDialogWithinSprint(CreateOrEdit.EDIT, project, backlog, thisStage);
    sprintProjectLabel.setText(project.getLabel());
    teams.clear();
    for (AgileHistory teamHistory : project.getAllocatedTeams()) {
      teams.add((Team) teamHistory.getAgileItem());
    }
    if (teams.contains(team)) {
      sprintTeamCombo.getSelectionModel().select(team);
    }
  }

  /**
   * A button which when clicked can add a project to the system.
   * Also adds to undo/redo stack so creation is undoable.
   * @param event Button click
   */
  @FXML
  protected void addNewProject(ActionEvent event) {
    Backlog backlog = sprintBacklogCombo.getSelectionModel().getSelectedItem();
    mainApp.showProjectDialogWithinSprint(CreateOrEdit.CREATE, null, backlog, thisStage);

    // set up map from backlog to project again
    projectMap.clear();
    for (Project project : mainApp.getProjects()) {
      Backlog projectBacklog = project.getBacklog();
      if (projectBacklog != null) {
        projectMap.put(projectBacklog, project);
      }
    }

    // fire the listener to update the dialog
    sprintBacklogCombo.getSelectionModel().select(null);
    sprintBacklogCombo.getSelectionModel().select(backlog);
  }

  /**
   * A button which when clicked can edit the selected team in the team combo box.
   * Also adds to undo/redo stack so the edit is undoable.
   * @param event Button click
   */
  @FXML
  protected void editTeam(ActionEvent event) {
    List<Team> tempTeamList = new ArrayList<>(teams);
    Team selectedTeam = sprintTeamCombo.getSelectionModel().getSelectedItem();
    if (selectedTeam != null) {
      mainApp.showTeamDialogWithinNested(selectedTeam, thisStage);
      teams.setAll(tempTeamList);
      sprintTeamCombo.getSelectionModel().select(selectedTeam);
    }
  }

  /**
   * A button which when clicked can edit the displayed release.
   * Also adds to undo/redo stack so the edit is undoable.
   * @param event Button click
   */
  @FXML
  protected void editRelease(ActionEvent event) {
    List<Release> tempReleaseList = new ArrayList<>(releases);
    Release selectedRelease = sprintReleaseCombo.getSelectionModel().getSelectedItem();
    if (selectedRelease != null) {
      mainApp.showReleaseDialogWithinSprint(CreateOrEdit.EDIT, selectedRelease, project, thisStage);
      releases.setAll(tempReleaseList);
      sprintReleaseCombo.getSelectionModel().select(selectedRelease);
    }
  }

  /**
   * A button which when clicked can add a release to the project.
   * Also adds to undo/redo stack so creation is undoable.
   * @param event Button click
   */
  @FXML
  protected void addNewRelease(ActionEvent event) {
    List<Release> tempReleaseList = new ArrayList<>(releases);
    mainApp.showReleaseDialogWithinSprint(CreateOrEdit.CREATE, null, project, thisStage);
    List<Release> tempNewReleaseList = new ArrayList<>();
    for (Release release : mainApp.getReleases()) {
      if (release.getProjectRelease().equals(project)) {
        tempNewReleaseList.add(release);
      }
    }
    for (Release release : tempNewReleaseList) {
      if (!tempReleaseList.contains(release)) {
        releases.setAll(tempNewReleaseList);
        sprintReleaseCombo.getSelectionModel().select(release);
        break;
      }
    }
  }

  /**
   * Returns the label of the backlog if a backlog is being edited.
   *
   * @return The label of the backlog as a string.
   */
  public String getLabel() {
    if (createOrEdit == CreateOrEdit.EDIT) {
      return sprint.getLabel();
    }
    return "";
  }
}
