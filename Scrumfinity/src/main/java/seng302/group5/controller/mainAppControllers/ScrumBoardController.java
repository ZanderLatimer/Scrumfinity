package seng302.group5.controller.mainAppControllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seng302.group5.Main;
import seng302.group5.model.Backlog;
import seng302.group5.model.Sprint;
import seng302.group5.model.Story;

/**
 * Scrum board controller completely redone. Now using accordions to make things prettier. First
 * controller create which contains a list of other controller objects (a controller for each story
 * to be displayed in the accordion view).
 */
public class ScrumBoardController {

  @FXML
  private ComboBox<Sprint> sprintCombo;
  @FXML
  private ComboBox<Backlog> backlogCombo;
  @FXML
  private VBox storiesBox;
  @FXML
  private Button btnDeleteTask;
  @FXML
  private Button btnNewTask;
  @FXML
  private ScrollPane scrollPane;


  private Main mainApp;
  private Stage stage;
  private Story fakeStory;

  private Sprint prevSprint;
  private List<StoryItemController> oldPanes;

  private List<Sprint> oldSprints;

  private ObservableList<Sprint> availableSprints;
  private ObservableList<Story> availableStories;
  private List<StoryItemController> storyPanes;
  private List<String> openedTabs;

  @FXML
  private void initialize() {
  }

  /**
   * This function sets up the scrum board dialog controller.
   *
   * @param mainApp The main application object
   * @param stage   The stage the application is in.
   */
  public void setupController(Main mainApp, Stage stage) {
    this.mainApp = mainApp;
    this.stage = stage;
    storyPanes = new ArrayList<>();
    openedTabs = new ArrayList<>();
    oldPanes = new ArrayList<>();
    prevSprint = new Sprint();
    initialiseLists();
  }

