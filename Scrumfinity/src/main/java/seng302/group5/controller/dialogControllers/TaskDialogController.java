package seng302.group5.controller.dialogControllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import seng302.group5.Main;
import seng302.group5.controller.enums.CreateOrEdit;
import seng302.group5.model.AgileController;
import seng302.group5.model.Effort;
import seng302.group5.model.Person;
import seng302.group5.model.Status;
import seng302.group5.model.Task;
import seng302.group5.model.Taskable;
import seng302.group5.model.Team;
import seng302.group5.model.undoredo.Action;
import seng302.group5.model.undoredo.CompositeUndoRedo;
import seng302.group5.model.undoredo.UndoRedo;
import seng302.group5.model.undoredo.UndoRedoObject;
import seng302.group5.model.util.TimeFormat;

/**
 * A controller to handle the creation or editing tasks. Tasks involve a label, description,
 * estimate (hours), status, and allocated people with their logged effort.
 *
 * @author Su-Shing Chen
 */
public class TaskDialogController implements AgileController {

  @FXML private TextField labelField;
  @FXML private TextArea descriptionField;
  @FXML private TextField estimateField;
  @FXML private TextField impedimentsField;
  @FXML private ComboBox<String> statusComboBox;
  @FXML private ListView<Person> availablePeopleList;
  @FXML private ListView<Person> allocatedPeopleList;
  @FXML private Button btnAddPerson;
  @FXML private Button btnRemovePerson;
  @FXML private HBox btnContainer;
  @FXML private TableView<Effort> effortTable;
  @FXML private TableColumn<Effort, String> dateTimeColumn;
  @FXML private TableColumn userColumn;
  @FXML private TableColumn<Effort, String> spentEffortColumn;
  @FXML private TableColumn commentColumn;
  @FXML private Button btnConfirm;
  @FXML private Button btnCancel;

  private Main mainApp;
  private Taskable taskable;
  private Stage thisStage;
  private CreateOrEdit createOrEdit;
  private Task task;
  private Task lastTask;
  private ObservableList<Person> availablePeople;
  private ObservableList<Person> allocatedPeople;
  private ObservableList<Person> originalPeople;
  private ObservableList<Effort> efforts = FXCollections.observableArrayList();
  private List<Effort> currentEfforts;

  private CompositeUndoRedo effortsUndoRedo;
  private UndoRedo undoRedoObject;

