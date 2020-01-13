package seng302.group5.controller.mainAppControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import seng302.group5.Main;
import seng302.group5.controller.enums.CreateOrEdit;
import seng302.group5.model.Sprint;
import seng302.group5.model.Status;
import seng302.group5.model.Story;
import seng302.group5.model.Task;
import seng302.group5.model.Taskable;
import seng302.group5.model.Team;
import seng302.group5.model.undoredo.Action;
import seng302.group5.model.undoredo.CompositeUndoRedo;
import seng302.group5.model.undoredo.UndoRedo;
import seng302.group5.model.undoredo.UndoRedoObject;

/**
 * Controller class for a single storyPane for the accordion in the scrum board.
 */
public class StoryItemController {

  @FXML private VBox notStartedList;
  @FXML private VBox inProgressList;
  @FXML private VBox verifyList;
  @FXML private VBox doneList;

  @FXML private AnchorPane storyAnchor;
  @FXML private TitledPane storyPane;

  @FXML private ImageView SBImage;
  @FXML private Rectangle doneBar;
  @FXML private Rectangle inProgBar;
  @FXML private Rectangle notStartedBar;

  private Main mainApp;
  private Story story;
  private Sprint sprint;
  private Accordion accordion;

  private Image inprogress;
  private Image complete;
  private Image notStarted;
  private ImageView dinoGif;

  private ObservableList<Task> notStartedTasks;
  private ObservableList<Task> inProgressTasks;
  private ObservableList<Task> verifyTasks;
  private ObservableList<Task> doneTasks;

  private Label selectedLabel;

  /**
   * This function sets up the story item controller.
   * @param story       The story being displayed in this.
   */
  public void setupController(Story story, Main mainApp, Accordion accordion, Sprint sprint) {
    this.story = story;
    this.mainApp = mainApp;
    this.accordion = accordion;
    this.sprint = sprint;
    storyPane.setText(story.getLabel());

    //Set up the dino gif and place it on the accordion
    inprogress = new Image("runningDino.gif");
    complete = new Image("victoryDino.gif");
    notStarted = new Image("progressDino1.png");
    dinoGif = new ImageView();

    dinoGif.setFitHeight(28);
    dinoGif.setFitWidth(28);

    updateDino();

    updateProgBar();

    setupLists();
  }

  public void updateDino() {
    if (this.story.percentComplete() == 0.0 && this.story.percentInProg() == 0.0) {
      dinoGif.setImage(notStarted);
      SBImage.setImage(notStarted);
    }else if (this.story.percentComplete() == 1.0) {
      dinoGif.setImage(complete);
      SBImage.setImage(complete);
    } else {
      dinoGif.setImage(inprogress);
      SBImage.setImage(inprogress);

    }
    storyPane.setGraphic(dinoGif);
  }


  public void updateProgBar() {
    int totalSpace = 180;
    float doneSpace = totalSpace*story.percentComplete();
    float inProgSpace = totalSpace*story.percentInProg();
    float notStartedSpace = totalSpace*(1 - (story.percentComplete() + story.percentInProg()));

    doneBar.setWidth(doneSpace);
    inProgBar.setWidth(inProgSpace);
    notStartedBar.setWidth(notStartedSpace);
  }

