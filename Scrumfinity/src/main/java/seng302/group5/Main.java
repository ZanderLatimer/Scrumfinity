package seng302.group5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import seng302.group5.controller.dialogControllers.BacklogDialogController;
import seng302.group5.controller.dialogControllers.EffortDialogController;
import seng302.group5.controller.dialogControllers.SprintDialogController;
import seng302.group5.controller.dialogControllers.TaskDialogController;
import seng302.group5.controller.mainAppControllers.ListMainPaneController;
import seng302.group5.controller.mainAppControllers.LoginController;
import seng302.group5.controller.mainAppControllers.MenuBarController;
import seng302.group5.controller.dialogControllers.ReleaseDialogController;
import seng302.group5.controller.dialogControllers.PersonDialogController;
import seng302.group5.controller.dialogControllers.ProjectDialogController;
import seng302.group5.controller.mainAppControllers.ReportDialogController;
import seng302.group5.controller.dialogControllers.StoryDialogController;
import seng302.group5.controller.dialogControllers.TeamDialogController;
import seng302.group5.controller.enums.CreateOrEdit;
import seng302.group5.controller.dialogControllers.SkillsDialogController;
import seng302.group5.controller.mainAppControllers.ToolBarController;
import seng302.group5.model.AgileController;
import seng302.group5.model.AgileItem;
import seng302.group5.model.Backlog;
import seng302.group5.model.Effort;
import seng302.group5.model.Estimate;
import seng302.group5.model.Project;
import seng302.group5.model.Release;
import seng302.group5.model.Role;
import seng302.group5.model.Skill;
import seng302.group5.model.Person;
import seng302.group5.model.Sprint;
import seng302.group5.model.Story;
import seng302.group5.model.Task;
import seng302.group5.model.Taskable;
import seng302.group5.model.Team;
import seng302.group5.model.undoredo.Action;
import seng302.group5.model.undoredo.CompositeUndoRedo;
import seng302.group5.model.undoredo.UndoRedo;
import seng302.group5.model.undoredo.UndoRedoHandler;
import seng302.group5.model.undoredo.UndoRedoObject;
import seng302.group5.model.util.RevertHandler;

/**
 * Main class to run the application
 */
public class Main extends Application {

  private Stage primaryStage;
  private BorderPane rootLayout;
  private VBox topVBox;
  private BorderPane loginLayout;
  private Scene mainScene;
  private Scene loginScene;

  private ListMainPaneController LMPC;
  private MenuBarController MBC;
  private ToolBarController TBC;

  private ObservableList<Project> projects = FXCollections.observableArrayList();
  private ObservableList<Team> teams = FXCollections.observableArrayList();
  private ObservableList<Skill> skills = FXCollections.observableArrayList();
  private ObservableList<Person> people = FXCollections.observableArrayList();
  private ObservableList<Release> releases = FXCollections.observableArrayList();
  private ObservableList<Role> roles = FXCollections.observableArrayList();
  private ObservableList<Story> stories = FXCollections.observableArrayList();
  private ObservableList<Backlog> backlogs = FXCollections.observableArrayList();
  private ObservableList<Estimate> estimates = FXCollections.observableArrayList();
  private ObservableList<Sprint> sprints = FXCollections.observableArrayList();

  private ArrayList<AgileItem> nonRemovable = new ArrayList<>();
  private ArrayList<AgileController> openControllers = new ArrayList<>();

  private UndoRedoHandler undoRedoHandler = new UndoRedoHandler(this);

  private UndoRedo lastSavedObject = null;

  private RevertHandler revertHandler = new RevertHandler(this);

  private String mainTitle = "Scrumfinity"; //THIS CAN BE USED FOR ORGANIZATION

  private Map<Class, String> listTypeMap;

  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Starts up the app creating the stage and setting up the window creates the default skills and
   * roles that are needed
   *
   * @param primaryStage The main stage for the app
   */
  @Override
  public void start(Stage primaryStage) {
    // Show login screen
    showLoginScreen(primaryStage);

    this.primaryStage = primaryStage;
    this.primaryStage.setTitle(mainTitle);
    this.primaryStage.setMinHeight(500);
    this.primaryStage.setMinWidth(820);
    // Constructs the application
    initRootLayout();
    showMenuBar();
    showToolBar();
    showListMainPane();

    //Set predetermined roles and skills
    Skill smSkill = new Skill("Scrum Master", "Trained to be a Scrum Master");
    Skill poSkill = new Skill("Product Owner", "Trained to be a Product Owner");
    addSkill(smSkill);
    addSkill(poSkill);
    nonRemovable.add(smSkill);
    nonRemovable.add(poSkill);

    Role devRole = new Role("DEV", "Developer");
    Role poRole = new Role("PO", "Product Owner", poSkill, 1);
    Role smRole = new Role("SM", "Scrum Master", smSkill, 1);

    addRole(devRole);
    addRole(poRole);
    addRole(smRole);

    createDefaultEstimates();

    revertHandler.setLastSaved();

    initListTypeMap();

    this.primaryStage.setOnCloseRequest(event -> {
      event.consume();
      exitScrumfinity();
    });
  }

  /**
   * Creates default estimate lists in main app. For now just creates fibonacci scale and our custom
   * dino scale.
   */
  public void createDefaultEstimates() {
    List<String> fiboEsts = Arrays.asList("Not Set", "1", "2", "3", "5", "8", "13", "21", "Epic");
    Estimate fibonacci = new Estimate("Fibonacci", fiboEsts);
    List<String> dinoEsts = Arrays.asList("Not Set", "Dino Egg", "Dino Baby", "Dino Toddler",
                                          "Dino Kid", "Dino Teen", "Dino Saur", "Elder Dino",
                                          "Epic Dino");
    Estimate dinos = new Estimate("Dinos", dinoEsts);

    estimates.addAll(fibonacci, dinos);
  }

