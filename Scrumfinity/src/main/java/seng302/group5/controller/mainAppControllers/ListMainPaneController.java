package seng302.group5.controller.mainAppControllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import seng302.group5.Main;
import seng302.group5.model.AgileHistory;
import seng302.group5.model.AgileItem;
import seng302.group5.model.Backlog;
import seng302.group5.model.Release;
import seng302.group5.model.Role;
import seng302.group5.model.Sprint;
import seng302.group5.model.Status;
import seng302.group5.model.Story;
import seng302.group5.model.Task;
import seng302.group5.model.util.Settings;
import seng302.group5.model.Person;
import seng302.group5.model.Project;
import seng302.group5.model.Team;
import seng302.group5.model.Skill;
import seng302.group5.model.util.TimeFormat;

/**
 * Created by Michael on 3/15/2015.
 * ListManePane fxml contains styling for both List and MainPane,
 * still requires asthetic tweaks, doesn't scale properly.
 */
public class ListMainPaneController {

  @FXML private ListView listView;
  @FXML private TextFlow displayTextFlow;
  @FXML private SplitPane splitPane;
  @FXML private AnchorPane listViewPane;
  @FXML private Label listViewLabel;
  @FXML private ToggleButton temporal;
  @FXML private ScrumBoardController scrumBoardController;
  @FXML private BurndownController burndownController;
  @FXML private ImageView sortImage;
  private Main mainApp;
  private boolean isListShown = true;
  private boolean isTemporal = false;

  private Image aSortImage = new Image("ASortButton.png");
  private Image dSortImage = new Image("DSortButton.png");

  private AgileItem selectedItem;

  /**
   * Initialise the fxml, basic setup functions called.
   */
  @FXML
  private void initialize() {
    Settings.currentListType = "Projects";
    Settings.setSysDefault();
    iniActorList();
  }

  /**
   * Sets listeners to whatever is in the list.
   */
  private void iniActorList() {
    listView.getSelectionModel().selectedItemProperty().addListener(
        new ChangeListener<AgileItem>() {
          @Override
          public void changed(ObservableValue<? extends AgileItem> observableValue,
                              AgileItem previous, AgileItem next) {
            if (next != null) {
              // Will place checks to update main pane here based on item type selected
              displayInfo(next);

              selectedItem = next;
            }
          }
        }
    );

    listView.setCellFactory(listView -> new ListViewCell());
  }

  /**
   * Shows/Hides items in list.
   *
   * @param item a CheckMenuItem is used to select the current item or deselect it depending on if
   *             you're hiding or showing the list
   */
  public void showHideList(CheckMenuItem item) {
    if (!isListShown) {
      checkListType();
      item.setSelected(true);
      mainApp.getMBC().deselectList(Settings.currentListType);
    } else {
      ObservableList<AgileItem> clear = FXCollections.observableArrayList();
      listView.setItems(clear);
      isListShown = false;
      item.setSelected(false);
      mainApp.getMBC().deselectList("");
      // Hide the pane containing the list
      splitPane.getItems().remove(listViewPane);
    }
  }


  /**
   * Refreshes listView and text area text for when edits occur.
   *
   * @param agileItem agile item being edited/created.
   */
  public void refreshList(AgileItem agileItem) {
    listView.setItems(null);
    checkListType();
    selectedItem = agileItem;
    if (agileItem != null) {
      listView.getSelectionModel().select(agileItem);
      displayInfo(agileItem);
    } else {
      listView.getSelectionModel().clearSelection();
      displayTextFlow.getChildren().clear();
    }
  }

  /**
   * This refreshes the backlogs display and reselects the selected item
   * only used in the dependencies dialog controller
   */
  public void refreshBacklogDisplay() {
    Backlog bl = (Backlog) listView.getSelectionModel().getSelectedItem();
    listView.setItems(null);
    checkListType();
    listView.getSelectionModel().clearSelection();
    displayTextFlow.getChildren().clear();
    if (bl != null) {
      listView.getSelectionModel().select(bl);
    }
  }


  /**
   * Checks list type currently set as viewed list by user.
   */
  public void checkListType() {
    String listType = Settings.currentListType;
    switch (listType) {
      case "Projects":
        isListShown = true;
        temporal.setVisible(false);
        // temporal.visibleProperty().setValue(true);
        listView.setItems(mainApp.getProjects().sorted(Comparator.<Project>naturalOrder()));
        break;
      case "People":
        isListShown = true;
        temporal.setVisible(false);
        listView.setItems(mainApp.getPeople().sorted(Comparator.<Person>naturalOrder()));
        break;
      case "Skills":
        isListShown = true;
        temporal.setVisible(false);
        listView.setItems(mainApp.getSkills().sorted(Comparator.<Skill>naturalOrder()));
        break;
      case "Teams":
        isListShown = true;
        temporal.setVisible(false);
        listView.setItems(mainApp.getTeams().sorted(Comparator.<Team>naturalOrder()));
        break;
      case "Releases":
        isListShown = true;
        temporal.setVisible(true);
        if (isTemporal) {
          listView.setItems(mainApp.getReleasesbydate());
        } else {
          listView.setItems(mainApp.getReleases().sorted(Comparator.<Release>naturalOrder()));
        }
        break;
      case "Stories":
        isListShown = true;
        temporal.setVisible(false);
        listView.setItems(mainApp.getStories().sorted(Comparator.<Story>naturalOrder()));
        break;
      case "Backlogs":
        isListShown = true;
        temporal.setVisible(false);
        listView.setItems(mainApp.getBacklogs().sorted(Comparator.<Backlog>naturalOrder()));
        break;
      case "Sprints":
        isListShown = true;
        temporal.setVisible(true);
        if (isTemporal) {
          listView.setItems(mainApp.getSprintsByDate());
        } else {
          listView.setItems(mainApp.getSprints().sorted(Comparator.<Sprint>naturalOrder()));
        }
        break;
    }
    // Set the list label
    listViewLabel.setText(listType);
    // Show the pane containing the list if not already
    if (splitPane.getItems().size() < 2) {
      splitPane.getItems().add(0, listViewPane);
    }
  }

  /**
   * Gets the currently selected item in the listview.
   *
   * @return AgileItem which is the item selected in the list.
   */
  public AgileItem getSelected() {
    String listType = Settings.currentListType;
    switch (listType) {
      case "Projects":
        if (!isListShown || !listType.equals("Projects")) {
          return null;
        }
        return selectedItem;
      case "People":
        if (!isListShown || !listType.equals("People")) {
          return null;
        }
        return selectedItem;
      case "Skills":
        if (!isListShown || !listType.equals("Skills")) {
          return null;
        }
        return selectedItem;
      case "Teams":
        if (!isListShown || !listType.equals("Teams")) {
          return null;
        }
        return selectedItem;
      case "Releases":
        temporal.visibleProperty().setValue(true);
        if (!isListShown || !listType.equals("Releases")) {
          return null;
        }
        return selectedItem;
      case "Stories":
        if (!isListShown || !listType.equals("Stories")) {
          return null;
        }
        return selectedItem;
      case "Backlogs":
        if (!isListShown || !listType.equals("Backlogs")) {
          return null;
        }
        return selectedItem;
      case "Sprints":
        if (!isListShown || !listType.equals("Sprints")) {
          return null;
        }
        return selectedItem;
    }
    return null;
  }

  /**
   * Gets the current list type being displayed
   *
   * @return String, a string of the current list type.
   */
  public String getCurrentListType() {
    return Settings.currentListType;
  }