  /**
   * Sets the tasks from story into their appropriate lists.
   */
  public void setupLists() {

    selectedLabel = null; //set this to null to avoid funny behaviour after an action.
    notStartedTasks = FXCollections.observableArrayList();
    inProgressTasks = FXCollections.observableArrayList();
    verifyTasks = FXCollections.observableArrayList();
    doneTasks = FXCollections.observableArrayList();
    notStartedList.getChildren().clear();
    inProgressList.getChildren().clear();
    verifyList.getChildren().clear();
    doneList.getChildren().clear();

    if (story.getLabel().equals("Non-story Tasks")) {
      story.removeAllTasks();
      story.addAllTasks(sprint.getTasks());
    }

    for (Task task : story.getTasks()) {

      Label tempLabel = new Label(task.getLabel());

      switch (task.getStatus()) {
        case NOT_STARTED:
          notStartedTasks.add(task);
          //Makes the task extend all the way across the container
          tempLabel.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
          addWithDragging(notStartedList, tempLabel);
          break;
        case IN_PROGRESS:
          inProgressTasks.add(task);
          //Makes the task extend all the way across the container
          tempLabel.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
          addWithDragging(inProgressList, tempLabel);
          break;
        case VERIFY:
          verifyTasks.add(task);
          //Makes the task extend all the way across the container
          tempLabel.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
          addWithDragging(verifyList, tempLabel);
          break;
        case DONE:
          doneTasks.add(task);
          //Makes the task extend all the way across the container
          tempLabel.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
          addWithDragging(doneList, tempLabel);
          break;
      }
    }

    // in case user drops node in blank space in root:
    notStartedList.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        if (checkVBox(event)) {
          addToBottom(notStartedList, (Node) event.getGestureSource());
          generateUndoRedoObject(event, Status.NOT_STARTED);
          updateDino();
          updateProgBar();
          disableVBoxStyles();
        }
      }
    });
    // When dragging over the vbox
    notStartedList.setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        if (checkVBox(event)) {
          notStartedList.setStyle("-fx-border-color: #72f995");
        }
      }
    });
    // When dragging off the vbox
    notStartedList.setOnMouseDragExited(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        notStartedList.setStyle("");
      }
    });

    inProgressList.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        if (checkVBox(event)) {
          addToBottom(inProgressList, (Node) event.getGestureSource());
          generateUndoRedoObject(event, Status.IN_PROGRESS);
          updateDino();
          updateProgBar();
          disableVBoxStyles();

        }
      }
    });
    inProgressList.setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        if (checkVBox(event)) {
          disableVBoxStyles();
          inProgressList.setStyle("-fx-border-color: #72f995");
        }
      }
    });
    inProgressList.setOnMouseDragExited(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        inProgressList.setStyle("");
      }
    });

    verifyList.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        if (checkVBox(event)) {
          addToBottom(verifyList, (Node) event.getGestureSource());
          generateUndoRedoObject(event, Status.VERIFY);
          updateDino();
          updateProgBar();
          disableVBoxStyles();
        }
      }
    });
    verifyList.setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        if (checkVBox(event)) {
          disableVBoxStyles();
          verifyList.setStyle("-fx-border-color: #72f995");
        }
      }
    });
    verifyList.setOnMouseDragExited(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        verifyList.setStyle("");
      }
    });

    doneList.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        if (checkVBox(event)) {
          addToBottom(doneList, (Node) event.getGestureSource());
          generateUndoRedoObject(event, Status.DONE);
          updateDino();
          updateProgBar();
          disableVBoxStyles();
        }
      }
    });
    doneList.setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        if (checkVBox(event)) {
          disableVBoxStyles();
          doneList.setStyle("-fx-border-color: #72f995");
        }
      }
    });
    doneList.setOnMouseDragExited(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        doneList.setStyle("");
      }
    });
  }


  /**
   * Adds the label to a VBox, and assigns mouse event listeners for drag and drop functionality
   * between the four VBoxes (notStartedList, inProgressList, verifyList, doneList).
   * Overrides drag detected, mouse drag detected, drag exited, drag released.
   * @param root VBox root.
   * @param label Label to be added
   */
  private void addWithDragging(VBox root, Label label) {
    //Changes the colour of the clicked on/selected item.
    label.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        if (selectedLabel != null) {
          selectedLabel.setStyle("-fx-background-color: transparent;");
        }
        selectedLabel = (Label) event.getSource();
        selectedLabel.setStyle("-fx-background-color: #ffffa0;");
      }
    });

    label.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
          Label taskLabel = (Label) event.getSource();
          String taskString = taskLabel.getText();
          Task newTask = null;
          for (Task task : story.getTasks()) {
            if (taskString.equals(task.getLabel())) {
              newTask = task;
              break;
            }
          }
          if (newTask != null) {
            Team team = null;
            for (Sprint sprint : mainApp.getSprints()) {
              if (sprint.getSprintStories().contains(story)) {
                team = sprint.getSprintTeam();
              }
            }
            UndoRedo taskEdit = mainApp.showTaskDialog(story, newTask, team,
                                                       CreateOrEdit.EDIT, mainApp.getStage());
            if (taskEdit != null) {
              mainApp.newAction(taskEdit);
            }
            mainApp.getLMPC().getScrumBoardController().refreshTaskLists();
          }
        }
      }
    });

    label.setOnDragDetected(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        label.startFullDrag();
      }
    });

    // next two handlers just an idea how to show the drop target visually:
    label.setOnMouseDragEntered(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        if (checkSource(event) && !checkSameLabel(event)) {
          label.setStyle("-fx-background-color: #72f995;");
        }
      }
    });
    label.setOnMouseDragExited(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        if (checkSource(event) && !checkSameLabel(event)) {
          label.setStyle("");
        }
      }
    });

    label.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
      @Override
      public void handle(MouseDragEvent event) {
        if (checkSource(event) && !checkSameLabel(event)) {
          // Mouse is dropped;
          label.setStyle("");
          disableVBoxStyles();
          int indexOfDropTarget = -1;
          Label sourceLbl = (Label) event.getSource();

          VBox newRoot = null;
          // Removes dragged label from root, saves index of dragged to label;
          if (notStartedList.getChildren().contains(sourceLbl)) {
            generateUndoRedoObject(event, Status.NOT_STARTED);
            notStartedList.getChildren().remove(sourceLbl);
          } else if (inProgressList.getChildren().contains(sourceLbl)) {
            generateUndoRedoObject(event, Status.IN_PROGRESS);
            inProgressList.getChildren().remove(sourceLbl);
          } else if (verifyList.getChildren().contains(sourceLbl)) {
            generateUndoRedoObject(event, Status.VERIFY);
            verifyList.getChildren().remove(sourceLbl);
          } else if (doneList.getChildren().contains(sourceLbl)) {
            generateUndoRedoObject(event, Status.DONE);
            doneList.getChildren().remove(sourceLbl);
          }

          // Adds label to root with new listeners
          addWithDragging(root, sourceLbl);
          int indexOfDrag = root.getChildren().indexOf(event.getSource());
          // Label is now in vbox at bottom
          // Root=correct Vbox, iODT=index of highlighted node, Lbl=label to be moved up
          bringUpNode(root, indexOfDrag, sourceLbl);
          mainApp.getLMPC().getScrumBoardController().refreshTaskLists();
          event.consume();
        }
      }
    });
    root.getChildren().add(label);
    updateDino();
    updateProgBar();
  }

  /**
   * If already exists in root, removes the node then inserts it at the given index.
   * All parameters must be instantiated.
   * @param root Where to insert into
   * @param indexOfDropTarget Index to be inserted at
   * @param label Node to be inserted
   */
  private void bringUpNode(VBox root, int indexOfDropTarget, Node label) {
    if (root.getChildren().contains(label)) {
      root.getChildren().remove(label);
    }
    root.getChildren().add(indexOfDropTarget, label);
  }

  /**
   * Adds the node to the bottom of the root, and removes it from it's original parent.
   * @param root A javafx container with children
   * @param node Node to be appended
   */
  private void addToBottom(VBox root, Node node) {
    //1:Remove from orig location
    Label draggedLabel = (Label) node;
    VBox oldVBox = (VBox) draggedLabel.getParent();
    oldVBox.getChildren().remove(draggedLabel);
    if (root.getChildren().size() == 0) {
      addWithDragging(root, draggedLabel);
    } else {
      Label lowermostLabel = (Label) root.getChildren().get(root.getChildren().size() - 1);
      addWithDragging(root, draggedLabel);

      Task tempT = null;
      Task tempT2 = null;

      for (Task task : story.getTasks()) {
        if (task.getLabel().equals(lowermostLabel.getText()))
          tempT = task;
        if (task.getLabel().equals(draggedLabel.getText())) {
          tempT2 = task;
        }
      }

      int pos = story.getTasks().indexOf(tempT2);

      if (tempT != null) {
        if (pos <= story.getTasks().indexOf(tempT)) {
          story.removeTask(tempT);
          story.addTask(pos, tempT);
        } else if (pos > story.getTasks().indexOf(tempT)) {
          story.removeTask(tempT);
          story.addTask(pos - 1, tempT);
        }
      }
    }
  }

  /**
   * Checks to make sure event source and final location of mouse drag event for a label
   * have the same parent's parent. This make sure it belongs to the same set of four lists
   * (stops dragging label between stories).
   * @param event The mouse drag event
   * @return same? true : false
   */
  private boolean checkSource(MouseDragEvent event) {
    Label sourceLabel = (Label) event.getGestureSource();
    Label destinationLabel = (Label) event.getSource();
    // checks to see if both nodes contained within the higher level hbox
    if (sourceLabel.getParent().getParent().getParent().equals(
        destinationLabel.getParent().getParent().getParent())) {
      return true;
    }
    return false;
  }

  /**
   * Checks to make sure destination VBox belongs to the same parent as the label's VBox parent
   * @param event The mouse drag event
   * @return same? true : false
   */
  private boolean checkVBox(MouseDragEvent event) {
    Label sourceLabel = (Label) event.getGestureSource();
    VBox destinationVBox = (VBox) event.getSource();
    // checks to see if both nodes contained within the higher level hbox
    if (sourceLabel.getParent().getParent().getParent().equals(
        destinationVBox.getParent().getParent())) {
      return true;
    }
    return false;
  }

  /**
   * A check to see if mouse drag event source and gesture source are the same label.
   * @param event The mouse drag event
   * @return same? true : false
   */
  private boolean checkSameLabel(MouseDragEvent event) {
    Label sourceLabel = (Label) event.getGestureSource();
    Label destinationLabel = (Label) event.getSource();
    if (sourceLabel == destinationLabel) {
      return true;
    }
    return false;
  }

  /**
   * Add a new task into associated story (the current accordion)
   *
   * @param event Event generated by event listener
   */
  @FXML
  protected void btnAddTask(ActionEvent event) {
    //Show the task creation dialog and make the undoredo object
    UndoRedo taskCreate;
    Taskable taskable;
    if (story.getLabel().equals("Non-story Tasks")) {
      taskable = sprint;
    } else {
      taskable = story;
    }
    taskCreate = mainApp.showTaskDialog(taskable, null, null, CreateOrEdit.CREATE, mainApp.getStage());
    if (taskCreate != null) {
      mainApp.newAction(taskCreate);
    }
    mainApp.getLMPC().getScrumBoardController().refreshTaskLists();

    //Gotta update the dino and the prog bar to reflect changes
    updateDino();
    updateProgBar();
  }

  /**
   * This handles the remove button being pressed
   * It will take the selected task and delete it.
   * @param event The event of the minus button being pressed.
   */
  @FXML
  protected void btnRemoveTask(ActionEvent event) {

    if (selectedLabel != null) {

      UndoRedo ssUR = new UndoRedoObject();

      Task deleteTask = null;

      for (Task task : story.getTasks()) {
        if (task.getLabel().equals(selectedLabel.getText())) {
          deleteTask = task;
          break;
        }
      }

      if (story.getLabel().equals("Non-story Tasks")) {
        Sprint before = new Sprint(sprint);

        sprint.removeTask(deleteTask);
        story.removeTask(deleteTask);
        Sprint after = new Sprint(sprint);
        ssUR.setAgileItem(sprint);
        ssUR.setAction(Action.SPRINT_EDIT);
        ssUR.addDatum(before);
        ssUR.addDatum(after);
      } else {
        Story before = new Story(story);
        story.removeTask(deleteTask);
        Story after = new Story(story);
        ssUR.setAgileItem(story);
        ssUR.setAction(Action.STORY_EDIT);
        ssUR.addDatum(before);
        ssUR.addDatum(after);
      }

      CompositeUndoRedo comp = new CompositeUndoRedo("Scrumboard Drag Action");
      comp.addUndoRedo(ssUR);

      mainApp.newAction(ssUR);

      mainApp.getLMPC().getScrumBoardController().refreshTaskLists();

      //Gotta update the dino and the prog bar to reflect changes
      updateDino();
      updateProgBar();
    }
  }

  /**
   * This handles when the edit button is pushed for a task.
   * This will take the selected item and edit it accordingly.
   * @param event the event of pushing the button.
   */
  @FXML
  protected void btnEditTask(ActionEvent event) {
    if (selectedLabel != null) {
      Task newTask = null;
      for (Task task : story.getTasks()) {
        if (selectedLabel.getText().equals(task.getLabel())) {
          newTask = task;
          break;
        }
      }
      if (newTask != null) {
        Team team = null;
        for (Sprint sprint : mainApp.getSprints()) {
          if (sprint.getSprintStories().contains(story)) {
            team = sprint.getSprintTeam();
          }
        }
        UndoRedo taskEdit = mainApp.showTaskDialog(story, newTask, team,
                                                   CreateOrEdit.EDIT, mainApp.getStage());
        if (taskEdit != null) {
          mainApp.newAction(taskEdit);
        }
        mainApp.getLMPC().getScrumBoardController().refreshTaskLists();

        //Gotta update the dino and the prog bar to reflect changes
        updateDino();
        updateProgBar();
      }
    }
  }


  /**
   * Generates the undo/redo object for drag/dropping an item between lists.
   * @param event Event of drop
   * @param status Status that the task is changed to.
   */
  private void generateUndoRedoObject(MouseDragEvent event, Status status) {
    int posToInsert = -1;
    int initialPosition = -1;
    UndoRedoObject ssUR = new UndoRedoObject();
    // Step 1:Gesture source will always be label, item dragged.
    Label taskLabel = (Label) event.getGestureSource();
    String sourceTaskString = taskLabel.getText();

    // Step 2:Get source task string
    String destinationTaskString = "None";
    if (event.getSource() instanceof Label) {
      Label taskNewPos = (Label) event.getSource();
      destinationTaskString = taskNewPos.getText();
    } else if (event.getSource() instanceof VBox) {
      VBox droppedBox = (VBox) event.getSource();
      if (droppedBox.getChildren().size() != 0) {
        Label taskNewPos =
            (Label) droppedBox.getChildren().get(droppedBox.getChildren().size() - 1);
        destinationTaskString = taskNewPos.getText();
      } else {
        destinationTaskString = sourceTaskString;
      }
    }

    //Step2: Create new and last task(old pre-drag, new after)
    Task newTask = null;

    //Step3: Clone last Task, assign task to newTask
    for (Task task : story.getTasks()) {
      if (sourceTaskString.equals(task.getLabel())) {
        newTask = task;
        break;
      }
    }

    if (story.getLabel().equals("Non-story Tasks")) {
      Sprint before = new Sprint(sprint);
      for (Task task : sprint.getTasks()) {
        if (task.getLabel().equals(destinationTaskString)) {
          posToInsert = sprint.getTasks().indexOf(task);
        }
        if (task.getLabel().equals(sourceTaskString)) {
          initialPosition = sprint.getTasks().indexOf(task);
        }
      }
      sprint.removeTask(newTask);
      story.removeTask(newTask);
      if (posToInsert <= initialPosition && posToInsert != -1 && initialPosition != -1) {
        sprint.addTask(posToInsert, newTask);
        story.addTask(posToInsert, newTask);
      } else if (posToInsert > initialPosition && posToInsert != -1 && initialPosition != -1) {
        sprint.addTask(posToInsert - 1, newTask);
        story.addTask(posToInsert - 1, newTask);
      } else {
        System.out.println("Bad things happened");
      }
      Sprint after = new Sprint(sprint);
      ssUR.setAgileItem(sprint);
      ssUR.setAction(Action.SPRINT_EDIT);
      ssUR.addDatum(before);
      ssUR.addDatum(after);
    } else {
      Story before = new Story(story);
      for (Task task : story.getTasks()) {
        if (task.getLabel().equals(destinationTaskString)) {
          posToInsert = story.getTasks().indexOf(task);
        }
        if (task.getLabel().equals(sourceTaskString)) {
          initialPosition = story.getTasks().indexOf(task);
        }
      }
      story.removeTask(newTask);
      if (posToInsert <= initialPosition && posToInsert != -1 && initialPosition != -1) {
        story.addTask(posToInsert, newTask);
      } else if (posToInsert > initialPosition && posToInsert != -1 && initialPosition != -1) {
        story.addTask(posToInsert - 1, newTask);
      } else {
        System.out.println("Bad things happened");
      }
      Story after = new Story(story);
      ssUR.setAgileItem(story);
      ssUR.setAction(Action.STORY_EDIT);
      ssUR.addDatum(before);
      ssUR.addDatum(after);
    }

    Task before = new Task(newTask);
    newTask.setStatus(status);
    Task after = new Task(newTask);
    UndoRedoObject taskUR = new UndoRedoObject();
    taskUR.setAgileItem(newTask);
    taskUR.setAction(Action.TASK_EDIT);
    taskUR.addDatum(before);
    taskUR.addDatum(after);

    CompositeUndoRedo comp = new CompositeUndoRedo("Scrumboard Drag Action");
    comp.addUndoRedo(ssUR);
    comp.addUndoRedo(taskUR);

    mainApp.newAction(comp);
  }

  /**
   * Sets all VBox styles to empty
   */
  private void disableVBoxStyles() {
    notStartedList.setStyle("");
    inProgressList.setStyle("");
    verifyList.setStyle("");
    doneList.setStyle("");
  }

  public String getPaneName() {
    return storyPane.getText();
  }

  public boolean checkIfOpened() {
    return storyPane.isExpanded();
  }

  public void expandTab() {
    accordion.setExpandedPane(storyPane);
  }

  public Story getStory() {
    return story;
  }

  public TitledPane getTitledPane() {
    return storyPane;
  }

  public void setAccordion(Accordion accordion) {
    this.accordion = accordion;
  }

}