  /**
   * Handles exiting the app. checks if there are unsaved changes if so displays a popup warning
   * otherwise closes.
   */
  public void exitScrumfinity() {
    if (!checkSaved()) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Unsaved changes");
      alert.setHeaderText(null);
      alert.setContentText("There are unsaved changes, are you sure you wish to quit?");
      Optional<ButtonType> result = alert.showAndWait();
      if (result.get() == ButtonType.OK) {
        primaryStage.close();
      }
    } else {
      primaryStage.close();
    }
  }

  /**
   * Initialise the map between the class and the list type value.
   */
  private void initListTypeMap() {
    listTypeMap = new HashMap<>();
    listTypeMap.put(Backlog.class, "Backlogs");
    listTypeMap.put(Person.class, "People");
    listTypeMap.put(Project.class, "Projects");
    listTypeMap.put(Release.class, "Releases");
    listTypeMap.put(Skill.class, "Skills");
    listTypeMap.put(Sprint.class, "Sprints");
    listTypeMap.put(Story.class, "Stories");
    listTypeMap.put(Team.class, "Teams");
  }

  /**
   * Shows the initial welcome screen.
   *
   * @param primaryStage The main stage of the program
   */
  public void showLoginScreen(Stage primaryStage) {
    try {
      this.primaryStage = primaryStage;
      this.primaryStage.setTitle(mainTitle);
      this.primaryStage.setMinHeight(500);
      this.primaryStage.setMinWidth(820);

      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(LoginController.class.getResource("/LoginScreen.fxml"));
      loginLayout = loader.load();

      LoginController loginController = loader.getController();
      loginController.setMainApp(this);
      loginScene = new Scene(loginLayout);
      primaryStage.setScene(loginScene);
      primaryStage.show();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes the root layout.
   */
  public void initRootLayout() {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/Main.fxml"));
      rootLayout = loader.load();

      topVBox = new VBox();
      mainScene = new Scene(rootLayout);
/*      primaryStage.setScene(mainScene);
      primaryStage.show();*/

      primaryStage.getIcons().add(new Image("Thumbnail.png")); //sets the icon

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Shows the menu bar inside root layout
   */
  public void showMenuBar() {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(MenuBarController.class.getResource("/MenuBar.fxml"));
      MenuBar menuBar = loader.load();

      MenuBarController controller = loader.getController();
      controller.setMainApp(this);
      MBC = controller;

      topVBox.getChildren().add(menuBar);
      rootLayout.setTop(topVBox);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Shows the tool bar inside of the ListMainPane.
   */
  public void showToolBar() {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(ToolBarController.class.getResource("/Toolbar.fxml"));
      ToolBar toolBar = loader.load();

      ToolBarController controller = loader.getController();
      controller.setMainApp(this);
      TBC = controller;
      controller.setupController();

      topVBox.getChildren().add(toolBar);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Shows the ListMainPane
   */
  public void showListMainPane() {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(ListMainPaneController.class.getResource("/ListMainPane.fxml"));
      SplitPane splitPane = loader.load();

      ListMainPaneController controller = loader.getController();
      controller.setMainApp(this);
      controller.checkListType();   // Load objects into list view
      LMPC = controller;

      rootLayout.setCenter(splitPane);

      // init scrum board within LMPC
      LMPC.initScrumBoard();
      LMPC.initBurnDown();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * sets up the dialog box for creating/editing a project
   *
   * @param createOrEdit the createOrEdit object that decides if you are creating or editing
   */
  public void showProjectDialog(CreateOrEdit createOrEdit) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/ProjectDialog.fxml"));
      VBox projectDialogLayout = loader.load();

      ProjectDialogController controller = loader.getController();
      Scene projectDialogScene = new Scene(projectDialogLayout);
      Stage projectDialogStage = new Stage();

      Project project = null;
      if (createOrEdit == CreateOrEdit.EDIT) {
        project = (Project) LMPC.getSelected();
        if (project == null) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Nothing selected");
          alert.setHeaderText(null);
          alert.setContentText("No project selected.");
          alert.showAndWait();
          return;
        }
      }

      if (isInControllerStack(controller, project)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, projectDialogStage, createOrEdit, project);
      pushControllerStack(controller);

      projectDialogStage.initModality(Modality.APPLICATION_MODAL);
      projectDialogStage.initOwner(primaryStage);
      projectDialogStage.setScene(projectDialogScene);
      projectDialogStage.showAndWait();


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * sets up the dialog box for creating/editing a project
   *
   * @param project the selected project that to be edited form the project combo box.
   * @param stage   the stage it is currently on to void unusual behaviour.
   */
  public void showProjectDialogWithinRelease(Project project, Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/ProjectDialog.fxml"));
      VBox projectDialogLayout = loader.load();

      ProjectDialogController controller = loader.getController();
      Scene projectDialogScene = new Scene(projectDialogLayout);
      Stage projectDialogStage = new Stage();

      if (isInControllerStack(controller, project)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, projectDialogStage, CreateOrEdit.EDIT, project);
      pushControllerStack(controller);

      projectDialogStage.initModality(Modality.APPLICATION_MODAL);
      projectDialogStage.initOwner(stage);
      projectDialogStage.setScene(projectDialogScene);
      projectDialogStage.showAndWait();


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * sets up the dialog box for creating/editing a project
   *
   * @param createOrEdit either create a new project or edit existing project.
   * @param project the selected project that to be edited form the project combo box.
   * @param backlog the backlog that were selected from the backlog combo in sprint dialog
   * @param stage   the stage it is currently on to void unusual behaviour.
   */
  public void showProjectDialogWithinSprint(CreateOrEdit createOrEdit, Project project,
                                            Backlog backlog, Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/ProjectDialog.fxml"));
      VBox projectDialogLayout = loader.load();

      ProjectDialogController controller = loader.getController();
      Scene projectDialogScene = new Scene(projectDialogLayout);
      Stage projectDialogStage = new Stage();

      if (isInControllerStack(controller, project)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, projectDialogStage, createOrEdit, project);
      controller.setupSprintMode(backlog);
      pushControllerStack(controller);

      projectDialogStage.initModality(Modality.APPLICATION_MODAL);
      projectDialogStage.initOwner(stage);
      projectDialogStage.setScene(projectDialogScene);
      projectDialogStage.showAndWait();


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * sets up the dialog box for creating/editing a Team
   *
   * @param createOrEdit the createOrEdit object that decides if you are creating or editing
   */
  public void showTeamDialog(CreateOrEdit createOrEdit) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/TeamDialog.fxml"));
      Pane teamDialogLayout = loader.load();

      TeamDialogController controller = loader.getController();

      Scene teamDialogScene = new Scene(teamDialogLayout);
      Stage teamDialogStage = new Stage();

      Team team = null;
      if (createOrEdit == CreateOrEdit.EDIT) {
        team = (Team) LMPC.getSelected();
        if (team == null) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Nothing selected");
          alert.setHeaderText(null);
          alert.setContentText("No team selected.");
          alert.showAndWait();
          return;
        }
      }

      if (isInControllerStack(controller, team)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, teamDialogStage, createOrEdit, team);
      pushControllerStack(controller);

      teamDialogStage.initModality(Modality.APPLICATION_MODAL);
      teamDialogStage.initOwner(primaryStage);
      teamDialogStage.setScene(teamDialogScene);
      teamDialogStage.showAndWait();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * sets up the dialog box for creating/editing a Team withing a parent dialog
   *
   * @param team the team that you wanted to view or edit information with
   * @param stage the stage it is currently on to void unusual behaviour
   */
  public void showTeamDialogWithinNested(Team team, Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/TeamDialog.fxml"));
      Pane teamDialogLayout = loader.load();

      TeamDialogController controller = loader.getController();

      Scene teamDialogScene = new Scene(teamDialogLayout);
      Stage teamDialogStage = new Stage();

      if (isInControllerStack(controller, team)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, teamDialogStage, CreateOrEdit.EDIT, team);
      pushControllerStack(controller);

      teamDialogStage.initModality(Modality.APPLICATION_MODAL);
      teamDialogStage.initOwner(stage);
      teamDialogStage.setScene(teamDialogScene);
      teamDialogStage.showAndWait();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets up the dialog box for Reporting, and displays it with focus and lock.
   *
   * @param createOrEdit the createOrEdit object that decides if you are creating or editing
   */
  public void showReportDialog(CreateOrEdit createOrEdit) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/ReportDialog.fxml"));
      VBox releaseDialogLayout = loader.load();

      ReportDialogController controller = loader.getController();
      Scene releaseDialogScene = new Scene(releaseDialogLayout);
      Stage releaseDialogStage = new Stage();

      if (createOrEdit == CreateOrEdit.EDIT) {
        Release release = (Release) LMPC.getSelected();
        if (release == null) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Nothing selected");
          alert.setHeaderText(null);
          alert.setContentText("No release selected.");
          alert.showAndWait();
          return;
        }
      }
      controller.setupController(this, releaseDialogStage);

      releaseDialogStage.initModality(Modality.APPLICATION_MODAL);
      releaseDialogStage.initOwner(primaryStage);
      releaseDialogStage.setScene(releaseDialogScene);
      releaseDialogStage.show();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * sets up the dialog box for creating/editing a Release
   *
   * @param createOrEdit the createOrEdit object that decides if you are creating or editing
   */
  public void showReleaseDialog(CreateOrEdit createOrEdit) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/ReleaseDialog.fxml"));
      VBox releaseDialogLayout = loader.load();

      ReleaseDialogController controller = loader.getController();
      Scene releaseDialogScene = new Scene(releaseDialogLayout);
      Stage releaseDialogStage = new Stage();

      Release release = null;
      if (createOrEdit == CreateOrEdit.EDIT) {
        release = (Release) LMPC.getSelected();
        if (release == null) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Nothing selected");
          alert.setHeaderText(null);
          alert.setContentText("No release selected.");
          alert.showAndWait();
          return;
        }
      }

      if (isInControllerStack(controller, release)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, releaseDialogStage, createOrEdit, release);
      pushControllerStack(controller);

      releaseDialogStage.initModality(Modality.APPLICATION_MODAL);
      releaseDialogStage.initOwner(primaryStage);
      releaseDialogStage.setScene(releaseDialogScene);
      releaseDialogStage.show();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * sets up the dialog box for creating/editing a release
   *
   * @param createOrEdit either create a new release or edit existing release.
   * @param release the selected release that to be edited form the release combo box.
   * @param project the project that were linked with the backlog from the backlog combo in sprint dialog.
   * @param stage   the stage it is currently on to void unusual behaviour.
   */
  public void showReleaseDialogWithinSprint(CreateOrEdit createOrEdit, Release release,
                                            Project project, Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/ReleaseDialog.fxml"));
      VBox releaseDialogLayout = loader.load();

      ReleaseDialogController controller = loader.getController();
      Scene releaseDialogScene = new Scene(releaseDialogLayout);
      Stage releaseDialogStage = new Stage();

      if (isInControllerStack(controller, release)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, releaseDialogStage, createOrEdit, release);
      controller.setupSprintMode(project);
      pushControllerStack(controller);

      releaseDialogStage.initModality(Modality.APPLICATION_MODAL);
      releaseDialogStage.initOwner(stage);
      releaseDialogStage.setScene(releaseDialogScene);
      releaseDialogStage.showAndWait();


    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  /**
   * sets up the dialog box for creating/editing a person
   *
   * @param createOrEdit the createOrEdit object that decides if you are creating or editing
   */
  public void showPersonDialog(CreateOrEdit createOrEdit) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/PersonDialog.fxml"));
      VBox personDialogLayout = loader.load();

      PersonDialogController controller = loader.getController();
      Scene personDialogScene = new Scene(personDialogLayout);
      Stage personDialogStage = new Stage();

      Person person = null;
      if (createOrEdit == CreateOrEdit.EDIT) {
        person = (Person) LMPC.getSelected();
        if (person == null) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Nothing selected");
          alert.setHeaderText(null);
          alert.setContentText("No person selected.");
          alert.showAndWait();
          return;
        }
      }

      if (isInControllerStack(controller, person)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, personDialogStage, createOrEdit, person);
      pushControllerStack(controller);

      personDialogStage.initModality(Modality.APPLICATION_MODAL);
      personDialogStage.initOwner(primaryStage);
      personDialogStage.setScene(personDialogScene);
      personDialogStage.showAndWait();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * sets up the dialog box for editing a person when opened from a parent dialog
   *
   * @param person the person that you wanted to view or edit information with
   * @param stage the stage it is currently on to void unusual behaviour
   */
  public void showPersonDialogNested(Person person,Role role, Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/PersonDialog.fxml"));
      VBox personDialogLayout = loader.load();

      PersonDialogController controller = loader.getController();
      Scene personDialogScene = new Scene(personDialogLayout);
      Stage personDialogStage = new Stage();

      if (isInControllerStack(controller, person)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, personDialogStage, CreateOrEdit.EDIT, person);
      controller.setupTeamMode(role);
      pushControllerStack(controller);

      personDialogStage.initModality(Modality.APPLICATION_MODAL);
      personDialogStage.initOwner(stage);
      personDialogStage.setScene(personDialogScene);
      personDialogStage.showAndWait();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * sets up the dialog box for editing a person when opened from the backlog dialog
   *
   * @param createOrEdit whether the person is being created or edited
   * @param person the person that you wanted to view or edit information with, null if creating
   * @param stage the stage it is currently on to void unusual behaviour
   */
  public void showPersonDialogWithinBacklog(CreateOrEdit createOrEdit, Person person, Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/PersonDialog.fxml"));
      VBox personDialogLayout = loader.load();

      PersonDialogController controller = loader.getController();
      Scene personDialogScene = new Scene(personDialogLayout);
      Stage personDialogStage = new Stage();

      if (isInControllerStack(controller, person)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, personDialogStage, createOrEdit, person);
      controller.setupBacklogMode();
      pushControllerStack(controller);

      personDialogStage.initModality(Modality.APPLICATION_MODAL);
      personDialogStage.initOwner(stage);
      personDialogStage.setScene(personDialogScene);
      personDialogStage.showAndWait();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * sets up the dialog box for creating/editing a skill
   *
   * @param createOrEdit the createOrEdit object that decides if you are creating or editing
   */
  public void showSkillDialog(CreateOrEdit createOrEdit) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/SkillsDialog.fxml"));
      VBox SkillsDialogLayout = loader.load();

      SkillsDialogController controller = loader.getController();
      Scene skillDialogScene = new Scene(SkillsDialogLayout);
      Stage skillDialogStage = new Stage();

      Skill skill = null;
      if (createOrEdit == CreateOrEdit.EDIT) {
        skill = (Skill) LMPC.getSelected();
        if (skill == null) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Nothing selected");
          alert.setHeaderText(null);
          alert.setContentText("No skill selected.");
          alert.showAndWait();
          return;
        }
      }

      if (isInControllerStack(controller, skill)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, skillDialogStage, createOrEdit, skill);
      pushControllerStack(controller);

      skillDialogStage.initModality(Modality.APPLICATION_MODAL);
      skillDialogStage.initOwner(primaryStage);
      skillDialogStage.setScene(skillDialogScene);
      skillDialogStage.showAndWait();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * sets up the dialog box for editing a skill when opened from the person dialog
   *
   * @param skill the skill that you wanted to view or edit information with
   * @param stage the stage it is currently on to void unusual behaviour
   */
  public void showSkillDialogWithinPerson(Skill skill, Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/SkillsDialog.fxml"));
      VBox skillDialogLayout = loader.load();

      SkillsDialogController controller = loader.getController();
      Scene skillDialogScene = new Scene(skillDialogLayout);
      Stage skillDialogStage = new Stage();

      if (isInControllerStack(controller, skill)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, skillDialogStage, CreateOrEdit.EDIT, skill);
      pushControllerStack(controller);

      skillDialogStage.initModality(Modality.APPLICATION_MODAL);
      skillDialogStage.initOwner(stage);
      skillDialogStage.setScene(skillDialogScene);
      skillDialogStage.showAndWait();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets up a dialog box for creating/editing a Story.
   *
   * @param createOrEdit The CreateOrEdit object that determines whether you are creating or
   *                     editing.
   */
  public void showStoryDialog(CreateOrEdit createOrEdit) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/StoryDialog.fxml"));
      HBox StoryDialogLayout = loader.load();

      StoryDialogController controller = loader.getController();
      Scene storyDialogScene = new Scene(StoryDialogLayout);
      Stage storyDialogStage = new Stage();

      Story story = null;
      if (createOrEdit == CreateOrEdit.EDIT) {
        story = (Story) LMPC.getSelected();
        if (story == null) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Nothing selected");
          alert.setHeaderText(null);
          alert.setContentText("No story selected.");
          alert.showAndWait();
          return;
        }
      }

      if (isInControllerStack(controller, story)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, storyDialogStage, createOrEdit, story);
      pushControllerStack(controller);

      storyDialogStage.initModality(Modality.APPLICATION_MODAL);
      storyDialogStage.initOwner(primaryStage);
      storyDialogStage.setScene(storyDialogScene);
      storyDialogStage.showAndWait();

      LMPC.getScrumBoardController().refreshComboBoxes();
      LMPC.getBurndownController().refreshComboBoxes();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * sets up the dialog box for creating or editing a story when opened from the backlog dialog
   *
   * @param createOrEdit it should be edit since it is opened existing from backlog dialog
   * @param story the person that you wanted to view or edit information with
   * @param owner the stage it is currently on to void unusual behaviour
   */
  public void showStoryDialogWithinBacklog(CreateOrEdit createOrEdit, Story story,
                                           Backlog backlog, Stage owner) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/StoryDialog.fxml"));
      HBox StoryDialogLayout = loader.load();

      StoryDialogController controller = loader.getController();
      Scene storyDialogScene = new Scene(StoryDialogLayout);
      Stage storyDialogStage = new Stage();

      if (createOrEdit == CreateOrEdit.EDIT) {
        if (story == null) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Nothing selected");
          alert.setHeaderText(null);
          alert.setContentText("No story selected.");
          alert.showAndWait();
          return;
        }
      }

      if (isInControllerStack(controller, story)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, storyDialogStage, createOrEdit, story);
      controller.setupBacklogMode(backlog);
      pushControllerStack(controller);

      storyDialogStage.initModality(Modality.APPLICATION_MODAL);
      storyDialogStage.initOwner(owner);
      storyDialogStage.setScene(storyDialogScene);
      storyDialogStage.showAndWait();

      LMPC.getScrumBoardController().refreshComboBoxes();
      LMPC.getBurndownController().refreshComboBoxes();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * sets up the dialog box for creating or editing a story when opened from the sprint dialog.
   *
   * @param createOrEdit either it is editing the current story in the lists or create a new one.
   * @param story the story that you wanted to view or edit information with. Null for creation.
   * @param stage the stage it is currently on to void unusual behaviour
   */
  public void showStoryDialogWithinSprint(CreateOrEdit createOrEdit, Story story, Backlog backlog,
                                          Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/StoryDialog.fxml"));
      HBox StoryDialogLayout = loader.load();

      StoryDialogController controller = loader.getController();
      Scene storyDialogScene = new Scene(StoryDialogLayout);
      Stage storyDialogStage = new Stage();

      if (isInControllerStack(controller, story)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, storyDialogStage, createOrEdit, story);
      controller.setupSprintMode(backlog);
      pushControllerStack(controller);

      storyDialogStage.initModality(Modality.APPLICATION_MODAL);
      storyDialogStage.initOwner(stage);
      storyDialogStage.setScene(storyDialogScene);
      storyDialogStage.showAndWait();

      LMPC.getScrumBoardController().refreshComboBoxes();
      LMPC.getBurndownController().refreshComboBoxes();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets up a dialog box for creating/editing a backlog.
   *
   * @param createOrEdit The CreateOrEdit object that determines whether you are creating or
   *                     editing.
   */
  public void showBacklogDialog(CreateOrEdit createOrEdit) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/BacklogDialog.fxml"));
      VBox backlogDialogLayout = loader.load();

      BacklogDialogController controller = loader.getController();
      Scene backlogDialogScene = new Scene(backlogDialogLayout);
      Stage backlogDialogStage = new Stage();

      Backlog backlog = null;
      if (createOrEdit == CreateOrEdit.EDIT) {
        backlog = (Backlog) LMPC.getSelected();
        if (backlog == null) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Nothing selected");
          alert.setHeaderText(null);
          alert.setContentText("No backlog selected.");
          alert.showAndWait();
          return;
        }
      }

      if (isInControllerStack(controller, backlog)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, backlogDialogStage, createOrEdit, backlog);
      pushControllerStack(controller);

      backlogDialogStage.initModality(Modality.APPLICATION_MODAL);
      backlogDialogStage.initOwner(primaryStage);
      backlogDialogStage.setScene(backlogDialogScene);
      backlogDialogStage.showAndWait();

      LMPC.getScrumBoardController().refreshComboBoxes();
      LMPC.getBurndownController().refreshComboBoxes();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets up a dialog box for editing a backlog within a parent dialog.
   *
   * @param backlog the selected backlog that to be edited form the backlog combo box.
   * @param stage   the stage it is currently on to void unusual behaviour.
   */
  public void showBacklogDialogNested(Backlog backlog, Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/BacklogDialog.fxml"));
      VBox backlogDialogLayout = loader.load();

      BacklogDialogController controller = loader.getController();
      Scene backlogDialogScene = new Scene(backlogDialogLayout);
      Stage backlogDialogStage = new Stage();

      if (backlog != null) {
        if (isInControllerStack(controller, backlog)) {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Dialog already open");
          alert.setHeaderText(null);
          alert.setContentText(
              "The window you are trying to open is already open in the background.");
          alert.showAndWait();
          return;
        }
      }

      controller.setupController(this, backlogDialogStage, CreateOrEdit.EDIT, backlog);
      pushControllerStack(controller);

      backlogDialogStage.initModality(Modality.APPLICATION_MODAL);
      backlogDialogStage.initOwner(stage);
      backlogDialogStage.setScene(backlogDialogScene);
      backlogDialogStage.showAndWait();

      LMPC.getScrumBoardController().refreshComboBoxes();
      LMPC.getBurndownController().refreshComboBoxes();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void showSprintDialog(CreateOrEdit createOrEdit) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/SprintDialog.fxml"));
      HBox sprintDialogLayout = loader.load();

      SprintDialogController controller = loader.getController();
      Scene sprintDialogScene = new Scene(sprintDialogLayout);
      Stage sprintDialogStage = new Stage();

      Sprint sprint = null;
      if (createOrEdit == CreateOrEdit.EDIT) {
        sprint = (Sprint) LMPC.getSelected();
        if (sprint == null) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Nothing selected");
          alert.setHeaderText(null);
          alert.setContentText("No sprint selected.");
          alert.showAndWait();
          return;
        }
      }

      if (isInControllerStack(controller, sprint)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return;
      }

      controller.setupController(this, sprintDialogStage, createOrEdit, sprint);
      pushControllerStack(controller);

      sprintDialogStage.initModality(Modality.APPLICATION_MODAL);
      sprintDialogStage.initOwner(primaryStage);
      sprintDialogStage.setScene(sprintDialogScene);
      sprintDialogStage.showAndWait();

      LMPC.getScrumBoardController().refreshComboBoxes();
      LMPC.getBurndownController().refreshComboBoxes();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * sets up the dialog box for editing a task when opened from another dialog
   *
   * @param taskable the object which owns/will own the task (it is not stored in Main)
   * @param task the task that to view or edit (null if creating)
   * @param team the team of the sprint which will contain the task (non-null if story in sprint)
   * @param createOrEdit Whether editing or creating the task
   * @param stage the stage it is currently on to void unusual behaviour
   * @return the UndoRedo instance representing a task edit (null in all other cases)
   */
  public UndoRedo showTaskDialog(Taskable taskable, Task task, Team team,
                                 CreateOrEdit createOrEdit, Stage stage) {
    UndoRedo taskUndoRedo = null;
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("/TaskDialog.fxml"));
      VBox taskDialogLayout = loader.load();

      TaskDialogController controller = loader.getController();
      Scene taskDialogScene = new Scene(taskDialogLayout);
      Stage taskDialogStage = new Stage();

      if (isInControllerStack(controller, task)) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog already open");
        alert.setHeaderText(null);
        alert.setContentText("The window you are trying to open is already open in the background.");
        alert.showAndWait();
        return taskUndoRedo;
      }

      controller.setupController(this, taskable, team, taskDialogStage, createOrEdit, task);
      pushControllerStack(controller);

      taskDialogStage.initModality(Modality.APPLICATION_MODAL);
      taskDialogStage.initOwner(stage);
      taskDialogStage.setScene(taskDialogScene);
      taskDialogStage.showAndWait();

      this.getLMPC().getScrumBoardController().refreshTaskLists();
      this.getLMPC().getBurndownController().refreshComboBoxes();
      taskUndoRedo = controller.getUndoRedoObject();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return taskUndoRedo;
  }

  /**
   * Sets the cloned lists for revert to be what it is now.
   */
  public void setLastSaved() {
    revertHandler.setLastSaved();
  }

  /**
   * Reverts the current state to the last saved state.
   */
  public void revert() {
    revertHandler.revert();
    LMPC.getScrumBoardController().hardReset();
    LMPC.getBurndownController().hardReset();
  }

  /**
   * Undo last action
   */
  public void undo() {
    try {
      undoRedoHandler.undo();
      toggleName();
      checkUndoRedoItems();
      LMPC.getScrumBoardController().refreshComboBoxes();
      LMPC.getBurndownController().refreshComboBoxes();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Redo last undo
   */
  public void redo() {
    try {
      undoRedoHandler.redo();
      toggleName();
      checkUndoRedoItems();
      LMPC.getScrumBoardController().refreshComboBoxes();
      LMPC.getBurndownController().refreshComboBoxes();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Undo an action which was not stored on the stacks. Use with care.
   *
   * @param undoRedo The UndoRedo instance to undo.
   */
  public void quickUndo(UndoRedo undoRedo) {
    try {
      undoRedoHandler.quickUndo(undoRedo);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Add a new action to the undo/redo stack
   *
   * @param undoRedoObject Action to store
   */
  public void newAction(UndoRedo undoRedoObject) {
    undoRedoHandler.newAction(undoRedoObject);
    toggleName();
    checkUndoRedoItems();
  }

  /**
   * Refresh the last saved object to be the newest action on the undo stack
   */
  public void refreshLastSaved() {
    lastSavedObject = undoRedoHandler.peekUndoStack();
    toggleName();
    checkUndoRedoItems();
  }

  /**
   * Check if the newest action was the saved action and adjust the window title
   *
   * @return Boolean returns true if the project is saved returns true else false
   */
  public boolean checkSaved() {
    UndoRedo topObject = undoRedoHandler.peekUndoStack();

    boolean neverSaved = lastSavedObject == null && topObject == null;

    // Adjust the window title
    if (neverSaved || topObject == lastSavedObject) {
      return true;
    }
    return false;
  }

  public void toggleName() {
    if (checkSaved()) {
      primaryStage.setTitle(mainTitle);
    } else {
      primaryStage.setTitle(mainTitle + " *");
    }
  }

  /**
   * Delete a project from the list of projects
   *
   * @param inputProject Project to delete - must be same object reference
   */
  public void deleteProject(Project inputProject) {
    projects.remove(inputProject);
  }

  /**
   * Delete a person from the list of people
   *
   * @param inputPerson Person to delete - must be the same object reference
   */
  public void deletePerson(Person inputPerson) {
    people.remove(inputPerson);
  }

  /**
   * Delete a skill from the list of skills
   *
   * @param inputSkill Skill to delete - must be the same object reference
   */
  public void deleteSkill(Skill inputSkill) {
    skills.remove(inputSkill);
  }

  /**
   * Delete a team from the list of teams
   *
   * @param inputTeam Team to delete - must be the same object reference
   */
  public void deleteTeam(Team inputTeam) {
    teams.remove(inputTeam);
  }

  /**
   * Delete a release from the list of releases
   *
   * @param inputRelease release to be deleted
   */
  public void deleteRelease(Release inputRelease) {
    releases.remove(inputRelease);
  }

  /**
   * Delete a story from the list of stories.
   *
   * @param inputStory Story to be deleted.
   */
  public void deleteStory(Story inputStory) {
    stories.remove(inputStory);
  }

  /**
   * Delete a backlog from the list of backlogs.
   *
   * @param inputBacklog Backlog to be deleted.
   */
  public void deleteBacklog(Backlog inputBacklog) {
    backlogs.remove(inputBacklog);
  }

  /**
   * Delete a sprint from the list of sprints.
   *
   * @param inputSprint Backlog to be deleted.
   */
  public void deleteSprint(Sprint inputSprint) {
    sprints.remove(inputSprint);
  }

  /**
   * Generate an UndoRedoObject to place in the stack
   *
   * @param action    The action to store in the object
   * @param agileItem The item to store in the object
   * @return the UndoRedoObject to store
   */
  private UndoRedoObject generateDelUndoRedoObject(Action action, AgileItem agileItem) {
    UndoRedoObject undoRedoObject = new UndoRedoObject();

    undoRedoObject.setAction(action);

    // Store a copy of object in stack to avoid reference problems
    AgileItem itemToStore;
    switch (action) {
      case PROJECT_DELETE:
        itemToStore = new Project((Project) agileItem);
        break;
      case PERSON_DELETE:
        itemToStore = new Person((Person) agileItem);
        break;
      case SKILL_DELETE:
        itemToStore = new Skill((Skill) agileItem);
        break;
      case TEAM_DELETE:
        itemToStore = new Team((Team) agileItem);
        break;
      case RELEASE_DELETE:
        itemToStore = new Release((Release) agileItem);
        break;
      case STORY_DELETE:
        itemToStore = new Story((Story) agileItem);
        break;
      case BACKLOG_DELETE:
        itemToStore = new Backlog((Backlog) agileItem);
        break;
      case SPRINT_DELETE:
        itemToStore = new Sprint((Sprint) agileItem);
        break;
      default:
        itemToStore = null; // should never happen
    }

    undoRedoObject.setAgileItem(agileItem); // store original
    undoRedoObject.addDatum(itemToStore);   // store clone

    return undoRedoObject;
  }

  /**
   * Generic delete function which deletes an item from the appropriate list and then adds the
   * action to the undo/redo stack
   *
   * The objects that have a cascading delete in the are teams and backlogs.
   *
   * @param agileItem Item to delete
   */
  public void delete(AgileItem agileItem) {
    String listType = LMPC.getCurrentListType();
    UndoRedoObject undoRedoObject;
    switch (listType) {
      case "Projects":
        Project project = (Project) agileItem;

        ArrayList<Release> projectsReleases = new ArrayList<>();
        ArrayList<Sprint> projectsSprints = new ArrayList<>();

        for (Release release : releases) {
          if (release.getProjectRelease().equals(project)) {
            projectsReleases.add(release);
          }
        }
        for (Sprint sprint : sprints) {
          if (sprint.getSprintProject().equals(project)) {
            projectsSprints.add(sprint);
          }
        }
        if (!projectsReleases.isEmpty()) {
          //if so open a yes/no dialog (note can't have sprints on project without releases)
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          alert.setTitle("Releases still exist for this project");
          alert.setHeaderText(null);
          String contentText = String.format("Do you want to delete project '%s' and its "
                                         + "associated items?", project.getLabel());
          alert.setResizable(true);
          alert.setContentText(contentText);

          String message = "Releases:\n";
          for (Release release : projectsReleases) {
            message += String.format("\t%s\n", release.getLabel());
          }
          // Extending message for sprints
          if (!projectsSprints.isEmpty()) {
            message += "Sprints:\n";
            for (Sprint sprintDelete : projectsSprints) {
              if (sprintDelete.getSprintFullName().equals("")) {
                message += String.format("\t%s - [No name]\n",
                                         sprintDelete.getLabel());
              } else {
                message += String.format("\t%s - %s\n",
                                         sprintDelete.getLabel(),
                                         sprintDelete.getSprintFullName());
              }
            }
          }

          Label label = new Label("The affected items are:");
          TextArea textArea = new TextArea(message);
          textArea.setEditable(false);
          textArea.setWrapText(true);

          textArea.setMaxWidth(Double.MAX_VALUE);
          textArea.setMaxHeight(Double.MAX_VALUE);
          GridPane.setVgrow(textArea, Priority.ALWAYS);
          GridPane.setHgrow(textArea, Priority.ALWAYS);

          GridPane expContent = new GridPane();
          expContent.setMaxWidth(Double.MAX_VALUE);
          expContent.add(label, 0, 0);
          expContent.add(textArea, 0, 1);

          // Set expandable Exception into the dialog pane.
          alert.getDialogPane().setExpandableContent(expContent);

          //checks response
          Optional<ButtonType> result = alert.showAndWait();
          if (result.get() == ButtonType.OK) {
            for (Release release : projectsReleases) {
              deleteRelease(release);
            }
            for (Sprint sprint : projectsSprints) {
              if (sprints.contains(sprint)) {
                deleteSprint(sprint);
              }
            }
            deleteProject(project);
          } else {
            return;
          }
        } else {
          deleteProject(project);
        }
        undoRedoObject = generateDelUndoRedoObject(Action.PROJECT_DELETE, agileItem);
        for (Release release : projectsReleases) {
          undoRedoObject.addDatum(release);
        }
        for (Sprint sprint : projectsSprints) {
          undoRedoObject.addDatum(sprint);
        }
        newAction(undoRedoObject);
        break;
      case "People":
        Person person = (Person) agileItem;
        Boolean inBacklog = false;
        Boolean inStory = false;
        Boolean inTask = false;
        Boolean inEffort = false;
        List<Task> taskList = new ArrayList<>();

        for (Story story : getStories()) {
          if (story.getCreator() == person) {
            inStory = true;
            break;
          }
          for (Task task : story.getTasks()) {
            if (task.getTaskPeople().contains(person)) {
              inTask = true;
              taskList.add(task);
            }
            if (!inEffort) {
              for (Effort effort : task.getEfforts()) {
                if (effort.getWorker().equals(person)) {
                  inEffort = true;
                  break;
                }
              }
            }
          }
        }
        for (Sprint sprint : getSprints()) {
          for (Task task : sprint.getTasks()) {
            if (task.getTaskPeople().contains(person)) {
              inTask = true;
              taskList.add(task);
            }
            if (!inEffort) {
              for (Effort effort : task.getEfforts()) {
                if (effort.getWorker().equals(person)) {
                  inEffort = true;
                  break;
                }
              }
            }
          }
        }

        if (inEffort) {
          String message =
              "This person has contributed effort to the organisation and therefore cannot be "
              + "removed from the system.";
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Person has logged effort");
          alert.setHeaderText(null);
          alert.setContentText(message);
          alert.getDialogPane().setPrefSize(500, 130);
          alert.showAndWait();
          break;
        }

        if(inStory) {
          String message =
              "This person is the creator of a story. You cannot delete them until you delete the "
              + "story created by this person.";
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Person is a story creator");
          alert.setHeaderText(null);
          alert.setContentText(message);
          alert.getDialogPane().setPrefSize(500, 130);
          alert.showAndWait();
          break;
        }

        for (Backlog backlog : getBacklogs()) {
          if (backlog.getProductOwner() == person) {
            inBacklog = true;
          }
        }

        if(inBacklog) {
          String message =
              "This person is the product owner of a backlog. You must un-assign them from the backlog"
              + " before you can delete them.";
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Person is a backlog product owner");
          alert.setHeaderText(null);
          alert.setContentText(message);
          alert.getDialogPane().setPrefSize(500, 130);
          alert.showAndWait();
          break;
        }

        CompositeUndoRedo compositeUndoRedo =
            new CompositeUndoRedo(Action.getActionString(Action.PERSON_DELETE));
        if (person.isInTeam()) {
          String message;
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          if (inTask) {
            message = String.format(
                "Do you want to delete '%s', remove them from team '%s' and un-assign"
                + " them from their assigned tasks?",
                person.getLabel(),
                person.getTeamLabel());
            alert.getDialogPane().setPrefSize(500, 130);
            alert.setTitle("Person is in team and assigned to tasks");
          } else {
            message = String.format(
                "Do you want to delete '%s' and remove them from team '%s'?",
                person.getLabel(),
                person.getTeamLabel());
            alert.getDialogPane().setPrefSize(500, 130);
            alert.setTitle("Person is in team");
          }
          alert.setHeaderText(null);
          alert.setContentText(message);
          //checks response
          Optional<ButtonType> result = alert.showAndWait();
          if (result.get() == ButtonType.OK) {
            //if yes then remove
            person.getTeam().getTeamMembers().remove(person);
            if (inTask) {
              for (Task task : taskList) {
                Task before = new Task(task);
                task.removeTaskPerson(person);
                Task after = new Task(task);
                UndoRedo taskEdit = new UndoRedoObject();
                taskEdit.setAction(Action.TASK_EDIT);
                taskEdit.setAgileItem(task);
                taskEdit.addDatum(before);
                taskEdit.addDatum(after);
                compositeUndoRedo.addUndoRedo(taskEdit);
              }
            }
            deletePerson(person);
          } else {
            return;
          }
        } else {
          deletePerson(person);
        }
        undoRedoObject = generateDelUndoRedoObject(Action.PERSON_DELETE, agileItem);
        compositeUndoRedo.addUndoRedo(undoRedoObject);
        newAction(compositeUndoRedo);
        break;
      case "Skills":
        Skill skill = (Skill) agileItem;
        ArrayList<Person> skillUsers = new ArrayList<>();
        //iterate through each person
        for (Person skillPerson : people) {
          //check if they have the skill
          if (skillPerson.getSkillSet().contains(skill)) {
            skillUsers.add(skillPerson);
          }
        }
        if (!skillUsers.isEmpty()) {
          //if so open a yes/no dialog
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          alert.setTitle("People have this skill");
          alert.setHeaderText(null);
          alert.getDialogPane().setPrefSize(500, 130);
          int messageLength = 1;
          String message = String.format("Do you want to delete skill '%s' and remove it from:\n\n",
                                         skill.getLabel());
          for (Person skillUser : skillUsers) {
            messageLength++;
            message += String.format("%s - %s %s\n",
                                     skillUser.getLabel(),
                                     skillUser.getFirstName(),
                                     skillUser.getLastName());
          }
          alert.getDialogPane().setPrefHeight(60 + 30 * messageLength);
          alert.setContentText(message);
          //checks response
          Optional<ButtonType> result = alert.showAndWait();
          if (result.get() == ButtonType.OK) {
            //if yes then remove skill from all who have it
            for (Person skillUser : skillUsers) {
              skillUser.getSkillSet().remove(skill);
            }
            //after all people have this skill removed delete the skill object
            deleteSkill(skill);
          } else {
            return;
          }
        } else {
          deleteSkill(skill);
        }
        undoRedoObject = generateDelUndoRedoObject(Action.SKILL_DELETE, agileItem);
        for (Person skillUser : skillUsers) {
          // Add data so users can get the skill back after undo
          undoRedoObject.addDatum(skillUser);
        }
        newAction(undoRedoObject);
        break;
      case "Teams":
        Team team = (Team) agileItem;
        // Check if team assigned to a sprint
        List<Sprint> teamSprints = new ArrayList<>();
        for (Sprint sprint : getSprints()) {
          if (sprint.getSprintTeam().equals(team)) {
            teamSprints.add(sprint);
          }
        }
        if (!teamSprints.isEmpty()) {
          Alert alert = new Alert(Alert.AlertType.WARNING);
          alert.setHeaderText(null);
          alert.setResizable(false);
          alert.getDialogPane().setPrefSize(500, 130);
          alert.setTitle("Team assigned to a Sprint");
          String content = String.format("You are attempting to delete a team that"
                                         + " is allocated to a sprint.\nPlease remove "
                                         + "'%s' from the following sprints before deleting it:\n",
                                         team.toString());
          for (Sprint sprint : teamSprints) {
            if (sprint.getSprintFullName().isEmpty()) {
              content += String.format("\t%s - [No name]\n", sprint.toString());
            } else {
              content += String.format("\t%s - %s\n", sprint.toString(), sprint.getSprintFullName());
            }
          }
          alert.getDialogPane().setPrefSize(510, 150 + teamSprints.size() * 20);

          alert.getDialogPane().setContentText(content);
          alert.showAndWait();
        } else if (team.getTeamMembers().isEmpty()) {
          deleteTeam(team);
          undoRedoObject = generateDelUndoRedoObject(Action.TEAM_DELETE, agileItem);
          newAction(undoRedoObject);
        } else {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          alert.setTitle("Team contains people");
          alert.setHeaderText(null);
          alert.getDialogPane().setPrefSize(500, 130);
          int messageLength = 1;
          String
              message =
              String.format("Are you sure that you want to delete team '%s' and people:\n\n",
                            team.getLabel());
          for (Person teamMember : team.getTeamMembers()) {
            messageLength++;
            message += String.format("%s - %s %s\n",
                                     teamMember.getLabel(),
                                     teamMember.getFirstName(),
                                     teamMember.getLastName());
          }
          alert.getDialogPane().setPrefHeight(60 + 25 * messageLength);
          alert.setContentText(message);

          Optional<ButtonType> result = alert.showAndWait();
          if (result.get() == ButtonType.OK) {
            for (Person teamPerson : team.getTeamMembers()) {
              deletePerson(teamPerson);
            }
            deleteTeam(team);
            undoRedoObject = generateDelUndoRedoObject(Action.TEAM_DELETE, agileItem);
            newAction(undoRedoObject);
          }
        }
        break;
      case "Releases":
        Release release = (Release) agileItem;
        // Check if release assigned to a sprint
        List<Sprint> releaseSprints = new ArrayList<>();
        for (Sprint sprint : getSprints()) {
          if (sprint.getSprintRelease().equals(release)) {
            releaseSprints.add(sprint);
          }
        }
        if (!releaseSprints.isEmpty()) {
          Alert alert = new Alert(Alert.AlertType.WARNING);
          alert.setHeaderText(null);
          alert.setResizable(false);
          alert.getDialogPane().setPrefSize(500, 130);
          String content = String.format("You are attempting to delete a release that"
                                         + " is allocated to a sprint.\nPlease remove "
                                         + "'%s' from the following sprints before deleting it:\n",
                                         release.toString());
          for (Sprint sprint : releaseSprints) {
            if (sprint.getSprintFullName().isEmpty()) {
              content += String.format("\t%s - [No name]\n", sprint.toString());
            } else {
              content += String.format("\t%s - %s\n", sprint.toString(), sprint.getSprintFullName());
            }
          }
          alert.getDialogPane().setPrefSize(510, 150 + releaseSprints.size() * 20);

          alert.getDialogPane().setContentText(content);
          alert.showAndWait();
        } else {
          deleteRelease(release);
          undoRedoObject = generateDelUndoRedoObject(Action.RELEASE_DELETE, agileItem);
          newAction(undoRedoObject);
        }
        break;
      case "Stories":
        Story story = (Story) agileItem;
        Backlog estimateBacklog = new Backlog();
        Backlog storyBacklog = null;
        for (Backlog backlog : backlogs) {
          if (backlog.getStories().contains(story)) {
            estimateBacklog.copyValues(backlog);
            storyBacklog = backlog;
            break;
          }
        }
        if (storyBacklog != null) {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          alert.setTitle("Story is in a Backlog");
          alert.setHeaderText(null);
          alert.getDialogPane().setPrefSize(500, 130);
          Sprint storySprint = null;
          for (Sprint sprint : getSprints()) {
            if (sprint.getSprintStories().contains(story)) {
              storySprint = sprint;
              break;
            }
          }
          String message;
          if (storySprint != null) {
            message =
                String.format("Do you want to delete '%s' and remove it from\n\t Backlog: '%s'\n\t"
                              + " and Sprint: '%s'?",
                              story.getLabel(), storyBacklog.getLabel(), storySprint.getLabel());
            alert.getDialogPane().setPrefHeight(150);
          } else {
            message =
                String.format("Do you want to delete '%s' and remove it from\n\t Backlog: '%s'?",
                              story.getLabel(), storyBacklog.getLabel());
            alert.getDialogPane().setPrefHeight(125);
          }
          alert.setContentText(message);

          //checks response
          Optional<ButtonType> result = alert.showAndWait();
          if (result.get() == ButtonType.OK) {
            //if yes then remove story from the backlog
            storyBacklog.removeStory(story);
          } else {
            return;
          }
        }

        deleteStory(story);
        undoRedoObject = generateDelUndoRedoObject(Action.STORY_DELETE, agileItem);
        undoRedoObject.addDatum(storyBacklog);
        undoRedoObject.addDatum(estimateBacklog);
        // The stories that were dependent on this story
        for (Story mainStory : getStories()) {
          if (mainStory.getDependencies().contains(story)) {
            mainStory.removeDependency(story);
            undoRedoObject.addDatum(mainStory);
          }
        }
        newAction(undoRedoObject);
        break;
      case "Backlogs":
        Backlog backlog = (Backlog) agileItem;
        if (backlog.getStories().isEmpty()) {
          deleteBacklog(backlog);
          undoRedoObject = generateDelUndoRedoObject(Action.BACKLOG_DELETE, agileItem);
          newAction(undoRedoObject);
        } else {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          alert.setTitle("Backlog contains stories");
          alert.setHeaderText(null);
          String contentText = String.format("Are you sure you want to delete backlog '%s' and "
                                             + "its associated items?",
                                             backlog.getLabel());
          alert.setContentText(contentText);
          alert.setResizable(true);
          alert.getDialogPane().setPrefSize(500, 130);
          int messageLength = 2;
          String message = "";
          message += "Stories:\n";
          for (Story blStory : backlog.getStories()) {
            messageLength++;
            if (blStory.getStoryName().equals("")) {
              message += String.format("\t%s - [No name]\n",
                                       blStory.getLabel());
            } else {
              message += String.format("\t%s - %s\n",
                                       blStory.getLabel(),
                                       blStory.getStoryName());
            }
          }
          // Finding all sprints associated with backlog
          List<Sprint> deleteSprints = new ArrayList<>();
          for (Sprint blSprint : getSprints()) {
            if (blSprint.getSprintBacklog().equals(backlog)) {
              deleteSprints.add(blSprint);
            }
          }
          // Extending message
          if (!deleteSprints.isEmpty()) {
            messageLength ++;
            message += "Sprints:\n";
            for (Sprint sprintDelete : deleteSprints) {
              messageLength++;
              if (sprintDelete.getSprintFullName().equals("")) {
                message += String.format("\t%s - [No name]\n",
                                         sprintDelete.getLabel());
              } else {
                message += String.format("\t%s - %s\n",
                                         sprintDelete.getLabel(),
                                         sprintDelete.getSprintFullName());
              }
            }
          }
          // Finding proj associated with backlog
          Project backlogProject = null;
          for (Project backProj : getProjects()) {
            if (backProj.getBacklog() != null && backProj.getBacklog().equals(backlog)){
              backlogProject = backProj;
              break;
            }
          }
          // Extending message
          if (backlogProject != null) {
            messageLength++;
            message += String.format("\nThese will un-assign from Project: %s", backlogProject.getLabel());
          }

          Label label = new Label("The affected items are:");
          TextArea textArea = new TextArea(message);
          textArea.setEditable(false);
          textArea.setWrapText(true);

          textArea.setMaxWidth(Double.MAX_VALUE);
          textArea.setMaxHeight(Double.MAX_VALUE);
          GridPane.setVgrow(textArea, Priority.ALWAYS);
          GridPane.setHgrow(textArea, Priority.ALWAYS);

          GridPane expContent = new GridPane();
          expContent.setMaxWidth(Double.MAX_VALUE);
          expContent.add(label, 0, 0);
          expContent.add(textArea, 0, 1);

          // Set expandable Exception into the dialog pane.
          alert.getDialogPane().setExpandableContent(expContent);

          Optional<ButtonType> result = alert.showAndWait();
          if (result.get() == ButtonType.OK) {
            for (Story blstory : backlog.getStories()) {
              deleteStory(blstory);
            }
            // Deleting associated sprints
            for (Sprint sprint : deleteSprints) {
              deleteSprint(sprint);
            }
            // Unassign associated project
            if (backlogProject != null) {
              backlogProject.setBacklog(null);
            }
            deleteBacklog(backlog);
            undoRedoObject = generateDelUndoRedoObject(Action.BACKLOG_DELETE, agileItem);
            undoRedoObject.addDatum(backlogProject);
            for (Sprint sprint : deleteSprints) {
              undoRedoObject.addDatum(sprint);
            }
            newAction(undoRedoObject);
          }
        }
        break;
      case "Sprints":
        Sprint sprint = (Sprint) agileItem;
        deleteSprint(sprint);
        undoRedoObject = generateDelUndoRedoObject(Action.SPRINT_DELETE, agileItem);
        newAction(undoRedoObject);
        break;
      default:
//        System.err.println("Unhandled case for deleting agile item");
        break;
    }
    LMPC.getScrumBoardController().refreshComboBoxes();
    LMPC.getBurndownController().refreshComboBoxes();
  }

  /**
   * Reset main to its original state
   */
  public void resetAll() {
    undoRedoHandler.clearStacks();
    lastSavedObject = null;
    projects.clear();
    teams.clear();
    people.clear();
    skills.clear();
    releases.clear();
    roles.clear();
    stories.clear();
    backlogs.clear();
    estimates.clear();
    sprints.clear();
    nonRemovable.clear();
  }

  public Stage getPrimaryStage() {
    return primaryStage;
  }

  public ListMainPaneController getLMPC() {
    return LMPC;
  }

  public void setLMPC(ListMainPaneController LMPC) {
    // This is for tests
    this.LMPC = LMPC;
  }

  public void setPrimaryStage(Stage primaryStage) {
    // This is for tests
    this.primaryStage = primaryStage;
  }

  public MenuBarController getMBC() {
    return MBC;
  }

  public void setMBC(MenuBarController MBC) {
    // This is for tests
    this.MBC = MBC;
  }

  public void setTBC(ToolBarController TBC) {
    // This is for tests
    this.TBC = TBC;
  }

  public ObservableList<Project> getProjects() {
    return projects.sorted(Comparator.<Project>naturalOrder());
  }

  public ObservableList<Team> getTeams() {
    return teams.sorted(Comparator.<Team>naturalOrder());
  }

  public ObservableList<Person> getPeople() {
    return people.sorted(Comparator.<Person>naturalOrder());
  }

  public ObservableList<Skill> getSkills() {
    return skills.sorted(Comparator.<Skill>naturalOrder());
  }

  public ObservableList<Release> getReleasesbydate() {
    Comparator<Release> byDate = new Comparator<Release>() {
      @Override
      public int compare(Release o1, Release o2) {
        return o1.getReleaseDate().compareTo(o2.getReleaseDate());
      }
    };
    return releases.sorted(byDate);
  }

  public ObservableList<Release> getReleases() {
    return releases.sorted(Comparator.<Release>naturalOrder());
  }

  public ObservableList<Role> getRoles() {
    return roles.sorted(Comparator.<Role>naturalOrder());
  }

  public ObservableList<Story> getStories() {
    return stories.sorted(Comparator.<Story>naturalOrder());
  }

  public ObservableList<Backlog> getBacklogs() {
    return backlogs.sorted(Comparator.<Backlog>naturalOrder());
  }


  public ObservableList<Estimate> getEstimates() {
    return estimates.sorted(Comparator.<Estimate>naturalOrder());
  }

  public ObservableList<Sprint> getSprints() {
    return sprints.sorted(Comparator.<Sprint>naturalOrder());
  }
  public ObservableList<Sprint> getSprintsByDate() {
    Comparator<Sprint> byDate = new Comparator<Sprint>() {
      @Override
      public int compare(Sprint o1, Sprint o2) {
        return o1.getSprintStart().compareTo(o2.getSprintStart());
      }
    };
    return sprints.sorted(byDate);
  }

  public ArrayList<AgileItem> getNonRemovable() {
    return nonRemovable;
  }

  public void addProject(Project project) {
    projects.add(project);
  }

  public void addPerson(Person person) {
    people.add(person);
  }

  public void addTeam(Team team) {
    teams.add(team);
  }

  public void addSkill(Skill skill) {
    skills.add(skill);
  }

  public void addRelease(Release release) {
    releases.add(release);
  }

  public void addRole(Role role) {
    this.roles.add(role);
  }

  public void addStory(Story story) {
    stories.add(story);
  }

  public void addBacklog(Backlog backlog) {
    backlogs.add(backlog);
  }

  public void addEstimate(Estimate estimate) {
    estimates.add(estimate);
  }

  public void addSprint(Sprint sprint) {
    sprints.add(sprint);
  }

  /**
   * Pushes a controller onto the openControllers stack.
   *
   * @param agileController The controller to push.
   */
  public void pushControllerStack(AgileController agileController) {
    openControllers.add(agileController);
  }

  /**
   * Pops the first controller of the openControllers stack.
   */
  public void popControllerStack() {
    openControllers.remove(openControllers.size() - 1);
  }

  /**
   * Returns whether a given AgileController is already in openControllers.
   *
   * @param agileController The controller to check.
   * @param agileItem The item that the controller is being opened for.
   * @return Returns true if the controller already exists in openControllers.
   */
  public boolean isInControllerStack(AgileController agileController, AgileItem agileItem) {
    for (AgileController controller : openControllers) {
      if (controller.getClass().equals(agileController.getClass()) &&
          agileItem != null &&
          controller.getLabel().equals(agileItem.getLabel())) {
        return true;
      }
    }
    return false;
  }

  public UndoRedoHandler getUndoRedoHandler() {
    // This is for tests
    return undoRedoHandler;
  }

  public RevertHandler getRevertHandler() {
    // This is for tests
    return revertHandler;
  }

  public void setMainTitle(String title) {
    this.mainTitle = title;
  }

  /**
   * Refreshes the list view
   *
   * @param agileItem agile item of list to refresh.
   */
  public void refreshList(AgileItem agileItem) {
    LMPC.refreshList(agileItem);
  }

  /**
   * Change the displayed list type to the type of the target and select that target item.
   * Preconditions: target is displayable in the list.
   *
   * @param target AgileItem to select.
   */
  public void selectItem(AgileItem target) {
    String listType = listTypeMap.get(target.getClass());
    MBC.showListType(listType);
    LMPC.refreshList(target);
  }

  /**
   * Refresh the undo and redo menu and toolbar items based on the state of the undo/redo handler
   */
  public void checkUndoRedoItems() {
    MBC.checkUndoRedoMenuItems(undoRedoHandler);
    TBC.checkUndoRedoToolbarButtons(undoRedoHandler);
  }

  public void setMainScene() {
    double height = primaryStage.getHeight();
    double width = primaryStage.getWidth();
    boolean maximised = primaryStage.isMaximized();
    primaryStage.setScene(mainScene);
    if (maximised) {
      primaryStage.setMaximized(true);
    } else {
      primaryStage.setHeight(height);
      primaryStage.setWidth(width);
    }
  }

  public Stage getStage() {
    return this.primaryStage;
  }
}
