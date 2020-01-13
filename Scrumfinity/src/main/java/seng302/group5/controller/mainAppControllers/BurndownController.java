package seng302.group5.controller.mainAppControllers;

import java.time.LocalDate;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seng302.group5.Main;
import seng302.group5.model.Backlog;
import seng302.group5.model.Effort;
import seng302.group5.model.Sprint;
import seng302.group5.model.Status;
import seng302.group5.model.Story;
import seng302.group5.model.Task;

/**
 * Created by Craig Barnard on 10/09/2015.
 */
public class BurndownController {

  @FXML private ComboBox<Backlog> backlogCombo;
  @FXML private ComboBox<Sprint> sprintCombo;
  @FXML private LineChart<LocalDate, Integer> burndownChart;
  @FXML private VBox holder;


  private XYChart.Series<LocalDate, Integer> aSeries;
  private XYChart.Series<LocalDate, Integer> bSeries;
  private XYChart.Series<LocalDate, Integer> cSeries;

  private Main mainApp;
  private Stage stage;

  private ObservableList<Sprint> availableSprints;
  private ObservableList<Effort> allEffort;
  private ObservableList<Backlog> availableBacklogs;
  private ObservableList<Task> doneTasks;
  private ObservableList<Task> tasks;
  private int flag = 0;

  private Sprint sprint;
  private Integer time;

  //TODO Javadoc

  /**
   * Sets up the burndown controller.
   *
   * @param mainApp the mainApp that contains all backlogs and sprints
   * @param stage the stage.
   */
  public void setupController(Main mainApp, Stage stage) {
    this.mainApp = mainApp;
    this.stage = stage;
    sprintCombo.setDisable(true);
    burndownChart.setPrefWidth(stage.getWidth());
    burndownChart.setAnimated(false);
    initialiseLists();
  }

  public void initialise() {
    initialiseLists();
  }