  /**
   * Sets up the controller on start up. Is called currently from sprints, stories and scrum board
   * but can be called from many places. The Taskable instance is used instead of Main compared to
   * other dialogs because Tasks cannot exist on their own, i.e. taskable.addTask() rather than
   * mainApp.addStory().
   *
   * @param mainApp The main class of the program. For managing undo/redo.
   * @param taskable The collection which will contain the task
   * @param team The team of the sprint which will contain the task
   * @param thisStage This is the window that will be displayed
   * @param createOrEdit This is an ENUM object to determine if creating or editing
   * @param task The object that will be edited (null if creating)
   */
  public void setupController(Main mainApp, Taskable taskable, Team team,
                              Stage thisStage, CreateOrEdit createOrEdit, Task task) {
    this.mainApp = mainApp;
    this.taskable = taskable;
    this.thisStage = thisStage;
    this.createOrEdit = createOrEdit;
    this.currentEfforts = new ArrayList<>();

    if (task != null) {
      this.task = task;
      this.lastTask = new Task(task);
      this.currentEfforts.addAll(task.getEfforts());
    } else {
      this.task = new Task();  // different because efforts
      this.lastTask = null;
    }

    String os = System.getProperty("os.name");

    if (!os.startsWith("Windows")) {
      btnContainer.getChildren().remove(btnConfirm);
      btnContainer.getChildren().add(btnConfirm);
    }

    initialiseLists(team);

    if (createOrEdit == CreateOrEdit.CREATE) {
      thisStage.setTitle("Create New Task");
      btnConfirm.setText("Create");

    } else if (createOrEdit == CreateOrEdit.EDIT) {
      thisStage.setTitle("Edit Task");
      btnConfirm.setText("Save");

      labelField.setText(task.getLabel());
      descriptionField.setText(task.getTaskDescription());
      estimateField.setText(TimeFormat.parseDuration(task.getTaskEstimation()));
      impedimentsField.setText(task.getImpediments());
      statusComboBox.setValue(Status.getStatusString(task.getStatus()));

      btnConfirm.setDisable(true);
    }

    btnConfirm.setDefaultButton(true);
    thisStage.setResizable(false);

    effortsUndoRedo = new CompositeUndoRedo("Edit Multiple Efforts");

    thisStage.setOnCloseRequest(event -> {
      mainApp.popControllerStack();
    });

    labelField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.trim().length() > 20) {
        labelField.setStyle("-fx-text-inner-color: red;");
      } else {
        labelField.setStyle("-fx-text-inner-color: black;");
      }
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });

    descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });

    estimateField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });

    impedimentsField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    });

    statusComboBox.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (createOrEdit == CreateOrEdit.EDIT) {
            checkButtonDisabled();
          }
        }
    );

    undoRedoObject = null;
    thisStage.getIcons().add(new Image("Thumbnail.png"));
  }

  /**
   * Checks if there are any changed fields and disables or enables the button accordingly
   */
  private void checkButtonDisabled() {
    if (labelField.getText().equals(task.getLabel()) &&
        descriptionField.getText().equals(task.getTaskDescription()) &&
        TimeFormat.parseMinutes(estimateField.getText()) == task.getTaskEstimation() &&
        impedimentsField.getText().equals(task.getImpediments()) &&
        effortTable.getItems().equals(currentEfforts) &&
        Status.getStatusEnum(statusComboBox.getValue()).equals(task.getStatus()) &&
        allocatedPeopleList.getItems().equals(originalPeople) &&
        effortsUndoRedo.getUndoRedos().isEmpty()) {
      btnConfirm.setDisable(true);
    } else {
      btnConfirm.setDisable(false);
    }
  }

  /**
   * Initialises the models lists including available people, allocated people with their logged
   * effort, available statuses, and sets the custom list cell of the allocated people list view.
   *
   * @param team The team for which team is initialized for.
   */
  private void initialiseLists(Team team) {
    availablePeople = FXCollections.observableArrayList();
    allocatedPeople = FXCollections.observableArrayList();
    originalPeople = FXCollections.observableArrayList();

    if (team != null) {
      availablePeople.addAll(team.getTeamMembers());
    }

    if (task != null && createOrEdit == CreateOrEdit.EDIT) {
      for (Person person : task.getTaskPeople()) {
        allocatedPeople.add(person);
        availablePeople.remove(person);

        originalPeople.add(person);
      }
    }

    availablePeopleList.setItems(availablePeople.sorted(Comparator.<Person>naturalOrder()));
    allocatedPeopleList.setItems(allocatedPeople.sorted(Comparator.<Person>naturalOrder()));

    ObservableList<String> availableStatuses = FXCollections.observableArrayList();
    for (Status status : Status.values()) {
      availableStatuses.add(Status.getStatusString(status));
    }
    statusComboBox.setItems(availableStatuses);
    statusComboBox.getSelectionModel().select(0);

    availablePeopleList.setCellFactory(listView -> new AvailablePersonCell());
    allocatedPeopleList.setCellFactory(listView -> new PersonEffortCell());
    effortTable.setRowFactory(tableView -> new EffortRow());

    dateTimeColumn.setCellValueFactory(row -> {
      Effort effort = row.getValue();
      LocalDateTime dateTime = effort.getDateTime();
      String result = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm"));
      return new ReadOnlyObjectWrapper<>(result);
    });
    spentEffortColumn.setCellValueFactory(row -> {
      Effort effort = row.getValue();
      int spentEffort = effort.getSpentEffort();
      String result = TimeFormat.parseDuration(spentEffort);
      return new ReadOnlyObjectWrapper<>(result);
    });

    updateEffortTable();
  }

  /**
   * Updates the tableview of efforts.
   */
  public void updateEffortTable() {
    efforts.setAll(task.getEfforts());
    effortTable.setItems(efforts);
    allocatedPeopleList.setCellFactory(listView -> new PersonEffortCell());
    if (createOrEdit == CreateOrEdit.EDIT) {
      checkButtonDisabled();
    }
  }

  /**
   * Allocate a team member to the task.
   *
   * @param event Action event.
   */
  @FXML
  protected void btnAddPersonClick(ActionEvent event) {
    Person selectedPerson = availablePeopleList.getSelectionModel().getSelectedItem();
    if (selectedPerson != null) {
      allocatedPeople.add(selectedPerson);
      availablePeople.remove(selectedPerson);

      allocatedPeopleList.getSelectionModel().select(selectedPerson);
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    }
  }

  /**
   * Deallocate a team member from the task.
   *
   * @param event Action event.
   */
  @FXML
  protected void btnRemovePersonClick(ActionEvent event) {
    Person selectedPerson = allocatedPeopleList.getSelectionModel().getSelectedItem();
    if (selectedPerson != null) {
      boolean found = false;
      for (Effort effort : task.getEfforts()) {
        if (effort.getWorker().equals(selectedPerson)) {
         found = true;
        }
      }
      if (found) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Person has logged effort");
        alert.setHeaderText(null);
        String message = "You can not remove this person from the task because they have already "
                         + "logged some effort against it.";
        alert.getDialogPane().setPrefHeight(150);
        alert.setContentText(message);
        //checks response
        alert.showAndWait();
      } else {
        availablePeople.add(selectedPerson);
        allocatedPeople.remove(selectedPerson);

        availablePeopleList.getSelectionModel().select(selectedPerson);
      }
      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    }
  }

  /**
   * Generate an UndoRedoObject to represent a task create or edit action and store it globally.
   * This is so a cancel in a dialog higher in the hierarchy can undo the changes made to the task.
   */
  private void generateUndoRedoObject() {
    Action action = Action.UNDEFINED;
    UndoRedoObject taskChanges = new UndoRedoObject();

    if (createOrEdit == CreateOrEdit.CREATE) {
      action = Action.TASK_CREATE;
      taskChanges = new UndoRedoObject();
      taskChanges.setAction(action);
      taskChanges.addDatum(new Task(task));
      taskChanges.addDatum(taskable);

      // Store a copy of task to create in object to avoid reference problems
      taskChanges.setAgileItem(task);

    } else if (createOrEdit == CreateOrEdit.EDIT) {
      action = Action.TASK_EDIT;
      taskChanges = new UndoRedoObject();
      taskChanges.setAction(action);
      taskChanges.addDatum(lastTask);

      // Store a copy of task to edit in object to avoid reference problems
      taskChanges.setAgileItem(task);
      Task taskToStore = new Task(task);
      taskChanges.addDatum(taskToStore);
    }

    // Create composite undo/redo with original action string to handle task and effort changes
    CompositeUndoRedo taskAndEffortChanges = new CompositeUndoRedo(Action.getActionString(action));
    taskAndEffortChanges.addUndoRedo(taskChanges);
    for (UndoRedo effortChange : effortsUndoRedo.getUndoRedos()) {
      // only include edits to avoid doubling efforts
      if (effortChange.getAction().equals(Action.EFFORT_EDIT)) {
        taskAndEffortChanges.addUndoRedo(effortChange);
      }
    }

    // Set the global value to be the generated composite undo/redo object
    undoRedoObject = taskAndEffortChanges;
  }

  /**
   * Handles the action of clicking the confirm button. It parses the values that were input
   * into the dialog fields and updates or creates the task it is looking at. It creates
   * the UndoRedoObject for the changes made but does not force it onto the stack in
   * UndoRedoHandler. It is up to the object that is using the dialog to use it by accessing
   * it using the getter.
   *
   * @param event Action event
   */
  @FXML
  protected void btnConfirmClick(ActionEvent event) {
    StringBuilder errors = new StringBuilder();
    int noErrors = 0;

    String taskLabel = "";
    String taskDescription = descriptionField.getText().trim();
    int taskEstimateMinutes;
    String taskImpediments = impedimentsField.getText().trim();
    Status taskStatus = Status.getStatusEnum(statusComboBox.getValue());

    try {
      taskLabel = parseTaskLabel(labelField.getText());
    } catch (Exception e) {
      noErrors++;
      errors.append(String.format("%s\n", e.getMessage()));
    }

    taskEstimateMinutes = TimeFormat.parseMinutes(estimateField.getText());
    if (taskEstimateMinutes < 0) {
      noErrors++;
      errors.append(String.format("%s\n", "Invalid estimate time format (e.g. 1h30m)."));
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
      alert.getDialogPane().setPrefHeight(60 + 30 * noErrors);
      alert.setContentText(errors.toString());
      alert.showAndWait();
    } else {
      List<Person> allocatedPeopleSorted = new ArrayList<>();
      for (Person person : allocatedPeopleList.getItems()) {
        allocatedPeopleSorted.add(person);
      }
      if (createOrEdit == CreateOrEdit.CREATE) {
        task.setLabel(taskLabel);
        task.setTaskDescription(taskDescription);
        task.setTaskEstimation(taskEstimateMinutes);
        task.setImpediments(taskImpediments);
        task.setStatus(taskStatus);
        task.addAllTaskPeople(allocatedPeopleSorted);
        taskable.addTask(task);
      } else if (createOrEdit == CreateOrEdit.EDIT) {
        task.setLabel(taskLabel);
        task.setTaskDescription(taskDescription);
        task.setTaskEstimation(taskEstimateMinutes);
        task.setImpediments(taskImpediments);
        task.setStatus(taskStatus);
        task.removeAllTaskPeople();
        task.addAllTaskPeople(allocatedPeopleSorted);
      }
      generateUndoRedoObject();
      mainApp.popControllerStack();
      thisStage.close();
    }
  }

  /**
   * Close the dialog and remove all applied changes
   *
   * @param event Action event
   */
  @FXML
  protected void btnCancelClick(ActionEvent event) {
    Alert alert = null;

    if ((createOrEdit == CreateOrEdit.CREATE && !efforts.isEmpty()) ||
        (createOrEdit == CreateOrEdit.EDIT && (!efforts.equals(task.getEfforts()) ||
                                               !effortsUndoRedo.getUndoRedos().isEmpty()))) {

      alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Changes have been made to the task's efforts");
      alert.setHeaderText(null);
      String message = "By cancelling this dialog you will lose all changes you have made "
                       + "to efforts since this task dialog was opened. Are you sure you wish "
                       + "to continue?";
      alert.getDialogPane().setPrefHeight(170);
      alert.setContentText(message);
      //checks response
      alert.showAndWait();
    }
    if (alert == null || alert.getResult() == ButtonType.OK) {
      if (createOrEdit == CreateOrEdit.EDIT) {
        mainApp.refreshList(null);
      }
      // undo all editing of existing efforts made within this dialog
      mainApp.quickUndo(effortsUndoRedo);
      mainApp.popControllerStack();
      thisStage.close();
    }
  }

  /**
   * Parse a task label to make sure the label is not empty and unique within the task collection
   *
   * @param inputTaskLabel Task label from entry field.
   * @return Task label if label is valid.
   * @throws Exception Any invalid input.
   */
  private String parseTaskLabel(String inputTaskLabel) throws Exception {
    inputTaskLabel = inputTaskLabel.trim();

    if (inputTaskLabel.isEmpty()) {
      throw new Exception("Task label is empty.");
    } else {
      String lastTaskLabel;
      if (lastTask == null) {
        lastTaskLabel = "";
      } else {
        lastTaskLabel = lastTask.getLabel();
      }
      for (Task task : taskable.getTasks()) {
        String taskLabel = task.getLabel();
        if (taskLabel.equalsIgnoreCase(inputTaskLabel) &&
            !taskLabel.equalsIgnoreCase(lastTaskLabel)) {
          throw new Exception("Task label is not unique within the object.");
        }
      }
      return inputTaskLabel;
    }
  }

  /**
   * Get the UndoRedoObject representing the creating or editing of the task. Use this as a return
   * value of the dialog.
   *
   * @return The UndoRedo representing the successful task edit.
   */
  public UndoRedo getUndoRedoObject() {
    return undoRedoObject;
  }

  /**
   * Shows the dialog used for logging effort against a task.
   * @param createOrEdit Whether the dialog is for creating or editing effort.
   * @param effort The effort to edit (null if creating).
   */
  public UndoRedo showEffortDialog(CreateOrEdit createOrEdit, Effort effort) {
    UndoRedo effortUndoRedo = null;
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/LoggingEffortDialog.fxml"));
      VBox effortDialogLayout = loader.load();

      EffortDialogController controller = loader.getController();
      Scene effortDialogScene = new Scene(effortDialogLayout);
      Stage effortDialogStage = new Stage();

      List<Person> allocated = new ArrayList<>();
      for (Person person : allocatedPeople) {
        allocated.add(person);
      }
      controller.setupController(this, task, allocated, effortDialogStage, createOrEdit, effort);

      effortDialogStage.initModality(Modality.APPLICATION_MODAL);
      effortDialogStage.initOwner(thisStage);
      effortDialogStage.setScene(effortDialogScene);
      effortDialogStage.showAndWait();

      effortUndoRedo = controller.getUndoRedoObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return effortUndoRedo;
  }

  /**
   * Calls the method to open the effort dialog to log some new effort.
   * @param event The event generated by the listener.
   */
  @FXML
  private void btnAddEffortClick(ActionEvent event) {
    UndoRedo effortCreate = showEffortDialog(CreateOrEdit.CREATE, null);
    if (effortCreate != null) {
      effortsUndoRedo.addUndoRedo(effortCreate);
      updateEffortTable();

      if (createOrEdit == CreateOrEdit.EDIT) {
        checkButtonDisabled();
      }
    }
  }

  /**
   * Removes the selected effort from the task.
   * @param event The event generated by the listener.
   */
  @FXML
  private void btnRemoveEffortClick(ActionEvent event) {
    Effort selectedEffort = effortTable.getSelectionModel().getSelectedItem();
    if (selectedEffort != null) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Are you sure you want to remove effort?");
      alert.setHeaderText(null);
      String message = "You are trying to remove effort that has already been logged against this"
                       + " task. Are you sure you want to do this?";
      alert.getDialogPane().setPrefHeight(150);
      alert.setContentText(message);
      alert.showAndWait();
      if (alert.getResult() == ButtonType.OK) {
        UndoRedo effortDelete = new UndoRedoObject();
        effortDelete.setAction(Action.EFFORT_DELETE);
        effortDelete.addDatum(new Effort(selectedEffort));
        effortDelete.addDatum(task);
        // Store a copy of deleted effort in object to avoid reference problems
        effortDelete.setAgileItem(selectedEffort);

        effortsUndoRedo.addUndoRedo(effortDelete);

        task.removeEffort(selectedEffort);
        updateEffortTable();

        if (createOrEdit == CreateOrEdit.EDIT) {
          checkButtonDisabled();
        }
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
      return task.getLabel();
    }
    return "";
  }

  /**
   * List cell in the available person list.
   */
  private class AvailablePersonCell extends TextFieldListCell<Person> {

    public AvailablePersonCell() {
      super();

      // double click for editing
      this.setOnMouseClicked(click -> {
        if (click.getClickCount() == 2 &&
            click.getButton() == MouseButton.PRIMARY &&
            !isEmpty()) {
          Person selectedPerson = getItem();
          mainApp.showPersonDialogNested(selectedPerson,null,thisStage);
          availablePeople.remove(selectedPerson);
          availablePeople.add(selectedPerson);
          availablePeopleList.getSelectionModel().select(selectedPerson);
        }
      });
    }
  }

  /**
   * List cell to combine a person with their logged effort in the allocated person list.
   */
  private class PersonEffortCell extends TextFieldListCell<Person> {

    private Label effortField;
    private Label cellText;
    private double labelWidth;
    private GridPane pane;

    public PersonEffortCell() {
      super();

      cellText = new Label();
      effortField = new Label();
      effortField.setMaxWidth(55);
      labelWidth = allocatedPeopleList.getLayoutBounds().getWidth() - 76;
      pane = new GridPane();
      pane.getColumnConstraints().add(new ColumnConstraints(labelWidth));
      pane.setHgap(5);
      pane.add(cellText, 0, 0);
      pane.add(effortField, 1, 0);
      cellText.setStyle("-fx-text-fill: black");
      effortField.setStyle("-fx-text-fill: black");

      // double click for editing
      this.setOnMouseClicked(click -> {
        if (click.getClickCount() == 2 &&
            click.getButton() == MouseButton.PRIMARY &&
            !isEmpty()) {
          Person selectedPerson = getItem();
          mainApp.showPersonDialogNested(selectedPerson,null,thisStage);
          allocatedPeople.remove(selectedPerson);
          allocatedPeople.add(selectedPerson);
          allocatedPeopleList.getSelectionModel().select(selectedPerson);
        }
      });
    }

    /**
     * Sets the overridden parameters for the PersonEffortCell when the cell is updated.
     * @param person The Person being added to the cell with their effort.
     * @param empty Whether or not string is empty as a boolean flag.
     */
    @Override
    public void updateItem(Person person, boolean empty) {
      super.updateItem(person, empty);

      if (empty) {
        cellText.setText(null);
        setText(null);
        setGraphic(null);
      } else {
        cellText.setText(person.toString());
        effortField.setText(TimeFormat.parseDuration(task.getPersonEffort(person)));
        setText(null);
        setGraphic(pane);
      }
    }
  }

  /**
   * Allow double clicking for editing an Effort.
   */
  private class EffortRow extends TableRow<Effort> {

    public EffortRow() {
      super();
      this.setOnMouseClicked(click -> {
        if (click.getClickCount() == 2 &&
            click.getButton() == MouseButton.PRIMARY &&
            !isEmpty()) {
          UndoRedo effortEdit = showEffortDialog(CreateOrEdit.EDIT, getItem());
          if (effortEdit != null) {
            effortsUndoRedo.addUndoRedo(effortEdit);
            updateEffortTable();
            effortTable.getSelectionModel().select(getItem());
            if (createOrEdit == CreateOrEdit.EDIT) {
              checkButtonDisabled();
            }
          }
        }
      });
    }
  }
}