  /**
   * Takes the current selected item (next) as an AgileItem and then casts it to the correct object
   * based on what the current selected list is.
   *
   * @param next Next selected item
   */
  public void displayInfo(AgileItem next) {
    displayTextFlow.getChildren().clear();
    mainApp.getPrimaryStage().widthProperty().addListener((observable, oldValue, newValue) -> {
      displayTextFlow.setMinWidth(mainApp.getPrimaryStage().getWidth()-250);
    });
    switch (Settings.currentListType) {
      case "People":
        displayTextFlow.getChildren().clear();
        Person person = (Person) next;
        displayPeopleTextArea(person);
        break;
      case "Projects":
        displayTextFlow.getChildren().clear();
        Project project = (Project) next;
        displayProjectTextArea(project);
        break;
      case "Skills":
        displayTextFlow.getChildren().clear();
        Skill skill = (Skill) next;
        displaySkillTextArea(skill);
        break;
      case "Teams":
        displayTextFlow.getChildren().clear();
        Team team = (Team) next;
        displayTeamTextArea(team);
        break;
      case "Releases":
        displayTextFlow.getChildren().clear();
        Release release = (Release) next;
        displayReleaseTextArea(release);
        break;
      case "Stories":
        displayTextFlow.getChildren().clear();
        Story story = (Story) next;
        displayStoryTextArea(story);
        break;
      case "Backlogs":
        displayTextFlow.getChildren().clear();
        Backlog backlog = (Backlog) next;
        displayBacklogTextArea(backlog);
        break;
      case "Sprints":
        displayTextFlow.getChildren().clear();
        Sprint sprint = (Sprint) next;
        displaySprintTextArea(sprint);
        break;
    }
  }