  /**
   * initialise the lists with the available backlogs and sprints.
   */
  private void initialiseLists() {
    aSeries = new XYChart.Series<LocalDate, Integer>();
    bSeries = new XYChart.Series<LocalDate, Integer>();
    cSeries = new XYChart.Series<LocalDate, Integer>();

    availableSprints = FXCollections.observableArrayList();
    availableBacklogs = FXCollections.observableArrayList();
    allEffort = FXCollections.observableArrayList();
    tasks = FXCollections.observableArrayList();
    doneTasks = FXCollections.observableArrayList();
    availableBacklogs.addAll(this.mainApp.getBacklogs());

    backlogCombo.getSelectionModel().clearSelection();
    sprintCombo.getSelectionModel().clearSelection();
    backlogCombo.setItems(mainApp.getBacklogs());
    burndownChart.setMinWidth(stage.getWidth() - 200);
    burndownChart.setMinHeight(stage.getHeight() - 200);
    burndownChart.setTitle("");

    backlogCombo.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldBacklog, newBacklog) -> {
          if (backlogCombo.getSelectionModel().getSelectedItem() != null) {
            bSeries.getData().clear();
            cSeries.getData().clear();
            aSeries.getData().clear();
            availableSprints.clear();
            doneTasks.clear();
            tasks.clear();
            allEffort.clear();
            time = 0;
            burndownChart.setTitle("");
            burndownChart.getData().clear();
            sprintCombo.setDisable(false);
            // get backlog's sprints
            for (Sprint sprint : mainApp.getSprints()) {
              if (backlogCombo.getValue().equals(sprint.getSprintBacklog())) {
                if (!availableSprints.contains(sprint)) {
                  availableSprints.add(sprint);
                }
              }
            }
            sprintCombo.setItems(null);
            sprintCombo.setItems(availableSprints);
            //sprintCombo.setDisable(true);
          }
        }
    );

    // Resizes the linecharts height when the main stage is resized.
    stage.heightProperty().addListener((observable, oldValue, newValue) -> {
      burndownChart.setMinHeight(stage.getHeight() - 200);
    });

    // Resizes the linecharts width when the main stage is resized.
    stage.widthProperty().addListener((observable, oldValue, newValue) -> {
      burndownChart.setMinWidth(stage.getWidth() - 210);
    });

    sprintCombo.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldSprint, newSprint) -> {

          if (newSprint != null) {
            allEffort.clear();
            tasks.clear();
            aSeries.getData().clear();
            bSeries.getData().clear();
            cSeries.getData().clear();
            doneTasks.clear();
            sprint = sprintCombo.getValue();
            tasks.addAll(sprint.getTasks());
            burndownChart.setTitle(newSprint.toString() + " - Velocity: " +
            mainApp.getLMPC().sprintVelocity(newSprint));
            for (Story story : newSprint.getSprintStories()) {
              if (mainApp.getStories().contains(story)) {
                tasks.addAll(story.getTasks());
              }
            }
            time = 0;
            for (Task task : tasks.sorted()) {
              time += task.getTaskEstimation();
              if (task.getEfforts() != null) {
                allEffort.addAll(task.getEfforts());
              }
              if (task.getStatus().equals(Status.DONE)) {
                doneTasks.add(task);
              }
            }
            //hours
            time = time / 60;
            burndownChart.autosize();
            burndownChart.getData().clear();
            burndownChart.setData(getChartData(time));
          }
        }
    );

  }

  /**
   * Plots the data for the burndown chart, takes in the total time estimate and returns an
   * array of series charts.
   * @param time the total time estimated for the sprint.
   */
  private ObservableList<XYChart.Series<LocalDate, Integer>> getChartData(Integer time) {
    int days = 0;
    double timeDiff = 0;

    ObservableList<XYChart.Series<LocalDate, Integer>> answer = FXCollections.observableArrayList();
    answer.clear();
    answer.removeAll();
    aSeries.getData().clear();
    bSeries.getData().clear();
    cSeries.getData().clear();

    aSeries.setName("Reference Velocity");
    bSeries.setName("Burn-Down");
    cSeries.setName("Burn-Up");
    LocalDate date = sprint.getSprintStart();
    LocalDate date1 = sprint.getSprintEnd();
    LocalDate date2 = sprint.getSprintStart();

    if (date.getYear() == date1.getYear()) {
      days = date1.getDayOfYear() - date.getDayOfYear();
    } else {
      days = (365-date.getDayOfYear()) + date1.getDayOfYear();
    }
    Double i = time+ 0.0;
    if (days > 0) {
      timeDiff = (time+0.0) / days;
    }
    else if (days < 0) {
      days = days* -1;
      timeDiff = (time+ 0.0) / days;
    }

    if (timeDiff != 0) {
      int burnUp = 0;
      for (Integer day = days; day >= 0; --day) {

        aSeries.getData().add(new XYChart.Data(date2.toString(), i));
        i = i - timeDiff;
        for (Effort effort : allEffort) {
          if (effort.getDateTime().getDayOfYear() == date2.getDayOfYear()) {
            cSeries.getData().add(new XYChart.Data(date2.toString(), burnUp));
            burnUp += (effort.getSpentEffort() / 60);
          }
        }
        for (Task doneTask : doneTasks.sorted()) {
          if (doneTask.getDoneDate().getDayOfYear() == date2.getDayOfYear()) {
            time -= (doneTask.getTaskEstimation() / 60);
            bSeries.getData().add(new XYChart.Data(date2.toString(), time));
          }
        }
        date2 = date2.plusDays(1);
      }
    }
    answer.addAll(aSeries, bSeries, cSeries);

    return answer;
  }
  /**
   * Refresh the comboboxes whenever objects are modified in the main app
   */
  public void refreshComboBoxes() {
    Backlog backlog = backlogCombo.getValue();
    backlogCombo.getSelectionModel().clearSelection();
    Sprint sprint = sprintCombo.getValue();
    sprintCombo.getSelectionModel().clearSelection();
    tasks.clear();
    allEffort.clear();
    doneTasks.clear();
    burndownChart.getData().clear();
    initialiseLists();
    burndownChart.setTitle(null);
    sprintCombo.setDisable(true);
    initialiseLists();

    if (mainApp.getBacklogs().contains(backlog)) {
      backlogCombo.setValue(backlog);

      if(availableSprints.contains(sprint)){
        sprintCombo.setValue(sprint);
        sprintCombo.setDisable(false);
        burndownChart.setTitle(sprint.getLabel() + " - Velocity " +
        mainApp.getLMPC().sprintVelocity(sprint));
      } else {
        sprintCombo.setValue(null);
      }
    } else {
      backlogCombo.setValue(null);
      sprintCombo.setValue(null);
    }
  }

  /**
   * Refreshes the selections of the combo boxes
   */
  public void hardReset() {
    sprintCombo.getSelectionModel().clearSelection();
    sprintCombo.getItems().clear();
    sprintCombo.setDisable(true);
    availableSprints.clear();
    backlogCombo.getSelectionModel().clearSelection();
    backlogCombo.setItems(mainApp.getBacklogs());
    burndownChart.setTitle("");
    burndownChart.getData().clear();
    initialiseLists();
    sprintCombo.setDisable(true);
  }
}
