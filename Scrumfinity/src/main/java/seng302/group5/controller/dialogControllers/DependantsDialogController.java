package seng302.group5.controller.dialogControllers;

import java.util.ArrayList;
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
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import seng302.group5.Main;
import seng302.group5.model.Backlog;
import seng302.group5.model.Story;
import seng302.group5.model.undoredo.Action;
import seng302.group5.model.undoredo.CompositeUndoRedo;
import seng302.group5.model.undoredo.UndoRedo;
import seng302.group5.model.undoredo.UndoRedoObject;

/**
 * A controller for the dependency dialog which shows the dependencies between stories in
 * the program. It can modify dependencies of any story in the main application. Intended to only
 * be used via the launchDependantsDialog function in the MenuBarController.
 *
 * Created by Michael and Shingy on 22/7/2015.
 */
public class DependantsDialogController {

  private Main mainApp;
  private Stage thisStage;
  private Story story;
  private Map<String, Story> syncMap;
  private Map<String, Story> cloneMap;
  private List<Story> clones;

  private ObservableList<Story> allStories;
  private ObservableList<Story> availableStories;
  private ObservableList<Story> dependantStories;

  private Set<Story> visitedStories;

  // for undo/redo
  private Map<Story, Story> beforeMap;
  private Map<Story, Story> afterMap;

  @FXML private ListView<Story> allStoriesList;
  @FXML private ListView<Story> availableStoriesList;
  @FXML private ListView<Story> dependantStoriesList;
  @FXML private Button btnAddStory;
  @FXML private Button btnRemoveStory;
  @FXML private HBox btnContainer;
  @FXML private Button btnConfirm;
  @FXML private Button btnCancel;


  /**
   * Setup the dependency dialog controller for stories. Sets up model collections using data
   * from main.
   *
   * @param mainApp Our amazing god class.
   * @param thisStage The stage of the dialog.
   * @param story Active story to edit dependencies of, which can be null.
   */
  public void setupController(Main mainApp, Stage thisStage, Story story) {
    this.mainApp = mainApp;
    this.thisStage = thisStage;
    this.story = story;
    clones = new ArrayList<>();
    syncMap = new IdentityHashMap<>();
    cloneMap = new IdentityHashMap<>();
    beforeMap = new IdentityHashMap<>();
    afterMap = new IdentityHashMap<>();
    btnConfirm.setDisable(true);

    String os = System.getProperty("os.name");

    if (!os.startsWith("Windows")) {
      btnContainer.getChildren().remove(btnConfirm);
      btnContainer.getChildren().add(btnConfirm);
    }

    for (Story origStory : mainApp.getStories()) {
      Story clonedStory = new Story(origStory);
      clones.add(clonedStory);
      syncMap.put(clonedStory.getLabel(), origStory);
      cloneMap.put(clonedStory.getLabel(), clonedStory);

      beforeMap.put(origStory, new Story(origStory)); // put fresh clone in map for before the edit
    }
    // Makes the clones's dependency stories also clones.
    for (Story clone : clones) {
      List<Story> tempList = new ArrayList<>();
      for (Story cloneDep : clone.getDependencies()) {
        tempList.add(cloneMap.get(cloneDep.getLabel()));
      }
      clone.removeAllDependencies();
      clone.addAllDependencies(tempList);
    }

    // Populate up in here
    allStories = FXCollections.observableArrayList();
    availableStories = FXCollections.observableArrayList();
    dependantStories = FXCollections.observableArrayList();

    // Set the views
    allStories.setAll(clones);
    allStoriesList.setItems(allStories);
    availableStoriesList.setItems(availableStories);
    dependantStoriesList.setItems(dependantStories);

    allStoriesList.getSelectionModel().select(0);

    btnConfirm.setDefaultButton(true);
    initialiseLists();
    refreshLists();
    thisStage.getIcons().add(new Image("Thumbnail.png"));
  }

  /**
   * Sets up listeners to enable click selection on all stories and double click
   * to (un)assign between available stories and dependent stories respectively
   */
  private void initialiseLists() {
    allStoriesList.getSelectionModel().clearSelection();
    allStoriesList.getSelectionModel().select(0);
    allStoriesList.setOnMouseClicked(mouseEvent -> {
      if (mouseEvent.getButton().equals(MouseButton.PRIMARY) &&
          allStoriesList.getSelectionModel().getSelectedItem() != null) {
        story = allStoriesList.getSelectionModel().getSelectedItem();
        refreshLists();
    }});
    if (allStoriesList.getSelectionModel().getSelectedItem() != null) {
      this.story = allStoriesList.getSelectionModel().getSelectedItem();
    }
/*    availableStoriesList.setOnMouseClicked(mouseEvent -> {
      if (mouseEvent.getButton().equals(MouseButton.PRIMARY) &&
          mouseEvent.getClickCount()%2 == 0 &&
          availableStoriesList.getSelectionModel().getSelectedItem() != null) {
        btnAddStoryClick(new ActionEvent());
        refreshLists();
      }
    });
    dependantStoriesList.setOnMouseClicked(mouseEvent -> {
      if (mouseEvent.getButton().equals(MouseButton.PRIMARY) &&
          mouseEvent.getClickCount()%2 == 0 &&
          dependantStoriesList.getSelectionModel().getSelectedItem() != null) {
        btnRemoveStoryClick(new ActionEvent());
        refreshLists();
      }
    });*/
  }