  /**
   * Generate a Hyperlink (or rather pseudo-hyperlink with Text) which changes the displayed
   * list type and selects the target item.
   * Note that the Text class is used rather than the Hyperlink class because Hyperlink has
   * different line spacing to Text.
   *
   * @param target The target AgileItem to select.
   * @return The Text object representing the hyperlink.
   */
  private Text generateHyperlink(AgileItem target) {
    Text text = new Text(target.toString());
    text.setFill(Color.BLACK);
    text.setUnderline(true);
    text.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    text.setOnMouseClicked(event -> mainApp.selectItem(target));
    text.setLineSpacing(0);
    text.setOnMouseEntered(event -> mainApp.getPrimaryStage().getScene().setCursor(Cursor.HAND));
    text.setOnMouseExited(event -> mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT));
    return text;
  }

  /**
   * Method that takes the selected skill and displays its information in the main display pane.
   * @param skill the skills who's information will be displayed.
   */
  private void displaySkillTextArea(Skill skill) {
    Text text1 = new Text("Skill Information\n");
    text1.setFill(Color.BLACK);
    text1.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 20));

    Text text2 = new Text("Skill Label: ");
    text2.setFill(Color.BLACK);
    text2.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text text3 = new Text(skill.getLabel());
    text3.setFill(Color.BLACK);
    text3.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text text4 = new Text("\nSkill Description: \n");
    text4.setFill(Color.BLACK);
    text4.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text text5;
    if (skill.getSkillDescription().isEmpty()) {
      text5 = new Text("N/A");
      text5.setFill(Color.BLACK);
      text5.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    } else {
      text5 = new Text(skill.getSkillDescription());
      text5.setFill(Color.BLACK);
      text5.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    Text text6 = new Text("\nPeople with Skill: ");
    text6.setFill(Color.BLACK);
    text6.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    List<Person> peopleWithSkill = new ArrayList<>();
    List<Text> peopleWithSkillBody = new ArrayList<>();
    for (Person person : mainApp.getPeople()) {
      if (person.getSkillSet().contains(skill)) {
        peopleWithSkill.add(person);
      }
    }
    if (peopleWithSkill.isEmpty()) {
      peopleWithSkillBody.add(new Text("No people with skill, "
                                       + "please assign this skill to a person."));
    } else {
      for (Person person : peopleWithSkill) {
        peopleWithSkillBody.add(generateHyperlink(person));
        peopleWithSkillBody.add(new Text(", "));
      }
      peopleWithSkillBody.remove(peopleWithSkillBody.size() - 1); // remove last comma
    }
    for (Text text : peopleWithSkillBody) {
      text.setFill(Color.BLACK);
      text.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    displayTextFlow.getChildren().addAll(text1, text2, text3, text4, text5, text6);
    displayTextFlow.getChildren().addAll(peopleWithSkillBody);
  }

  /**
   * method that takes the selected person and displays there information in the main pane.
   * @param person the person who's information will be displayed.
   */
  private void displayPeopleTextArea(Person person) {
    Text text1 = new Text("Person Information\n");
    text1.setFill(Color.BLACK);
    text1.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 20));

    Text text2 = new Text("Person Label: ");
    text2.setFill(Color.BLACK);
    text2.setFont(Font.font("Helvetica",FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text text3 = new Text(person.getLabel());
    text3.setFill(Color.BLACK);
    text3.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text text4 = new Text("\nFirst Name: ");
    text4.setFill(Color.BLACK);
    text4.setFont(Font.font("Helvetica",FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text text5 = new Text(person.getFirstName());
    text5.setFill(Color.BLACK);
    text5.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text text6 = new Text("\nLast Name: ");
    text6.setFill(Color.BLACK);
    text6.setFont(Font.font("Helvetica",FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text text7 = new Text(person.getLastName());
    text7.setFill(Color.BLACK);
    text7.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text text8 = new Text("\nTeam: ");
    text8.setFill(Color.BLACK);
    text8.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text text9;
    if (person.isInTeam()) {
      text9 = generateHyperlink(person.getTeam());
    } else {
      text9 = new Text("Not Assigned.");
      text9.setFill(Color.BLACK);
      text9.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    Text skillsHeader = new Text("\nSkills: ");
    skillsHeader.setFill(Color.BLACK);
    skillsHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    List<Text> skillsBody = new ArrayList<>();
    if (person.getSkillSet().isEmpty()) {
      skillsBody.add(new Text("No skills, please assign skills."));
    } else {
      for (Skill skill : person.getSkillSet().sorted(Comparator.<Skill>naturalOrder())) {
        skillsBody.add(generateHyperlink(skill));
        skillsBody.add(new Text(", "));
      }
      skillsBody.remove(skillsBody.size() - 1); // remove last comma
    }
    for (Text text : skillsBody) {
      text.setFill(Color.BLACK);
      text.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    Text createdStoriesHeader = new Text("\nCreated Stories: ");
    createdStoriesHeader.setFill(Color.BLACK);
    createdStoriesHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    List<Story> createdStories = new ArrayList<>();
    List<Text> createdStoriesBody = new ArrayList<>();
    for (Story story : mainApp.getStories()) {
      if (person.equals(story.getCreator())) {
        createdStories.add(story);
      }
    }
    if (createdStories.isEmpty()) {
      createdStoriesBody.add(new Text("N/A"));
    } else {
      for (Story story : createdStories) {
        createdStoriesBody.add(generateHyperlink(story));
        createdStoriesBody.add(new Text(", "));
      }
      createdStoriesBody.remove(createdStoriesBody.size() - 1); // remove last comma
    }
    for (Text text : createdStoriesBody) {
      text.setFill(Color.BLACK);
      text.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    displayTextFlow.getChildren().addAll(text1, text2, text3, text4, text5, text6,
                                         text7, text8, text9, skillsHeader);
    displayTextFlow.getChildren().addAll(skillsBody);
    displayTextFlow.getChildren().addAll(createdStoriesHeader);
    displayTextFlow.getChildren().addAll(createdStoriesBody);

    Skill poSkill = null;
    for (Role role : mainApp.getRoles()) {
      if (role.getLabel().equals("PO")) {
        poSkill = role.getRequiredSkill();
        break;
      }
    }
    if (person.getSkillSet().contains(poSkill)) {
      Text ownedBacklogsHeader = new Text("\nOwned Backlogs: ");
      ownedBacklogsHeader.setFill(Color.BLACK);
      ownedBacklogsHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

      List<Backlog> ownedBacklogs = new ArrayList<>();
      List<Text> ownedBacklogsBody = new ArrayList<>();
      for (Backlog backlog : mainApp.getBacklogs()) {
        if (person.equals(backlog.getProductOwner())) {
          ownedBacklogs.add(backlog);
        }
      }
      if (ownedBacklogs.isEmpty()) {
        ownedBacklogsBody.add(new Text("N/A"));
      } else {
        for (Backlog backlog : ownedBacklogs) {
          ownedBacklogsBody.add(generateHyperlink(backlog));
          ownedBacklogsBody.add(new Text(", "));
        }
        ownedBacklogsBody.remove(ownedBacklogsBody.size() - 1); // remove last comma
      }
      for (Text text : ownedBacklogsBody) {
        text.setFill(Color.BLACK);
        text.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
      }
      displayTextFlow.getChildren().addAll(ownedBacklogsHeader);
      displayTextFlow.getChildren().addAll(ownedBacklogsBody);
    }
  }

  /**
   * Method that takes the selected project and displays its information in the main pane.
   * @param project the project who's information will be displayed.
   */
  private void displayProjectTextArea(Project project) {


    Text text1 = new Text("Project Information\n");
    text1.setFill(Color.BLACK);
    text1.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 20));

    Text text2 = new Text("Project Label: ");
    text2.setFill(Color.BLACK);
    text2.setFont(Font.font("Helvetica",FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text text3 = new Text(project.getLabel());
    text3.setFill(Color.BLACK);
    text3.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text text4 = new Text("\nProject Name: ");
    text4.setFill(Color.BLACK);
    text4.setFont(Font.font("Helvetica",FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text text5;
    if (project.getProjectName().isEmpty()) {
      text5 = new Text("N/A");
    } else {
      text5 = new Text(project.getProjectName());
    }
    text5.setFill(Color.BLACK);
    text5.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text text6 = new Text("\nProject Description: \n");
    text6.setFill(Color.BLACK);
    text6.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text text7;
    if (project.getProjectDescription().isEmpty()) {
      text7 = new Text("N/A");
      text7.setFill(Color.BLACK);
      text7.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    } else {
      text7 = new Text(project.getProjectDescription());
      text7.setFill(Color.BLACK);
      text7.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    Text text8 = new Text("\nAssigned Teams: ");
    text8.setFill(Color.BLACK);
    text8.setFont(Font.font("Helvetica",FontWeight.BOLD, FontPosture.ITALIC, 15));


    List<Text> teamsBody = new ArrayList<>();
    if (project.getAllocatedTeams().isEmpty()) {
      teamsBody.add(new Text("No Teams assigned, please assign Teams."));
    } else {
      for (AgileHistory agileHistory : project.getAllocatedTeams().sorted(
          Comparator.<AgileHistory>naturalOrder())) {
        teamsBody.add(new Text("\n"));
        teamsBody.add(generateHyperlink(agileHistory.getAgileItem()));
        String dates = agileHistory.toString().replace(agileHistory.getAgileItem().toString(), "");
        teamsBody.add(new Text(dates));
      }
    }
    for (Text text : teamsBody) {
      text.setFill(Color.BLACK);
      text.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    Text backlogHeader = new Text("\nBacklog: ");
    backlogHeader.setFill(Color.BLACK);
    backlogHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));
    Text backlogBody;
    if (project.getBacklog() == null) {
      backlogBody = new Text("N/A");
    } else {
      backlogBody = generateHyperlink(project.getBacklog());
    }
    backlogBody.setFill(Color.BLACK);
    backlogBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text releasesHeader = new Text("\nReleases: ");
    releasesHeader.setFill(Color.BLACK);
    releasesHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    List<Release> releases = new ArrayList<>();
    List<Text> releasesBody = new ArrayList<>();
    for (Release release : mainApp.getReleasesbydate()) {
      if (project.equals(release.getProjectRelease())) {
        releases.add(release);
      }
    }
    if (releases.isEmpty()) {
      releasesBody.add(new Text("No releases for this project."));
    } else {
      for (Release release : releases) {
        releasesBody.add(new Text("\n"));
        releasesBody.add(generateHyperlink(release));
        String dateFormat = "dd/MM/yyyy";
        releasesBody.add(new Text(" - " + release.getReleaseDate().format(
            DateTimeFormatter.ofPattern(dateFormat))));
      }
    }
    for (Text text : releasesBody) {
      text.setFill(Color.BLACK);
      text.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    Text sprintsHeader = new Text("\nSprints: ");
    sprintsHeader.setFill(Color.rgb(1, 0, 1));
    sprintsHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    List<Sprint> projectsSprints = new ArrayList<>();
    List<Text> sprintsBody = new ArrayList<>();
    for (Sprint sprint : mainApp.getSprintsByDate()) {
      if (project.equals(sprint.getSprintProject())) {
        projectsSprints.add(sprint);
      }
    }
    if (projectsSprints.isEmpty()) {
      sprintsBody.add(new Text("N/A"));
    } else {
      for (Sprint sprint : projectsSprints) {
        sprintsBody.add(new Text("\n"));
        sprintsBody.add(generateHyperlink(sprint));
        String dateFormat = "dd/MM/yyyy";
        sprintsBody.add(new Text(String.format(
            ": %s - %s",
            sprint.getSprintStart().format(DateTimeFormatter.ofPattern(dateFormat)),
            sprint.getSprintEnd().format(DateTimeFormatter.ofPattern(dateFormat)))));
      }
    }
    for (Text text : sprintsBody) {
      text.setFill(Color.BLACK);
      text.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    displayTextFlow.getChildren().addAll(text1, text2, text3, text4, text5, text6,
                                         text7, text8);
    displayTextFlow.getChildren().addAll(teamsBody);
    displayTextFlow.getChildren().addAll(backlogHeader, backlogBody);
    displayTextFlow.getChildren().addAll(releasesHeader);
    displayTextFlow.getChildren().addAll(releasesBody);
    displayTextFlow.getChildren().addAll(sprintsHeader);
    displayTextFlow.getChildren().addAll(sprintsBody);
  }


  /**
   * Sets the main app to the param
   *
   * @param mainApp The main application object
   */
  public void setMainApp(Main mainApp) {
    this.mainApp = mainApp;
  }

  /**
   * Calculates the median velocity of a team by getting all the teams sprints and getting the
   * median.
   * @param team the team to calcualte the median velocity for.
   * @return the median velocity of the team.
   */
  private double teamVelocity(Team team) {
    SortedSet velocities = new TreeSet<>();
    double velocity = 0.0;
    for (Sprint sprint : mainApp.getSprints()) {
      if (sprint.getSprintTeam().equals(team)) {
        velocities.add(sprintVelocity(sprint));
      }
    }
    int size = velocities.size();
    int median = size / 2;
    for (Object v : velocities) {
      if (median == 0) {
        velocity = (double) v;
      }
      median -= 1;
    }
    return velocity;
  }

  /**
   * Displays the information about a given team in the text pane.
   *
   * @param team The team to display in the text pane.
   */
  private void displayTeamTextArea(Team team) {
    Text textHeader = new Text("Team Information");
    textHeader.setFill(Color.rgb(1, 0, 1));
    textHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 20));

    Text textLabelHeader = new Text("\nTeam Label: ");
    textLabelHeader.setFill(Color.rgb(1, 0, 1));
    textLabelHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textLabelBody = new Text(team.getLabel());
    textLabelBody.setFill(Color.rgb(1, 0, 1));
    textLabelBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textVelocityHeader = new Text("\nMedian Team Velocity: ");
    textVelocityHeader.setFill(Color.rgb(1, 0, 1));
    textVelocityHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    double velocity = teamVelocity(team);

    Text textVelocityBody = new Text(String.valueOf(round(velocity, 2)) + " Story points per week.");
    textVelocityBody.setFill(Color.rgb(1, 0, 1));
    textVelocityBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textDescriptionHeader = new Text("\nTeam Description:\n");
    textDescriptionHeader.setFill(Color.rgb(1, 0, 1));
    textDescriptionHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textDescriptionBody;
    if (team.getTeamDescription().length() != 0) {
      textDescriptionBody = new Text(team.getTeamDescription());
    } else {
      textDescriptionBody = new Text("N/A");
    }
    textDescriptionBody.setFill(Color.rgb(1, 0, 1));
    textDescriptionBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textMembersHeader = new Text("\nTeam Members: ");
    textMembersHeader.setFill(Color.rgb(1, 0, 1));
    textMembersHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    List<Text> textMembersBody = new ArrayList<>();
    if (team.getTeamMembers().isEmpty()) {
      textMembersBody.add(new Text("None"));
    } else {
      for (Person member : team.getTeamMembers().sorted(Comparator.<Person>naturalOrder())) {
        textMembersBody.add(new Text("\n"));
        textMembersBody.add(generateHyperlink(member));   // get the hyperlink
        if (member.getFirstName().isEmpty() && member.getLastName().isEmpty()) {
          textMembersBody.add(new Text(" - Role: "));
        } else {
          textMembersBody.add(new Text(" - " + member.getFirstName() + " " + member.getLastName() +
                                       " - Role: "));
        }
        Role role = team.getMembersRole().get(member);
        if (role != null) {
          textMembersBody.add(new Text(role.toString()));
        } else {
          textMembersBody.add(new Text("Not assigned to a role yet."));
        }
      }
    }
    for (Text text : textMembersBody) {
      text.setFill(Color.rgb(1, 0, 1));
      text.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    Text projectsHeader = new Text("\nAssigned Projects: ");
    projectsHeader.setFill(Color.rgb(1, 0, 1));
    projectsHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    List<AgileHistory> projectsWithTeam = new ArrayList<>();
    List<Text> projectsWithTeamBody = new ArrayList<>();
    for (Project project : mainApp.getProjects()) {
      for (AgileHistory agileHistory : project.getAllocatedTeams()) {
        if (team.equals(agileHistory.getAgileItem())) {
          projectsWithTeam.add(
              new AgileHistory(project, agileHistory.getStartDate(), agileHistory.getEndDate()));
        }
      }
    }
    if (projectsWithTeam.isEmpty()) {
      projectsWithTeamBody.add(new Text("No project with team, "
                                        + "please assign this team to a project."));
    } else {
      for (AgileHistory agileHistory : projectsWithTeam) {
        projectsWithTeamBody.add(new Text("\n"));
        projectsWithTeamBody.add(generateHyperlink(agileHistory.getAgileItem()));
        String dates = agileHistory.toString().replace(agileHistory.getAgileItem().toString(), "");
        projectsWithTeamBody.add(new Text(dates));
      }
    }
    for (Text text : projectsWithTeamBody) {
      text.setFill(Color.BLACK);
      text.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    Text sprintsHeader = new Text("\nSprints: ");
    sprintsHeader.setFill(Color.rgb(1, 0, 1));
    sprintsHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    List<Sprint> teamsSprints = new ArrayList<>();
    List<Text> sprintsBody = new ArrayList<>();
    for (Sprint sprint : mainApp.getSprintsByDate()) {
      if (team.equals(sprint.getSprintTeam())) {
        teamsSprints.add(sprint);
      }
    }
    if (teamsSprints.isEmpty()) {
      sprintsBody.add(new Text("N/A"));
    } else {
      for (Sprint sprint : teamsSprints) {
        sprintsBody.add(new Text("\n"));
        sprintsBody.add(generateHyperlink(sprint));
        String dateFormat = "dd/MM/yyyy";
        sprintsBody.add(new Text(String.format(
            ": %s - %s",
            sprint.getSprintStart().format(DateTimeFormatter.ofPattern(dateFormat)),
            sprint.getSprintEnd().format(DateTimeFormatter.ofPattern(dateFormat)))));
      }
    }
    for (Text text : sprintsBody) {
      text.setFill(Color.BLACK);
      text.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    displayTextFlow.getChildren().addAll(textHeader, textLabelHeader, textLabelBody,
                                         textVelocityHeader, textVelocityBody,
                                         textDescriptionHeader, textDescriptionBody,
                                         textMembersHeader);
    displayTextFlow.getChildren().addAll(textMembersBody);
    displayTextFlow.getChildren().addAll(projectsHeader);
    displayTextFlow.getChildren().addAll(projectsWithTeamBody);
    displayTextFlow.getChildren().addAll(sprintsHeader);
    displayTextFlow.getChildren().addAll(sprintsBody);
  }

  /**
   * Returns the release velocity by calculating the all the releases sprint velocities and
   * averaging them.
   * @param release the release who's velocity is to be calculated.
   * @return the releases velocity.
   */
  private double releaseVelocity(Release release){
    int days = 0;
    double points = 0.0;
    for (Sprint sprint : mainApp.getSprints()) {
      if (sprint.getSprintRelease() == release) {
        days += 1;
        points += sprintVelocity(sprint);
      }
    }
    if (points > 0) {
      return points / days;
    } else {
      return 0;
    }
  }

  /**
   * Displays the information about a given release in the text pane.
   *
   * @param release The release to display information about.
   */
  private void displayReleaseTextArea(Release release) {
    Text textHeader = new Text("Release Information");
    textHeader.setFill(Color.rgb(1, 0, 1));
    textHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 20));

    Text textLabelHeader = new Text("\nRelease Label: ");
    textLabelHeader.setFill(Color.rgb(1, 0, 1));
    textLabelHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textLabelBody = new Text(release.getLabel());
    textLabelBody.setFill(Color.rgb(1, 0, 1));
    textLabelBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textVelocityHeader = new Text("\nRelease Velocity: ");
    textVelocityHeader.setFill(Color.rgb(1, 0, 1));
    textVelocityHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textVelocityBody = new Text(String.valueOf(round(releaseVelocity(release), 2)) + " story points per week.");
    textVelocityBody.setFill(Color.rgb(1, 0, 1));
    textVelocityBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textDescriptionHeader = new Text("\nRelease Description:\n");
    textDescriptionHeader.setFill(Color.rgb(1, 0, 1));
    textDescriptionHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textDescriptionBody;
    if (!release.getReleaseDescription().isEmpty()) {
      textDescriptionBody = new Text(release.getReleaseDescription());
    } else {
      textDescriptionBody = new Text("N/A");
    }
    textDescriptionBody.setFill(Color.rgb(1, 0, 1));
    textDescriptionBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textDateHeader = new Text("\nRelease Date: ");
    textDateHeader.setFill(Color.rgb(1, 0, 1));
    textDateHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    LocalDate date = release.getReleaseDate();
    String dateString;
    String format = "dd/MM/yyyy";
    dateString = date.format(DateTimeFormatter.ofPattern(format));

    Text textDateBody = new Text(dateString);
    textDateBody.setFill(Color.rgb(1, 0, 1));
    textDateBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textNotesHeader = new Text("\nRelease Notes:\n");
    textNotesHeader.setFill(Color.rgb(1, 0, 1));
    textNotesHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textNotesBody;
    if (!release.getReleaseNotes().isEmpty()) {
      textNotesBody = new Text(release.getReleaseNotes());
    } else {
      textNotesBody = new Text("N/A");
    }
    textNotesBody.setFill(Color.rgb(1, 0, 1));
    textNotesBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textProjectHeader = new Text("\nProject: ");
    textProjectHeader.setFill(Color.rgb(1, 0, 1));
    textProjectHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textProjectBody = generateHyperlink(release.getProjectRelease());

    Text textSprintsHeader = new Text("\nSprints: ");
    textSprintsHeader.setFill(Color.rgb(1, 0, 1));
    textSprintsHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    List<Sprint> releasesSprints = new ArrayList<>();
    List<Text> textSprintsBody = new ArrayList<>();
    for (Sprint sprint : mainApp.getSprintsByDate()) {
      if (release.equals(sprint.getSprintRelease())) {
        releasesSprints.add(sprint);
      }
    }
    if (releasesSprints.isEmpty()) {
      textSprintsBody.add(new Text("N/A"));
    } else {
      for (Sprint sprint : releasesSprints) {
        textSprintsBody.add(new Text("\n"));
        textSprintsBody.add(generateHyperlink(sprint));
        String dateFormat = "dd/MM/yyyy";
        textSprintsBody.add(new Text(String.format(
            ": %s - %s",
            sprint.getSprintStart().format(DateTimeFormatter.ofPattern(dateFormat)),
            sprint.getSprintEnd().format(DateTimeFormatter.ofPattern(dateFormat)))));
      }
    }
    for (Text text : textSprintsBody) {
      text.setFill(Color.BLACK);
      text.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    displayTextFlow.getChildren().addAll(textHeader, textLabelHeader, textLabelBody,
                                         textVelocityHeader, textVelocityBody,
                                         textDescriptionHeader, textDescriptionBody, textDateHeader,
                                         textDateBody, textNotesHeader, textNotesBody,
                                         textProjectHeader, textProjectBody, textSprintsHeader);
    displayTextFlow.getChildren().addAll(textSprintsBody);
  }

  /**
   * Displays the information about a given story in the text pane.
   *
   * @param story The story to display information about.
   */
  private void displayStoryTextArea(Story story) {
    Text textHeader = new Text("Story Information");
    textHeader.setFill(Color.rgb(1, 0, 1));
    textHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 20));

    Text textLabelHeader = new Text("\nStory Label: ");
    textLabelHeader.setFill(Color.rgb(1, 0, 1));
    textLabelHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textLabelBody = new Text(story.getLabel());
    textLabelBody.setFill(Color.rgb(1, 0, 1));
    textLabelBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textNameHeader = new Text("\nStory Name: ");
    textNameHeader.setFill(Color.rgb(1, 0, 1));
    textNameHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textNameBody = new Text(story.getStoryName());
    textNameBody.setFill(Color.rgb(1, 0, 1));
    textNameBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textDescriptionHeader = new Text("\nStory Description:\n");
    textDescriptionHeader.setFill(Color.rgb(1, 0, 1));
    textDescriptionHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textDescriptionBody;
    if (story.getDescription().length() != 0) {
      textDescriptionBody = new Text(story.getDescription());
    } else {
      textDescriptionBody = new Text("N/A");
    }
    textDescriptionBody.setFill(Color.rgb(1, 0, 1));
    textDescriptionBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textCreatorHeader = new Text("\nStory Creator: ");
    textCreatorHeader.setFill(Color.rgb(1, 0, 1));
    textCreatorHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textCreatorBody = generateHyperlink(story.getCreator());

    Text textBacklogHeader = new Text("\nBacklog: ");
    textBacklogHeader.setFill(Color.rgb(1, 0, 1));
    textBacklogHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Backlog storyBacklog = null;
    for (Backlog backlog : mainApp.getBacklogs()) {
      if (backlog.getStories().contains(story)) {
        storyBacklog = backlog;
        break;
      }
    }
    List<Text> textBacklogBody = new ArrayList<>();
    if (storyBacklog != null) {
      textBacklogBody.add(generateHyperlink(storyBacklog));
      int estimateIndex = storyBacklog.getSizes().get(story);
      String estimate = storyBacklog.getEstimate().getEstimateNames().get(estimateIndex);
      textBacklogBody.add(new Text(", Estimate: " + estimate));
    } else {
      textBacklogBody.add(new Text("N/A"));
    }
    for (Text text : textBacklogBody) {
      text.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    Text textSprintHeader = new Text("\nSprint: ");
    textSprintHeader.setFill(Color.rgb(1, 0, 1));
    textSprintHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Sprint storySprint = null;
    for (Sprint sprint : mainApp.getSprints()) {
      if (sprint.getSprintStories().contains(story)) {
        storySprint = sprint;
        break;
      }
    }
    Text textSprintBody;
    if (storySprint != null) {
      textSprintBody = generateHyperlink(storySprint);
    } else {
      textSprintBody = new Text("N/A");
      textSprintBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    Text textReadinessHeader = new Text("\nReadiness: ");
    textReadinessHeader.setFill(Color.rgb(1, 0, 1));
    textReadinessHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textReadinessBody = new Text();
    textReadinessBody.setFill(Color.rgb(1, 0, 1));
    textReadinessBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    if (story.getStoryState()) {
      textReadinessBody.setText("Ready");
    } else {
      textReadinessBody.setText("Not ready");
    }

    Text textImpedimentsHeader = new Text("\nImpediments: ");
    textImpedimentsHeader.setFill(Color.rgb(1, 0, 1));
    textImpedimentsHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textImpedimentsBody = new Text();
    if (!story.getImpediments().equals("")) {
      textImpedimentsBody.setText(story.getImpediments());
    } else {
      textImpedimentsBody.setText("N/A");
    }
    textImpedimentsBody.setFill(Color.rgb(1, 0, 1));
    textImpedimentsBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textACHeader = new Text("\nAcceptance Criteria:");
    textACHeader.setFill(Color.rgb(1, 0, 1));
    textACHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textStatusHeader = new Text("\nStatus: ");
    textStatusHeader.setFill(Color.rgb(1, 0, 1));
    textStatusHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textStatusBody = new Text(story.getStatusString());
    textStatusBody.setFill(Color.rgb(1, 0, 1));
    textStatusBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));


    displayTextFlow.getChildren().addAll(textHeader, textLabelHeader, textLabelBody,
                                         textNameHeader, textNameBody, textDescriptionHeader,
                                         textDescriptionBody, textCreatorHeader, textCreatorBody,
                                         textBacklogHeader);
    displayTextFlow.getChildren().addAll(textBacklogBody);
    displayTextFlow.getChildren().addAll(textSprintHeader, textSprintBody,
                                         textReadinessHeader, textReadinessBody,
                                         textImpedimentsHeader, textImpedimentsBody,
                                         textStatusHeader,
                                         textStatusBody, textACHeader);

    Text textACBody;
    if (story.getAcceptanceCriteria().size() != 0) {
      for (String storyAC : story.getAcceptanceCriteria()) {
        textACBody = new Text("\n• " + storyAC);
        displayTextFlow.getChildren().addAll(textACBody);
      }
    } else {
      textACBody = new Text("\nN/A");
      displayTextFlow.getChildren().addAll(textACBody);
    }

    Text textStoryTasksHeader = new Text("\nTasks");
    textStoryTasksHeader.setFill(Color.rgb(1, 0, 1));
    textStoryTasksHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));
    displayTextFlow.getChildren().add(textStoryTasksHeader);

    List<Text> storyTasksText = new ArrayList<>();
    Text textStoryTasksBody;
    Text textTaskEffortsBody;
    if (!story.getTasks().isEmpty()) {
      for (Task task : story.getTasks()) {
        textStoryTasksBody = new Text("\n• " + task + ", STATUS: " + Status.getStatusString(task.getStatus()));
        storyTasksText.add(textStoryTasksBody);
        displayTextFlow.getChildren().add(textStoryTasksBody);

        for (Person person : task.getTaskPeople()) {
          textTaskEffortsBody = new Text("\n\t" + person + ": " + TimeFormat.parseDuration(task.getPersonEffort(person)));
          displayTextFlow.getChildren().add(textTaskEffortsBody);
        }
      }
    } else {
      textStoryTasksBody = new Text("\nN/A");
      storyTasksText.add(textStoryTasksBody);
      displayTextFlow.getChildren().add(textStoryTasksBody);
    }
  }

  /**
   * Displays the information about a given backlog in the text pane.
   * Uses a textflow for formatting.
   *
   * @param backlog The backlog to display information about.
   */
  private void displayBacklogTextArea(Backlog backlog) {
    Text textHeader = new Text("Backlog Information");
    textHeader.setFill(Color.rgb(1, 0, 1));
    textHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 20));

    Text textLabelHeader = new Text("\nBacklog Label: ");
    textLabelHeader.setFill(Color.rgb(1, 0, 1));
    textLabelHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textLabelBody = new Text(backlog.getLabel());
    textLabelBody.setFill(Color.rgb(1, 0, 1));
    textLabelBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textNameHeader = new Text("\nBacklog Name: ");
    textNameHeader.setFill(Color.rgb(1, 0, 1));
    textNameHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textNameBody = new Text(backlog.getBacklogName());
    textNameBody.setFill(Color.rgb(1, 0, 1));
    textNameBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textDescriptionHeader = new Text("\nBacklog Description:\n");
    textDescriptionHeader.setFill(Color.rgb(1, 0, 1));
    textDescriptionHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textDescriptionBody;
    if (backlog.getBacklogDescription().length() != 0) {
      textDescriptionBody = new Text(backlog.getBacklogDescription());
    } else {
      textDescriptionBody = new Text("N/A");
    }
    textDescriptionBody.setFill(Color.rgb(1, 0, 1));
    textDescriptionBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textPoHeader = new Text("\nBacklog Product Owner: ");
    textPoHeader.setFill(Color.rgb(1, 0, 1));
    textPoHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textPoBody = generateHyperlink(backlog.getProductOwner());

    Text textProjectHeader = new Text("\nAssigned to Project: ");
    textProjectHeader.setFill(Color.rgb(1, 0, 1));
    textProjectHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Project backlogProject = null;
    for (Project project : mainApp.getProjects()) {
      if (backlog.equals(project.getBacklog())) {
        backlogProject = project;
        break;
      }
    }
    Text textProjectBody;
    if (backlogProject != null) {
      textProjectBody = generateHyperlink(backlogProject);
    } else {
      textProjectBody = new Text("N/A");
      textProjectBody.setFill(Color.rgb(1, 0, 1));
      textProjectBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    Text textEsHeader = new Text("\nBacklog Estimation Scale: ");
    textEsHeader.setFill(Color.rgb(1, 0, 1));
    textEsHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textEsBody;
    if (backlog.getEstimate() == null) {
      textEsBody = new Text("Null");
    }else {
      textEsBody = new Text(backlog.getEstimate().toString());
    }
    textEsBody.setFill(Color.rgb(1, 0, 1));
    textEsBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textSprintsHeader = new Text("\nSprints: ");
    textSprintsHeader.setFill(Color.rgb(1, 0, 1));
    textSprintsHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    List<Sprint> backlogsSprints = new ArrayList<>();
    List<Text> textSprintsBody = new ArrayList<>();
    for (Sprint sprint : mainApp.getSprintsByDate()) {
      if (backlog.equals(sprint.getSprintBacklog())) {
        backlogsSprints.add(sprint);
      }
    }
    if (backlogsSprints.isEmpty()) {
      textSprintsBody.add(new Text("N/A"));
    } else {
      for (Sprint sprint : backlogsSprints) {
        textSprintsBody.add(new Text("\n"));
        textSprintsBody.add(generateHyperlink(sprint));
        String dateFormat = "dd/MM/yyyy";
        textSprintsBody.add(new Text(String.format(
            ": %s - %s",
            sprint.getSprintStart().format(DateTimeFormatter.ofPattern(dateFormat)),
            sprint.getSprintEnd().format(DateTimeFormatter.ofPattern(dateFormat)))));
      }
    }
    for (Text text : textSprintsBody) {
      text.setFill(Color.BLACK);
      text.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));
    }

    Text textStoriesHeader = new Text("\nStories:");
    textStoriesHeader.setFill(Color.rgb(1, 0, 1));
    textStoriesHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    String prioritisedOrder = "Prioritised Order";
    String alphabeticalOrder = "Alphabetical Order";

    Hyperlink sortToggle = new Hyperlink(prioritisedOrder);

    displayTextFlow.getChildren().addAll(textHeader, textLabelHeader, textLabelBody,
                                         textNameHeader, textNameBody, textDescriptionHeader,
                                         textDescriptionBody, textPoHeader, textPoBody,
                                         textProjectHeader, textProjectBody,
                                         textEsHeader, textEsBody,
                                         textSprintsHeader);
    displayTextFlow.getChildren().addAll(textSprintsBody);
    displayTextFlow.getChildren().addAll(textStoriesHeader, sortToggle);

    List<Text> storiesText = new ArrayList<>();

    if (!backlog.getStories().isEmpty()) {
      for (Story story : backlog.getStories()) {

        Text t = new Text("\n⚫ "); //Need to create an object so i can color it depending on the readiness of the story. The "\uD83C\uDF11" is a BIG bullet
        t.setFill(getColor(story)); //Set the color

        int index = backlog.getSizes().get(story);
        storiesText.add(t);
        Text hyperlink = generateHyperlink(story);
        hyperlink.setFont(Font.getDefault());
        storiesText.add(hyperlink);
        storiesText.add(new Text(" - " + backlog.getEstimate().getEstimateNames().get(index)));
      }
    } else {
      storiesText.add(new Text("\nN/A"));
    }
    displayTextFlow.getChildren().addAll(storiesText);

    sortToggle.setOnAction(event -> {
      displayTextFlow.getChildren().removeAll(storiesText);
      storiesText.clear();
      if (sortToggle.getText().equals(prioritisedOrder)) {
        // Change to alphabetical order
        if (!backlog.getStories().isEmpty()) {
          SortedList<Story> sortedStories = new SortedList<>(
              FXCollections.observableArrayList(backlog.getStories()),
              Comparator.<Story>naturalOrder());

          for (Story story : sortedStories) {

            Text t = new Text("\n⚫ "); //Need to create an object so i can color it depending on the readiness of the story
            t.setFill(getColor(story)); //set color

            int index = backlog.getSizes().get(story);
            storiesText.add(t);
            Text hyperlink = generateHyperlink(story);
            hyperlink.setFont(Font.getDefault());
            storiesText.add(hyperlink);
            storiesText.add(new Text(" - " + backlog.getEstimate().getEstimateNames().get(index)));
          }
        } else {
          storiesText.add(new Text("\nN/A"));
        }
        displayTextFlow.getChildren().addAll(storiesText);
        // Change the mode
        sortToggle.setText(alphabeticalOrder);
      } else {
        // Change to prioritised order
        if (!backlog.getStories().isEmpty()) {
          for (Story story : backlog.getStories()) {

            Text t = new Text("\n⚫ "); //Need to create an object so i can color it depending on the readiness of the story
            t.setFill(getColor(story)); //set color

            int index = backlog.getSizes().get(story);
            storiesText.add(t);
            Text hyperlink = generateHyperlink(story);
            hyperlink.setFont(Font.getDefault());
            storiesText.add(hyperlink);
            storiesText.add(new Text(" - " + backlog.getEstimate().getEstimateNames().get(index)));
          }
        } else {
          storiesText.add(new Text("\nN/A"));
        }
        displayTextFlow.getChildren().addAll(storiesText);
        // Change the mode
        sortToggle.setText(prioritisedOrder);
      }
    });
  }

  /**
   * This takes in a story and returns its appropriate color depending on whether it is properly ready or not.
   * Based on dependencies.
   * @param story The story to be evaluated
   * @return The corresponding color
   */
  private Color getColor(Story story) {

    boolean dependent = false;
    Backlog bl = null;

    for (Backlog backlog : mainApp.getBacklogs()) {
      if (backlog.getStories().contains(story)) {
        bl = backlog;
        break;
      }
    }

    if (bl != null) {

      for (Story stories : bl.getStories()) {
        if (story.getDependencies().contains(stories)) {
          if (mainApp.getStories().indexOf(stories) <
              mainApp.getStories().indexOf(story)) {
            dependent = true;
            break;
          }
        }
      }

      if (dependent) {
        return Color.RED;
      } else if (story.getStoryState() && bl.getSizes().get(story) != 0) {
        return Color.rgb(0, 191, 0);
      } else if (story.getAcceptanceCriteria().size() > 0 && bl.getSizes().get(story) == 0) {
        return Color.rgb(255, 135, 0);
      } else {
        return Color.rgb(0, 0, 0, 0);
      }
    } else {
      return Color.rgb(0, 0, 0, 0);
    }
  }

  /**
   * Rounds velocity when it has a large amount of decimal places
   * pulled from stack overflow
   * https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
   * @param value the number to be rounded
   * @param places the number of decimal places
   * @return the rounded number.
   */
  public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  /**Function that calculates the velocity of a team working on a sprint.
   * @param sprint the sprint which's velocity will be calculated
   * @return the velocity as a double.
   */
  public double sprintVelocity(Sprint sprint){
    int days = 0;
    if (sprint.getSprintStart().getYear() == sprint.getSprintEnd().getYear()) {
      days = sprint.getSprintEnd().getDayOfYear() - sprint.getSprintStart().getDayOfYear();
    } else {
      days = (365- sprint.getSprintStart().getDayOfYear()) + sprint.getSprintEnd().getDayOfYear();
    }
    int points = 0;
    Map<Story, Integer> estimates = sprint.getSprintBacklog().getSizes();
    for (Map.Entry<Story, Integer> entry : estimates.entrySet())
    {
      if (sprint.getSprintStories().contains(entry.getKey())) {
        points += entry.getValue();
      }
    }
    double velocity = round((points+0.0)/(days/7.0), 2);
    return velocity;
  }

  /**
   * Displays the information about a given sprint in the text pane.
   *
   * @param sprint The sprint to display information about.
   */
  private void displaySprintTextArea(Sprint sprint) {
    Text textHeader = new Text("Sprint Information");
    textHeader.setFill(Color.rgb(1, 0, 1));
    textHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 20));

    Text textLabelHeader = new Text("\nSprint Goal: ");
    textLabelHeader.setFill(Color.rgb(1, 0, 1));
    textLabelHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textLabelBody = new Text(sprint.getLabel());
    textLabelBody.setFill(Color.rgb(1, 0, 1));
    textLabelBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textVelocityHeader = new Text("\nSprint Velocity: ");
    textVelocityHeader.setFill(Color.rgb(1, 0, 1));
    textVelocityHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    double velocity = sprintVelocity(sprint);

    Text textVelocityBody = new Text(String.valueOf(round(velocity, 2)) + " Story points per week.");
    textVelocityBody.setFill(Color.rgb(1, 0, 1));
    textVelocityBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textNameHeader = new Text("\nSprint Full Name: ");
    textNameHeader.setFill(Color.rgb(1, 0, 1));
    textNameHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textNameBody = new Text(sprint.getSprintFullName());
    textNameBody.setFill(Color.rgb(1, 0, 1));
    textNameBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textDescriptionHeader = new Text("\nSprint Description:\n");
    textDescriptionHeader.setFill(Color.rgb(1, 0, 1));
    textDescriptionHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textDescriptionBody;
    if (sprint.getSprintDescription().length() != 0) {
      textDescriptionBody = new Text(sprint.getSprintDescription());
    } else {
      textDescriptionBody = new Text("N/A");
    }
    textDescriptionBody.setFill(Color.rgb(1, 0, 1));
    textDescriptionBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textBacklogHeader = new Text("\nSprint Backlog: ");
    textBacklogHeader.setFill(Color.rgb(1, 0, 1));
    textBacklogHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textBacklogBody = generateHyperlink(sprint.getSprintBacklog());

    Text textProjectHeader = new Text("\nSprint Project: ");
    textProjectHeader.setFill(Color.rgb(1, 0, 1));
    textProjectHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textProjectBody = generateHyperlink(sprint.getSprintProject());

    Text textTeamHeader = new Text("\nAssigned Team: ");
    textTeamHeader.setFill(Color.rgb(1, 0, 1));
    textTeamHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textTeamBody = generateHyperlink(sprint.getSprintTeam());

    Text textReleaseHeader = new Text("\nPart of Release: ");
    textReleaseHeader.setFill(Color.rgb(1, 0, 1));
    textReleaseHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textReleaseBody = generateHyperlink(sprint.getSprintRelease());

    Text textImpedimentsHeader = new Text("\nImpediments: ");
    textImpedimentsHeader.setFill(Color.rgb(1, 0, 1));
    textImpedimentsHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    Text textImpedimentsBody = new Text();
    if (!sprint.getSprintImpediments().equals("")) {
      textImpedimentsBody.setText(sprint.getSprintImpediments());
    } else {
      textImpedimentsBody.setText("N/A");
    }
    textImpedimentsBody.setFill(Color.rgb(1, 0, 1));
    textImpedimentsBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    Text textStoriesHeader = new Text("\nStories:");
    textStoriesHeader.setFill(Color.rgb(1, 0, 1));
    textStoriesHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    String prioritisedOrder = "Prioritised Order";
    String alphabeticalOrder = "Alphabetical Order";

    Hyperlink sortToggle = new Hyperlink(prioritisedOrder);

    displayTextFlow.getChildren().addAll(textHeader, textLabelHeader, textLabelBody,
                                         textVelocityHeader, textVelocityBody,
                                         textNameHeader, textNameBody, textDescriptionHeader,
                                         textDescriptionBody, textBacklogHeader, textBacklogBody,
                                         textProjectHeader, textProjectBody, textTeamHeader,
                                         textTeamBody, textReleaseHeader, textReleaseBody,
                                         textImpedimentsHeader, textImpedimentsBody,
                                         textStoriesHeader, sortToggle);

    List<Text> storiesText = new ArrayList<>();

    Backlog sprintBacklog = sprint.getSprintBacklog();
    if (!sprint.getSprintStories().isEmpty()) {
      for (Story story : sprintBacklog.getStories()) {
        // TODO quick diplay fix, model still broke.
        if (sprint.getSprintStories().contains(story)) {
          int index = sprintBacklog.getSizes().get(story);
          storiesText.add(new Text("\n• "));
          Text hyperlink = generateHyperlink(story);
          hyperlink.setFont(Font.getDefault());
          storiesText.add(hyperlink);
          storiesText.add(new Text(" - " + sprintBacklog.getEstimate().getEstimateNames().get(index)));
        }
      }
    } else {
      storiesText.add(new Text("\nN/A"));
    }
    displayTextFlow.getChildren().addAll(storiesText);

    Text textSprintTasksHeader = new Text("\nTasks");
    textSprintTasksHeader.setFill(Color.rgb(1, 0, 1));
    textSprintTasksHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));
    displayTextFlow.getChildren().add(textSprintTasksHeader);

    List<Text> sprintTasksText = new ArrayList<>();
    Text textSprintTasksBody;
    Text textTaskEffortsBody;
    if (!sprint.getTasks().isEmpty()) {
      for (Task task : sprint.getTasks()) {
        textSprintTasksBody = new Text("\n• " + task + ", STATUS: " + Status.getStatusString(task.getStatus()));
        sprintTasksText.add(textSprintTasksBody);
        displayTextFlow.getChildren().add(textSprintTasksBody);

        for (Person person : task.getTaskPeople()) {
          textTaskEffortsBody = new Text("\n\t" + person + ": " + TimeFormat.parseDuration(task.getPersonEffort(person)));
          sprintTasksText.add(textTaskEffortsBody);
          displayTextFlow.getChildren().add(textTaskEffortsBody);
        }
      }
    } else {
      textSprintTasksBody = new Text("\nN/A");
      sprintTasksText.add(textSprintTasksBody);
      displayTextFlow.getChildren().add(textSprintTasksBody);
    }

    Text textDatesHeader = new Text("\nSprint Dates: ");
    textDatesHeader.setFill(Color.rgb(1, 0, 1));
    textDatesHeader.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.ITALIC, 15));

    LocalDate startDate = sprint.getSprintStart();
    LocalDate endDate = sprint.getSprintEnd();
    String format = "dd/MM/yyyy";
    String startDateString = startDate.format(DateTimeFormatter.ofPattern(format));
    String endDateString = endDate.format(DateTimeFormatter.ofPattern(format));

    Text textDatesBody = new Text(startDateString + " - " + endDateString);
    textDatesBody.setFill(Color.rgb(1, 0, 1));
    textDatesBody.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15));

    displayTextFlow.getChildren().addAll(textDatesHeader, textDatesBody);

    sortToggle.setOnAction(event -> {
      displayTextFlow.getChildren().removeAll(storiesText);
      storiesText.clear();
      if (sortToggle.getText().equals(prioritisedOrder)) {
        // Change to alphabetical order
        if (!sprint.getSprintStories().isEmpty()) {
          SortedList<Story> sortedStories = new SortedList<>(
              FXCollections.observableArrayList(sprint.getSprintStories()),
              Comparator.<Story>naturalOrder());
          removePostStory(textSprintTasksHeader, sprintTasksText, displayTextFlow);
          displayTextFlow.getChildren().removeAll(textDatesHeader, textDatesBody);
          for (Story story : sortedStories) {
            // TODO just a display fix, need to fix underlying model
            if (sprint.getSprintStories().contains(story)) {
              int index = sprintBacklog.getSizes().get(story);
              storiesText.add(new Text("\n• "));
              Text hyperlink = generateHyperlink(story);
              hyperlink.setFont(Font.getDefault());
              storiesText.add(hyperlink);
              storiesText.add(
                  new Text(" - " + sprintBacklog.getEstimate().getEstimateNames().get(index)));
            }
          }
          displayTextFlow.getChildren().addAll(storiesText);
          addPostStory(textSprintTasksHeader, sprintTasksText, displayTextFlow);
          displayTextFlow.getChildren().addAll(textDatesHeader, textDatesBody);
        } else {
          storiesText.add(new Text("\nN/A"));
          displayTextFlow.getChildren().removeAll(textDatesHeader, textDatesBody);
          removePostStory(textSprintTasksHeader, sprintTasksText, displayTextFlow);
          displayTextFlow.getChildren().addAll(storiesText);
          addPostStory(textSprintTasksHeader, sprintTasksText, displayTextFlow);
          displayTextFlow.getChildren().addAll(textDatesHeader, textDatesBody);
        }
        // Change the mode
        sortToggle.setText(alphabeticalOrder);
      } else {
        // Change to prioritised order
        if (!sprint.getSprintStories().isEmpty()) {
          removePostStory(textSprintTasksHeader, sprintTasksText, displayTextFlow);
          displayTextFlow.getChildren().removeAll(textDatesHeader, textDatesBody);
          for (Story story : sprintBacklog.getStories()) {
            // TODO just a display fix, need to fix underlying model
            if (sprint.getSprintStories().contains(story)) {
              int index = sprintBacklog.getSizes().get(story);
              storiesText.add(new Text("\n• "));
              Text hyperlink = generateHyperlink(story);
              hyperlink.setFont(Font.getDefault());
              storiesText.add(hyperlink);
              storiesText.add(
                  new Text(" - " + sprintBacklog.getEstimate().getEstimateNames().get(index)));
            }
          }
          displayTextFlow.getChildren().addAll(storiesText);
          addPostStory(textSprintTasksHeader, sprintTasksText, displayTextFlow);
          displayTextFlow.getChildren().addAll(textDatesHeader, textDatesBody);
        } else {
          storiesText.add(new Text("\nN/A"));
          removePostStory(textSprintTasksHeader, sprintTasksText, displayTextFlow);
          displayTextFlow.getChildren().removeAll(textDatesHeader, textDatesBody);
          displayTextFlow.getChildren().addAll(storiesText);
          addPostStory(textSprintTasksHeader, sprintTasksText, displayTextFlow);
          displayTextFlow.getChildren().addAll(textDatesHeader, textDatesBody);
        }
        // Change the mode
        sortToggle.setText(prioritisedOrder);
      }
    });
  }

  /**
   * Multiple places needed this exact code for Sprints display, so moved out to its own function
   * @param header Task object Text header
   * @param bodyList List of Task object's Text bodies
   * @param textFlow Text flow to remove from.
   */
  public void removePostStory(Text header, List<Text> bodyList, TextFlow textFlow) {
    textFlow.getChildren().remove(header);
    for (Text text : bodyList) {
      textFlow.getChildren().remove(text);
    }
  }
  /**
   * Mupltiple places needed this exact code for Sprints display, so moved out to its own function
   * @param header Task object Text header
   * @param bodyList List of Task object's Text bodies
   * @param textFlow Text flow to add to.
   */
  public void addPostStory(Text header, List<Text> bodyList, TextFlow textFlow) {
    textFlow.getChildren().add(header);
    for (Text text : bodyList) {
      textFlow.getChildren().add(text);
    }
  }

  /**
   * Create a dialog for creation depending on what is currently being displayed in the list
   *
   * @param event Event generated by event listener
   */
  @FXML
  protected void btnClickDirectAdd(ActionEvent event) {
    // Functionality is implemented in MenuBarController. Simply call that method.
    mainApp.getMBC().btnClickDirectAdd(event);
  }

  /**
   * A button function that toggles between temporal sort order and alphabetical sort order.
   * @param event the toggle button is activated.
   */
  @FXML
  protected void btnClickTemporalSort(ActionEvent event) {
    if (isTemporal){
      sortImage.setImage(aSortImage);
      isTemporal = false;
    } else {
      sortImage.setImage(dSortImage);
      isTemporal = true;
    }
    refreshList(selectedItem);
  }

  /**
   * Initialize the scrum board when the main application start.
   */
  public void initScrumBoard() {
    scrumBoardController.setupController(mainApp, mainApp.getStage());
  }

  public void initBurnDown() {
    burndownController.setupController(mainApp, mainApp.getStage());
  }

  public ScrumBoardController getScrumBoardController() {
    return scrumBoardController;
  }

  public BurndownController getBurndownController() {return burndownController; }

  /**
   * Allows us to override the a ListViewCell - a single cell in a ListView.
   */
  private class ListViewCell extends TextFieldListCell<Object> {

    public ListViewCell() {
      super();

      // double click for editing
      this.setOnMouseClicked(click -> {
        if (click.getClickCount() == 2 &&
            click.getButton() == MouseButton.PRIMARY &&
            !isEmpty()) {
          mainApp.getMBC().editItem(null);
        }
      });
    }
  }

}