  /**
   * Initializes the controller and sets the listeners to the combo boxes to update and reload
   * controllers as necessary. Changing backlog combo refreshes sprint combo. Changing sprint combo
   * refreshes list of controllers.
   */
  private void initialiseLists() {
    availableSprints = FXCollections.observableArrayList();
    availableStories = FXCollections.observableArrayList();

    backlogCombo.getSelectionModel().clearSelection();
    backlogCombo.setItems(mainApp.getBacklogs());

    backlogCombo.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldBacklog, newBacklog) -> {
          if (newBacklog != null) {
            sprintCombo.setDisable(false);
            // get backlog's sprints
            availableSprints.setAll(mainApp.getSprints().stream()
                                        .filter(
                                            sprint -> sprint.getSprintBacklog().equals(newBacklog))
                                        .collect(Collectors.toList()));
            sprintCombo.setItems(null);
            sprintCombo.setItems(availableSprints);
            sprintCombo.setValue(null);

            availableStories.clear();
            storiesBox.getChildren().setAll(FXCollections.observableArrayList());
            storyPanes.clear();
          }
        }
    );

    // Resizes the scrumboards width when the main stage is resized.
    stage.widthProperty().addListener((observable, oldValue, newValue) -> {
      storiesBox.setMinWidth(stage.getWidth() - 230);
    });

    sprintCombo.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldSprint, newSprint) -> {
          if (newSprint != null) {
            // Quick check if old label is same to clear opened tabs
            if (newSprint.getLabel() != prevSprint.getLabel()) {
              openedTabs.clear();
            }

            // Clear old values
            availableStories.clear();
            storiesBox.getChildren().setAll(FXCollections.observableArrayList());
            storyPanes.clear();

            // Repopulate available stories
            fakeStory = new Story();
            fakeStory.setLabel("Non-story Tasks");
            fakeStory.addAllTasks(newSprint.getTasks());
            availableStories.add(fakeStory);
            // ***************************************************************
            for (Story story : newSprint.getSprintStories()) {
              if (mainApp.getStories().contains(story)) {
                availableStories.add(story);
              }
            }

            // Generate controllers for stories
            for (Story story : availableStories) {
              StoryItemController paneController = checkController(story);
              if (paneController != null) {
                storyPanes.add(paneController);
                if (openedTabs.contains(paneController.getPaneName())) {
                  paneController.expandTab();
                }
              }
            }

            // Update the copy of available panes
            oldPanes.clear();
            oldPanes.addAll(storyPanes);
            prevSprint = newSprint;
          }
        }
    );
  }

  /**
   * Loads the StoryItem FXML and returns a StoryItemController if successful, or null if failed.
   *
   * @param story Story object which holds the data
   * @return The created controller
   */
  private StoryItemController createStoryPane(Story story, VBox storiesBox) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(StoryItemController.class.getResource("/StoryItem.fxml"));
      TitledPane accordionPane = loader.load();

      StoryItemController controller = loader.getController();
      Accordion storyAccordion = new Accordion();
      storyAccordion.setPrefHeight(Region.USE_COMPUTED_SIZE);
      storyAccordion.getPanes().add(accordionPane);
      storiesBox.getChildren().add(storyAccordion);
      controller.setupController(story, mainApp, storyAccordion, sprintCombo.getValue());
      return controller;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }


  /**
   * * Checks if the controller already exists, if so, returns it instead of recreating it
   * @param story The story that controller is displaying
   * @return The story pane
   */
  private StoryItemController checkController(Story story) {
    // Check if story inside sprint clone. If not make a controller.
    if (prevSprint.getSprintStories().contains(story)) {
      for (StoryItemController pane : oldPanes) {
        if (pane.getStory().equals(story)) {
          Accordion storyAccordion = new Accordion();
          storyAccordion.getPanes().add(pane.getTitledPane());
          storiesBox.getChildren().add(storyAccordion);
          pane.setAccordion(storyAccordion);
          pane.setupLists();
          return pane;
        }
      }
      return createStoryPane(story, storiesBox);
    } else {
      return createStoryPane(story, storiesBox);
    }
  }

  /**
   * Resets everything about the scrum board, everything cleared and disabled apart from Backlog
   * combo box.
   */
  public void refreshComboBoxes() {
    Backlog backlog = backlogCombo.getValue();
    Sprint sprint = sprintCombo.getValue();

    backlogCombo.setItems(FXCollections.observableArrayList());
    backlogCombo.setItems(mainApp.getBacklogs());
    backlogCombo.getSelectionModel().clearSelection();
    backlogCombo.getSelectionModel().select(null);
    sprintCombo.setItems(FXCollections.observableArrayList());
    sprintCombo.getSelectionModel().clearSelection();
    sprintCombo.getSelectionModel().select(null);

    // The pane stuff
    openedTabs = new ArrayList<>();
    for (StoryItemController titledPane : storyPanes) {
      if (titledPane.checkIfOpened()) {
        openedTabs.add(titledPane.getPaneName());
      }
    }

    storiesBox.getChildren().setAll(FXCollections.observableArrayList());
    storyPanes.clear();
    sprintCombo.setDisable(true);

    if (backlog != null &&
        sprint != null) {
      if (mainApp.getBacklogs().contains(backlog)) {
        backlogCombo.setValue(backlog);

        if (availableSprints.contains(sprint)) {
          sprintCombo.setValue(sprint);
        } else {
          sprintCombo.setValue(null);
          availableSprints.clear();
          storiesBox.getChildren().setAll(FXCollections.observableArrayList());
          availableStories.clear();
          storyPanes.clear();
          openedTabs.clear();
          availableSprints.addAll(mainApp.getSprints().stream()
                                      .filter(
                                          sprintz -> sprintz.getSprintBacklog()
                                              .equals(backlog))
                                      .collect(Collectors.toList()));
          sprintCombo.setItems(availableSprints);
          sprintCombo.getSelectionModel().clearSelection();
          sprintCombo.getSelectionModel().select(null);
        }
      } else {
        backlogCombo.setValue(null);
        availableSprints.clear();
        storiesBox.getChildren().setAll(FXCollections.observableArrayList());
        availableStories.clear();
        storyPanes.clear();
        openedTabs.clear();
        availableSprints.addAll(mainApp.getSprints().stream()
                                    .filter(
                                        sprintz -> sprintz.getSprintBacklog()
                                            .equals(backlog))
                                    .collect(Collectors.toList()));
        sprintCombo.setItems(availableSprints);
        sprintCombo.setValue(null);
        sprintCombo.getSelectionModel().clearSelection();
        sprintCombo.getSelectionModel().select(null);

      }
    } else {
      hardReset();
    }
  }

  /**
   * Refreshes the selections of the combo boxes
   */
  public void hardReset() {
    storiesBox.getChildren().setAll(FXCollections.observableArrayList());
    availableSprints.clear();
    availableStories.clear();
    storyPanes.clear();
    openedTabs.clear();
    oldPanes.clear();
    prevSprint = new Sprint();

    backlogCombo.setItems(FXCollections.observableArrayList());
    backlogCombo.getItems().addAll(mainApp.getBacklogs());
    backlogCombo.setValue(null);
    backlogCombo.getSelectionModel().select(null);
    sprintCombo.setValue(null);
    sprintCombo.getSelectionModel().select(null);
    sprintCombo.setDisable(true);
  }

  /**
   * Does not refresh the scrum board, just refreshes all the lists of the story items
   */
  public void refreshTaskLists() {
    for (StoryItemController controller : storyPanes) {
      controller.setupLists();
    }
  }

  public Stage getStage() {
    return stage;
  }
}