  /**
   * Fills the model collections with stories from the available stories and dependent stories
   * and manages what they contain based on what story is selected. Enables/disables buttons as
   * required.
   */

  private void refreshLists() {
    availableStories.setAll(clones);
    if (story == null) {
      story = allStoriesList.getSelectionModel().getSelectedItem();
    } else {
      btnAddStory.setDisable(false);
      btnRemoveStory.setDisable(false);
      dependantStories.setAll(story.getDependencies());
    }
      availableStories.remove(story);
      availableStories.removeAll(dependantStories);
    if (availableStories.isEmpty()) {
      btnAddStory.setDisable(true);
    }
    if (dependantStories.isEmpty()) {
      btnRemoveStory.setDisable(true);
    }
  }

  /**
   * DO NOT USE THIS FUNCTION - use checkIsCyclic instead.
   * This function takes in a story and uses a recursive
   * depth first search with pruning to determine if there
   * exists a cyclic dependancy with the inputted story
   *
   * @param root - This is the root of the graph. where the search will start.
   * @return - true or false. true for yes it is cyclic and false for no its not.
   */
  private boolean dependencyCheck(Story root) {

    if (visitedStories.contains(root)) {
      return true;
    }

    visitedStories.add(root);

    boolean result = false;

    for (Story childNode : root.getDependencies()) {
      result = dependencyCheck(childNode);
      if (result) {
        break;
      }
    }
    visitedStories.remove(root);

    return result;
  }

  /**
   * USE THIS BABY!
   * This function is needed to reset the visitedStories set so it is empty before we start
   * and get the boolean isCyclic to reset too.
   * basically I reset the globals.
   * Then i call the actual function for checking for cyclic dependencies.
   * I then return if it is cyclic or not
   *
   * @param story - the story to be passed through to check for cyclic dependency
   * @return - true or false indicating if it is(True) or is not (false)
   */
  public boolean checkIsCyclic(Story story) {
    visitedStories = new TreeSet<>();
    return dependencyCheck(story);
  }

  /**
   * Adds a selected story from the list of available stories and attempts to add it to the
   * active story's dependencies
   *
   * @param event An action event
   */
  @FXML
  protected void btnAddStoryClick(ActionEvent event) {
    btnConfirm.setDisable(false);
    Story selectedStory = availableStoriesList.getSelectionModel().getSelectedItem();
    if (selectedStory == null) {
      return;
    }
    story.addDependency(selectedStory);
    if (checkIsCyclic(selectedStory)) {
      story.removeDependency(selectedStory);
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Cannot add dependency to story");
      alert.setHeaderText(null);
      alert.setContentText("The dependency you are trying to add will cause an illegal"
                           + " cyclic dependency to occur.");
      alert.showAndWait();
    } else {
      refreshLists();
    }
  }

  /**
   * Removes the selected story from the list of active story's dependent stories list and removes
   * the dependency from the active story's model.
   *
   * @param event An action event
   */
  @FXML
  protected void btnRemoveStoryClick(ActionEvent event) {
    btnConfirm.setDisable(false);
    Story selectedStory = dependantStoriesList.getSelectionModel().getSelectedItem();
    if (selectedStory == null) {
      return;
    }

    story.removeDependency(selectedStory);
    refreshLists();
  }

  /**
   * Saves the edited state of the dependencies in the dialog to the mainApp
   *
   * @param event Action event
   */
  @FXML
  protected void setBtnConfirm(ActionEvent event) {
    for (Story clone : clones) {
      Story mainStory = syncMap.get(clone.getLabel());
      mainStory.removeAllDependencies();
      for (Story cloneDep : clone.getDependencies()) {
        Story mainDep = syncMap.get(cloneDep.getLabel());
        mainStory.addDependency(mainDep);
      }
      afterMap.put(mainStory, new Story(mainStory));  // put fresh clone in map for after the edit
    }
    UndoRedo undoRedo = generateUndoRedoObject();
    mainApp.newAction(undoRedo);

    if (mainApp.getLMPC().getCurrentListType() == "Backlogs") {
      mainApp.getLMPC().refreshBacklogDisplay();
    }

    thisStage.close();
  }

  /**
   * Discards all changes made from within the dialog and exits the dialog.
   *
   * @param event Action event
   */
  @FXML
  protected void setBtnCancel(ActionEvent event) {
    thisStage.close();
  }

  /**
   * Generate a CompositeUndoRedo to place in the stack containing all stories.
   *
   * @return The CompositeUndoRedo to store.
   */
  private UndoRedo generateUndoRedoObject() {
    CompositeUndoRedo compositeUndoRedo = new CompositeUndoRedo("Edit Story Dependencies");

    for (Story story : beforeMap.keySet()) {
      UndoRedoObject undoRedoObject = new UndoRedoObject();
      undoRedoObject.setAction(Action.STORY_EDIT);
      undoRedoObject.setAgileItem(story);
      undoRedoObject.addDatum(beforeMap.get(story));
      undoRedoObject.addDatum(afterMap.get(story));
      compositeUndoRedo.addUndoRedo(undoRedoObject);
    }

    return compositeUndoRedo;
  }
}
