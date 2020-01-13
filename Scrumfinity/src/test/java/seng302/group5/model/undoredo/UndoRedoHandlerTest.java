package seng302.group5.model.undoredo;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng302.group5.Main;
import seng302.group5.controller.mainAppControllers.ListMainPaneController;
import seng302.group5.model.AgileHistory;
import seng302.group5.model.AgileItem;
import seng302.group5.model.Backlog;
import seng302.group5.model.Effort;
import seng302.group5.model.Estimate;
import seng302.group5.model.Person;
import seng302.group5.model.Project;
import seng302.group5.model.Release;
import seng302.group5.model.Skill;
import seng302.group5.model.Sprint;
import seng302.group5.model.Status;
import seng302.group5.model.Story;
import seng302.group5.model.Task;
import seng302.group5.model.Team;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for undo and redo handler
 */
public class UndoRedoHandlerTest {

  private String personLabel;
  private String firstName;
  private String lastName;
  private ObservableList<Skill> skillSet;
  private String newPersonLabel;
  private String newFirstName;
  private String newLastName;
  private ObservableList<Skill> newSkillSet;

  private String skillLabel;
  private String skillDescription;
  private String newSkillLabel;
  private String newSkillDescription;

  private String teamLabel;
  private ObservableList<Person> teamMembers;
  private String teamDescription;
  private String newTeamLabel;
  private String newTeamDescription;
  private ObservableList<Person> newTeamMembers;

  private String projectLabel;
  private String projectName;
  private String projectDescription;
  private String newProjectLabel;
  private String newProjectName;
  private String newProjectDescription;

  private String releaseLabel;
  private String releaseDescription;
  private LocalDate releaseDate;
  private String releaseNotes;
  private Project projectRelease;
  private String newReleaseLabel;
  private String newReleaseDescription;
  private LocalDate newReleaseDate;
  private String newReleaseNotes;
  private Project newProjectRelease;

  private String storyLabel;
  private String storyName;
  private String storyDescription;
  private Person storyCreator;
  private ObservableList<String> storyAC;
  private String newStoryLabel;
  private String newStoryName;
  private String newStoryDescription;
  private ObservableList<String> newStoryAC;
  private boolean isReady;

  private String backlogLabel;
  private String backlogName;
  private String backlogDescription;
  private List<Story> backlogStories;
  private Person productOwner;
  private Estimate backlogEstimate;
  private String newBacklogLabel;
  private String newBacklogName;
  private String newBacklogDescription;
  private Person newProductOwner;
  private Estimate newBacklogEstimate;

  private List<String> estimateList;
  private String estimateLabel;

  private String sprintGoal;
  private String sprintFullName;
  private String sprintDescription;
  private String sprintImpediments;
  private Team sprintTeam;
  private Backlog sprintBacklog;
  private Project sprintProject;
  private Release sprintRelease;
  private LocalDate sprintStart;
  private LocalDate sprintEnd;
  private List<Story> sprintStories;
  private String newSprintGoal;
  private String newSprintFullName;
  private String newSprintDescription;
  private String newSprintImpediments;
  private Team newSprintTeam;
  private Backlog newSprintBacklog;
  private Project newSprintProject;
  private Release newSprintRelease;
  private LocalDate newSprintStart;
  private LocalDate newSprintEnd;
  private List<Story> newSprintStories;

  private String taskLabel;
  private String taskDescription;
  private int taskEstimate;
  private Status taskStatus;
  private List<Person> taskPeople;
  private Map<Person, Integer> taskEffort;
  private String newTaskLabel;
  private String newTaskDescription;
  private int newTaskEstimate;
  private Status newTaskStatus;
  private List<Person> newTaskPeople;
  private Map<Person, Integer> newTaskEffort;

  private Person worker;
  private int spentEffort;
  private String comments;
  private LocalDateTime dateTime;
  private Person newWorker;
  private int newSpentEffort;
  private String newComments;
  private LocalDateTime newDateTime;

  private Person person;
  private Skill skill;
  private Team team;
  private Project project;
  private Release release;
  private Story story;
  private Backlog backlog;
  private Estimate estimate;
  private Sprint sprint;
  private Task task;
  private Effort effort;

  private Story dependantStory1;
  private Story dependantStory2;
  private Story dependantStory3;

  private UndoRedoHandler undoRedoHandler;
  private Main mainApp;

  @Before
  public void setUp() throws Exception {

    ListMainPaneController listMainPaneController = mock(ListMainPaneController.class);

    mainApp = new Main();
    mainApp.setLMPC(listMainPaneController);

    undoRedoHandler = mainApp.getUndoRedoHandler();
  }

  private void newPerson() {
    personLabel = "ssc55";
    firstName = "Su-Shing";
    lastName = "Chen";
    skillSet = FXCollections.observableArrayList();
    person = new Person(personLabel, firstName, lastName, skillSet);

    mainApp.addPerson(person);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.PERSON_CREATE);
    undoRedoObject.setAgileItem(person);
    undoRedoObject.addDatum(new Person(person));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void deleteNewestPerson() {
    if (person.getTeam() != null) {
      person.getTeam().getTeamMembers().remove(person);
    }
    mainApp.deletePerson(person);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.PERSON_DELETE);
    undoRedoObject.setAgileItem(person);
    undoRedoObject.addDatum(new Person(person));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void editNewestPerson() {
    Person lastPerson = new Person(person);

    newPersonLabel = "apw76";
    newFirstName = "Alex";
    newLastName = "Woo";
    newSkillSet = FXCollections.observableArrayList();

    person.setLabel(newPersonLabel);
    person.setFirstName(newFirstName);
    person.setLastName(newLastName);
    person.setSkillSet(newSkillSet);

    Person newPerson = new Person(person);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.PERSON_EDIT);
    undoRedoObject.setAgileItem(person);
    undoRedoObject.addDatum(lastPerson);
    undoRedoObject.addDatum(newPerson);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newPersonWithSkill() {
    personLabel = "ssc55";
    firstName = "Su-Shing";
    lastName = "Chen";
    skillSet = FXCollections.observableArrayList();
    skillSet.add(skill);
    person = new Person(personLabel, firstName, lastName, skillSet);
    skillSet = FXCollections.observableArrayList(skillSet); // save a copy

    mainApp.addPerson(person);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.PERSON_CREATE);
    undoRedoObject.setAgileItem(person);
    undoRedoObject.addDatum(new Person(person));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void editNewestPersonWithSkill() {
    Person lastPerson = new Person(person);

    newPersonLabel = "apw76";
    newFirstName = "Alex";
    newLastName = "Woo";
    newSkillSet = FXCollections.observableArrayList();
    newSkillSet.add(skill);

    person.setLabel(newPersonLabel);
    person.setFirstName(newFirstName);
    person.setLastName(newLastName);
    person.setSkillSet(newSkillSet);
    newSkillSet = FXCollections.observableArrayList(newSkillSet); // save a copy

    Person newPerson = new Person(person);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.PERSON_EDIT);
    undoRedoObject.setAgileItem(person);
    undoRedoObject.addDatum(lastPerson);
    undoRedoObject.addDatum(newPerson);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newSkill() {
    skillLabel = "C#";
    skillDescription = "Person can program in the C# language";
    skill = new Skill(skillLabel, skillDescription);

    mainApp.addSkill(skill);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.SKILL_CREATE);
    undoRedoObject.setAgileItem(skill);
    undoRedoObject.addDatum(new Skill(skill));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void deleteNewestSkill() {
    ArrayList<Person> skillUsers = new ArrayList<>();
    for (Person skillPerson : mainApp.getPeople()) {
      //check if they have the skill
      if (skillPerson.getSkillSet().contains(skill)) {
        skillUsers.add(skillPerson);
      }
    }
    for (Person skillUser : skillUsers) {
      skillUser.getSkillSet().remove(skill);
    }
    mainApp.deleteSkill(skill);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.SKILL_DELETE);
    undoRedoObject.setAgileItem(skill);
    undoRedoObject.addDatum(new Skill(skill));
    for (Person skillUser : skillUsers) {
      // Add data so users can get the skill back after undo
      undoRedoObject.addDatum(skillUser);
    }

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void editNewestSkill() {
    Skill lastSkill = new Skill(skill);

    newSkillLabel = "Python";
    newSkillDescription = "Person can program in the Python language";
    skill.setLabel(newSkillLabel);
    skill.setSkillDescription(newSkillDescription);

    Skill newSkill = new Skill(skill);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.SKILL_EDIT);
    undoRedoObject.setAgileItem(skill);
    undoRedoObject.addDatum(lastSkill);
    undoRedoObject.addDatum(newSkill);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newTeam() {
    teamLabel = "TheMen";
    teamMembers = FXCollections.observableArrayList();
    teamDescription = "This is a manly team";
    team = new Team(teamLabel, teamMembers, teamDescription);

    mainApp.addTeam(team);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.TEAM_CREATE);
    undoRedoObject.setAgileItem(team);
    undoRedoObject.addDatum(new Team(team));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void deleteNewestTeam() {
    for (Person teamPerson : team.getTeamMembers()) {
      mainApp.deletePerson(teamPerson);
    }
    mainApp.deleteTeam(team);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.TEAM_DELETE);
    undoRedoObject.setAgileItem(team);
    undoRedoObject.addDatum(new Team(team));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void editNewestTeam() {
    Team lastTeam = new Team(team);

    newTeamLabel = "TheWomen";
    newTeamDescription = "This is a womanly team";
    newTeamMembers = FXCollections.observableArrayList();

    team.setLabel(newTeamLabel);
    team.setTeamDescription(newTeamDescription);

    Team newTeam = new Team(team);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.TEAM_EDIT);
    undoRedoObject.setAgileItem(team);
    undoRedoObject.addDatum(lastTeam);
    undoRedoObject.addDatum(newTeam);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newTeamWithMember() {
    teamLabel = "TheMen";
    teamMembers = FXCollections.observableArrayList();
    teamMembers.add(person);
    teamDescription = "This is a manly team";
    team = new Team(teamLabel, teamMembers, teamDescription);
    person.assignToTeam(team);

    mainApp.addTeam(team);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.TEAM_CREATE);
    undoRedoObject.setAgileItem(team);
    undoRedoObject.addDatum(new Team(team));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void editNewestTeamWithMember() {
    Team lastTeam = new Team(team);

    newTeamLabel = "TheWomen";
    newTeamDescription = "This is a womanly team";
    newTeamMembers = FXCollections.observableArrayList();
    newTeamMembers.add(person);
    person.assignToTeam(team);

    team.setLabel(newTeamLabel);
    team.setTeamDescription(newTeamDescription);
    team.setTeamMembers(newTeamMembers);

    Team newTeam = new Team(team);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.TEAM_EDIT);
    undoRedoObject.setAgileItem(team);
    undoRedoObject.addDatum(lastTeam);
    undoRedoObject.addDatum(newTeam);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newStory() {
    storyLabel = "Story";
    storyName = "This is a story";
    storyDescription = "Once upon a time...";
    storyCreator = new Person("Seamus", "Sandy", "Devil", null);
    storyAC = FXCollections.observableArrayList();
    story = new Story(storyLabel, storyName, storyDescription, storyCreator, storyAC);

    mainApp.addStory(story);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.STORY_CREATE);
    undoRedoObject.setAgileItem(story);
    undoRedoObject.addDatum(new Story(story));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newStoryWithAC() {
    storyLabel = "Story";
    storyName = "This is a story";
    storyDescription = "Once upon a time...";
    storyCreator = new Person("Seamus", "Sandy", "Devil", null);
    storyAC = FXCollections.observableArrayList();
    storyAC.add("MAKE DA BIG BOI BUY DA NOODLE");
    storyAC.add("AT least I got chicken");
    storyAC.add("BY FIRE BE PURGED!!\nNunununununu");
    story = new Story(storyLabel, storyName, storyDescription, storyCreator, storyAC);

    mainApp.addStory(story);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.STORY_CREATE);
    undoRedoObject.setAgileItem(story);
    undoRedoObject.addDatum(new Story(story));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newStoryMarkedAsReady() {
    storyLabel = "Story";
    storyName = "This is a story";
    storyDescription = "Once upon a time...";
    storyCreator = new Person("Seamus", "Sandy", "Devil", null);
    storyAC = FXCollections.observableArrayList();
    storyAC.add("MAKE DA BIG BOI BUY DA NOODLE");
    storyAC.add("AT least I got chicken");
    storyAC.add("BY FIRE BE PURGED!!\nNunununununu");
    story = new Story(storyLabel, storyName, storyDescription, storyCreator, storyAC);
    story.setStoryState(true);

    mainApp.addStory(story);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.STORY_CREATE);
    undoRedoObject.setAgileItem(story);
    undoRedoObject.addDatum(new Story(story));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void deleteNewestStory() {
    mainApp.deleteStory(story);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.STORY_DELETE);
    undoRedoObject.setAgileItem(story);
    undoRedoObject.addDatum(new Story(story));

    Backlog estimateBacklog = new Backlog();
    Backlog storyBacklog = null;
    for (Backlog backlog : mainApp.getBacklogs()) {
      if (backlog.getStories().contains(story)) {
        estimateBacklog.copyValues(backlog);
        storyBacklog = backlog;
        break;
      }
    }
    // Normally there is some dialog confirmation here
    if (storyBacklog != null) {
      storyBacklog.removeStory(story);
    }

    undoRedoObject.addDatum(storyBacklog);
    undoRedoObject.addDatum(estimateBacklog);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void editNewestStory() {
    Story lastStory = new Story(story);

    newStoryLabel = "NewStory";
    newStoryName = "New story name";
    newStoryDescription = "Once upon a time... Again!";
    storyCreator = new Person("Seamus", "Sandy", "Devil", null);
    storyAC = FXCollections.observableArrayList();
    storyAC.add("AC1");
    storyAC.add("AC2");
    isReady = true;

    story.setLabel(newStoryLabel);
    story.setStoryName(newStoryName);
    story.setDescription(newStoryDescription);
    story.setCreator(storyCreator);
    story.setStoryState(isReady);
    story.setAcceptanceCriteria(storyAC);

    Story newStory = new Story(story);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.STORY_EDIT);
    undoRedoObject.setAgileItem(story);
    undoRedoObject.addDatum(lastStory);
    undoRedoObject.addDatum(newStory);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void editNewestStoryWithAC() {
    Story lastStory = new Story(story);

    newStoryLabel = "NewStory";
    newStoryName = "New story name";
    newStoryDescription = "Once upon a time... Again!";
    newStoryAC = FXCollections.observableArrayList();

    newStoryAC.add("A new bunch of acs fools");
    newStoryAC.add("These are more shizzle");

    story.setLabel(newStoryLabel);
    story.setStoryName(newStoryName);
    story.setDescription(newStoryDescription);
    story.setAcceptanceCriteria(newStoryAC);

    Story newStory = new Story(story);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.STORY_EDIT);
    undoRedoObject.setAgileItem(story);
    undoRedoObject.addDatum(lastStory);
    undoRedoObject.addDatum(newStory);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void editNewestStoryMarkedAsReady() {
    Story lastStory = new Story(story);

    newStoryLabel = "NewStory";
    newStoryName = "New story name";
    newStoryDescription = "Once upon a time... Again!";
    newStoryAC = FXCollections.observableArrayList();

    newStoryAC.add("A new bunch of acs fools");
    newStoryAC.add("These are more shizzle");

    story.setLabel(newStoryLabel);
    story.setStoryName(newStoryName);
    story.setDescription(newStoryDescription);
    story.setAcceptanceCriteria(newStoryAC);
    story.setStoryState(true);

    Story newStory = new Story(story);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.STORY_EDIT);
    undoRedoObject.setAgileItem(story);
    undoRedoObject.addDatum(lastStory);
    undoRedoObject.addDatum(newStory);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newProject() {
    projectLabel = "proj";
    projectName = "The Project's Name";
    projectDescription = "This is a description for the project";
    project = new Project(projectLabel, projectName, projectDescription);

    mainApp.addProject(project);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.PROJECT_CREATE);
    undoRedoObject.setAgileItem(project);
    undoRedoObject.addDatum(new Project(project));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void deleteNewestProject() {
    // get releases and sprints assigned to the project
    List<Release> projectsReleases = new ArrayList<>();
    List<Sprint> projectsSprints = new ArrayList<>();
    for (Release release : mainApp.getReleases()) {
      if (release.getProjectRelease().equals(project)) {
        projectsReleases.add(release);
      }
    }
    for (Sprint sprint : mainApp.getSprints()) {
      if (sprint.getSprintProject().equals(project)) {
        projectsSprints.add(sprint);
      }
    }

    mainApp.deleteProject(project);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.PROJECT_DELETE);
    undoRedoObject.setAgileItem(project);
    undoRedoObject.addDatum(new Project(project));

    // do deletion and undo/redo managing all at once (not how it works in main application)
    for (Release release : projectsReleases) {
      mainApp.deleteRelease(release);
      undoRedoObject.addDatum(release);
    }
    for (Sprint sprint : projectsSprints) {
      mainApp.deleteSprint(sprint);
      undoRedoObject.addDatum(sprint);
    }

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void editNewestProject() {
    Project lastProject = new Project(project);

    newProjectLabel = "New Proj";
    newProjectName = "The New Project's Name";
    newProjectDescription = "This is a new description";

    project.setLabel(newProjectLabel);
    project.setProjectName(newProjectName);
    project.setProjectDescription(newProjectDescription);

    Project newProject = new Project(project);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.PROJECT_EDIT);
    undoRedoObject.setAgileItem(project);
    undoRedoObject.addDatum(lastProject);
    undoRedoObject.addDatum(newProject);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newProjectWithTeamAndBacklog() {
    projectLabel = "proj";
    projectName = "The Project's Name";
    projectDescription = "This is a description for the project";
    project = new Project(projectLabel, projectName, projectDescription);
    project.addTeam(new AgileHistory(team, LocalDate.MIN, LocalDate.MAX)); // assigned forever
    project.setBacklog(backlog);

    mainApp.addProject(project);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.PROJECT_CREATE);
    undoRedoObject.setAgileItem(project);
    undoRedoObject.addDatum(new Project(project));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newRelease() {
    releaseLabel = "TheRelease";
    releaseDescription = "The descriptioning";
    releaseDate = LocalDate.of(1994, Month.JANUARY, 6);
    releaseNotes = "Wagga wagga";
    projectRelease = project;

    release = new Release(releaseLabel, releaseDescription, releaseNotes,
                          releaseDate, projectRelease);

    mainApp.addRelease(release);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.RELEASE_CREATE);
    undoRedoObject.setAgileItem(release);
    undoRedoObject.addDatum(new Release(release));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void deleteNewestRelease() {
    mainApp.deleteRelease(release);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.RELEASE_DELETE);
    undoRedoObject.setAgileItem(release);
    undoRedoObject.addDatum(new Release(release));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void editNewestRelease() {
    Release lastRelease = new Release(release);

    newReleaseLabel = "NewRelease";
    newReleaseDescription = "The new descriptioning";
    newReleaseDate = LocalDate.of(1994, Month.JULY, 6);
    newReleaseNotes = "New Wagga wagga wagga";
    newProjectRelease = project;

    release.setLabel(newReleaseLabel);
    release.setReleaseDescription(newReleaseDescription);
    release.setReleaseDate(newReleaseDate);
    release.setReleaseNotes(newReleaseNotes);
    release.setProjectRelease(projectRelease);

    Release newRelease = new Release(release);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.RELEASE_EDIT);
    undoRedoObject.setAgileItem(release);
    undoRedoObject.addDatum(lastRelease);
    undoRedoObject.addDatum(newRelease);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newBacklog() {
    backlogLabel = "Backlog";
    backlogName = "This is a backlog";
    backlogDescription = "Once upon a time...BAM!";
    productOwner = person;
    backlog = new Backlog(backlogLabel, backlogName, backlogDescription, productOwner, null);
    backlog.addStory(story);
    backlog.setEstimate(estimate);
    backlogEstimate = estimate;
    backlogStories = backlog.getStories();
    mainApp.addBacklog(backlog);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.BACKLOG_CREATE);
    undoRedoObject.setAgileItem(backlog);
    undoRedoObject.addDatum(new Backlog(backlog));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newBacklogWithSprint() {
    backlogLabel = "Backlog";
    backlogName = "This is a backlog";
    backlogDescription = "Once upon a time...BAM!";
    productOwner = person;
    backlog = new Backlog(backlogLabel, backlogName, backlogDescription, productOwner, null);
    backlog.addStory(story);
    backlog.setEstimate(estimate);
    sprint.setSprintBacklog(backlog);
    backlogEstimate = estimate;
    backlogStories = backlog.getStories();
    mainApp.addBacklog(backlog);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.BACKLOG_CREATE);
    undoRedoObject.setAgileItem(backlog);
    undoRedoObject.addDatum(new Backlog(backlog));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newEstimate() {
    estimateLabel = "Estimate";
    estimateList = Arrays.asList("Size 0", "Size 1", "Size 2", "Size 3");
    estimate = new Estimate(estimateLabel, estimateList);
  }

  private void deleteNewestBacklog() {
    mainApp.deleteBacklog(backlog);

    UndoRedoObject undoRedoObject = new UndoRedoObject();

    undoRedoObject.setAction(Action.BACKLOG_DELETE);
    undoRedoObject.setAgileItem(backlog);
    undoRedoObject.addDatum(new Backlog(backlog));
    undoRedoObject.addDatum(project);
    for (Sprint sprint : mainApp.getSprints()) {
      if (sprint.getSprintBacklog().equals(backlog)) {
        undoRedoObject.addDatum(sprint);
      }
    }
    undoRedoHandler.newAction(undoRedoObject);


  }

  private void editNewestBacklog() {
    Backlog lastBacklog = new Backlog(backlog);

    newBacklogLabel = "New backlog";
    newBacklogName = "New backlog name";
    newBacklogDescription = "Once upon a time... BAM Again!";
    newProductOwner = new Person("Jason", "Smith", "Devil", null);
    newBacklogEstimate = new Estimate("newEstimate", Arrays.asList("Est1", "Est2", "Est3"));

    backlog.setLabel(newBacklogLabel);
    backlog.setBacklogName(newBacklogName);
    backlog.setBacklogDescription(newBacklogDescription);
    backlog.setProductOwner(newProductOwner);
    backlog.setEstimate(newBacklogEstimate);

    Backlog newBacklog = new Backlog(backlog);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.BACKLOG_EDIT);
    undoRedoObject.setAgileItem(backlog);
    undoRedoObject.addDatum(lastBacklog);
    undoRedoObject.addDatum(newBacklog);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newSprint() {
    sprintGoal = "Sprint";
    sprintFullName = "This is a sprint";
    sprintDescription = "Once upon a time again";
    sprintImpediments = "Something bad has happened!";
    sprintTeam = team;
    sprintBacklog = backlog;
    sprintProject = project;
    sprintRelease = release;
    sprintStart = LocalDate.of(2000, Month.JANUARY, 4);
    sprintEnd = LocalDate.of(2000, Month.JANUARY, 25);
    sprintStories = new ArrayList<>();
    sprintStories.add(story);

    sprint = new Sprint(sprintGoal, sprintFullName, sprintDescription, backlog, project, team,
                        release, sprintStart, sprintEnd, sprintStories);
    sprint.setSprintImpediments(sprintImpediments);
    mainApp.addSprint(sprint);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.SPRINT_CREATE);
    undoRedoObject.setAgileItem(sprint);
    undoRedoObject.addDatum(new Sprint(sprint));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void deleteNewestSprint() {
    mainApp.deleteSprint(sprint);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.SPRINT_DELETE);
    undoRedoObject.setAgileItem(sprint);
    undoRedoObject.addDatum(new Sprint(sprint));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void editNewestSprint() {
    Sprint lastSprint = new Sprint(sprint);

    newSprintGoal = "New sprint";
    newSprintFullName = "This is another sprint";
    newSprintDescription = "This is some other description for a sprint";
    newSprintImpediments = "Something terrible has happened!";
    newSprintTeam = new Team("NEW TEAM", "there's no one here");
    newSprintBacklog = new Backlog("NEW BACKLOG", "newer backlog", "new desc", new Person(),
                                   new Estimate());
    newSprintProject = new Project("NEW PROJECT", "newer project", "new description");
    newSprintRelease = new Release("NEW RELEASE", "newer release", "??",
                                   LocalDate.of(2014, Month.APRIL, 1), newSprintProject);
    newSprintStart = LocalDate.of(2014, Month.FEBRUARY, 1);
    newSprintEnd = LocalDate.of(2014, Month.FEBRUARY, 20);
    newSprintStories = new ArrayList<>(); // empty

    sprint.setSprintGoal(newSprintGoal);
    sprint.setSprintFullName(newSprintFullName);
    sprint.setSprintDescription(newSprintDescription);
    sprint.setSprintImpediments(newSprintImpediments);
    sprint.setSprintTeam(newSprintTeam);
    sprint.setSprintBacklog(newSprintBacklog);
    sprint.setSprintProject(newSprintProject);
    sprint.setSprintRelease(newSprintRelease);
    sprint.setSprintStart(newSprintStart);
    sprint.setSprintEnd(newSprintEnd);
    sprint.removeAllStories();
    sprint.addAllStories(newSprintStories);

    Sprint newSprint = new Sprint(sprint);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.SPRINT_EDIT);
    undoRedoObject.setAgileItem(sprint);
    undoRedoObject.addDatum(lastSprint);
    undoRedoObject.addDatum(newSprint);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newTask() {
    taskLabel = "task";
    taskDescription = "Task description";
    taskEstimate = 90;
    taskStatus = Status.IN_PROGRESS;
    taskPeople = new ArrayList<>();
    taskPeople.add(person);
    taskEffort = new IdentityHashMap<>();
    taskEffort.put(person, 30);
    task = new Task(taskLabel, taskDescription, taskEstimate, taskStatus, taskPeople);
//    task.updateSpentEffort(taskEffort);
    story.addTask(task);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.TASK_CREATE);
    undoRedoObject.setAgileItem(task);
    undoRedoObject.addDatum(new Task(task));
    undoRedoObject.addDatum(story);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void deleteNewestTask() {
    story.removeTask(task);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.TASK_DELETE);
    undoRedoObject.setAgileItem(task);
    undoRedoObject.addDatum(new Task(task));
    undoRedoObject.addDatum(story);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void editNewestTask() {
    Task lastTask = new Task(task);

    newTaskLabel = "new task";
    newTaskDescription = "New Task description";
    newTaskEstimate = 9000;
    newTaskStatus = Status.DONE;
    newTaskPeople = new ArrayList<>();
    newTaskPeople.add(person);
    newTaskEffort = new IdentityHashMap<>();
    newTaskEffort.put(person, 1000);

    task.setLabel(newTaskLabel);
    task.setTaskDescription(newTaskDescription);
    task.setTaskEstimation(newTaskEstimate);
    task.setStatus(newTaskStatus);
    task.removeAllTaskPeople();
    task.addAllTaskPeople(newTaskPeople);
//    task.updateSpentEffort(newTaskEffort);

    Task newTask = new Task(task);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.TASK_EDIT);
    undoRedoObject.setAgileItem(task);
    undoRedoObject.addDatum(lastTask);
    undoRedoObject.addDatum(newTask);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newEffort() {
    worker = person;
    spentEffort = 180;
    comments = "This person spent time doing X";
    dateTime = LocalDateTime.of(2009, Month.APRIL, 8, 10, 12);
    effort = new Effort(worker, spentEffort, comments, dateTime);
    task.addEffort(effort);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.EFFORT_CREATE);
    undoRedoObject.setAgileItem(effort);
    undoRedoObject.addDatum(new Effort(effort));
    undoRedoObject.addDatum(task);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void deleteNewestEffort() {
    task.removeEffort(effort);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.EFFORT_DELETE);
    undoRedoObject.setAgileItem(effort);
    undoRedoObject.addDatum(new Effort(effort));
    undoRedoObject.addDatum(task);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void editNewestEffort() {
    Effort lastEffort = new Effort(effort);

    newWorker = new Person("J", "dave", "smith", FXCollections.observableArrayList());
    newSpentEffort = 9001;
    newComments = "I changed my mind and did something else instead and assigned this task to a "
                  + "blank person";
    newDateTime = LocalDateTime.of(2004, Month.FEBRUARY, 27, 23, 59);

    effort.setWorker(newWorker);
    effort.setSpentEffort(newSpentEffort);
    effort.setComments(newComments);
    effort.setDateTime(newDateTime);

    Effort newEffort = new Effort(effort);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.EFFORT_EDIT);
    undoRedoObject.setAgileItem(effort);
    undoRedoObject.addDatum(lastEffort);
    undoRedoObject.addDatum(newEffort);

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void createAndEditStoryDependencies() {
    dependantStory1 = new Story("story1", "story1", "s1desc", new Person());
    dependantStory2 = new Story("story2", "story2", "s2desc", new Person());
    dependantStory3 = new Story("story3", "story3", "s3desc", new Person());

    dependantStory2.addDependency(dependantStory1);
    dependantStory3.addDependency(dependantStory1);
    dependantStory3.addDependency(dependantStory2);

    mainApp.addStory(dependantStory1);
    mainApp.addStory(dependantStory2);
    mainApp.addStory(dependantStory3);

    // ignore undo for creation for simplicity

    Story lastDependantStory1 = new Story(dependantStory1);
    Story lastDependantStory2 = new Story(dependantStory2);
    Story lastDependantStory3 = new Story(dependantStory3);

    dependantStory1.removeAllDependencies();
    dependantStory2.removeAllDependencies();
    dependantStory3.removeAllDependencies();

    // reverse order
    dependantStory1.addDependency(dependantStory2);
    dependantStory1.addDependency(dependantStory3);
    dependantStory2.addDependency(dependantStory3);

    Story newDependantStory1 = new Story(dependantStory1);
    Story newDependantStory2 = new Story(dependantStory2);
    Story newDependantStory3 = new Story(dependantStory3);

    UndoRedoObject undoRedoObject1 = new UndoRedoObject();
    undoRedoObject1.setAction(Action.STORY_EDIT);
    undoRedoObject1.setAgileItem(dependantStory1);
    undoRedoObject1.addDatum(lastDependantStory1);
    undoRedoObject1.addDatum(newDependantStory1);

    UndoRedoObject undoRedoObject2 = new UndoRedoObject();
    undoRedoObject2.setAction(Action.STORY_EDIT);
    undoRedoObject2.setAgileItem(dependantStory2);
    undoRedoObject2.addDatum(lastDependantStory2);
    undoRedoObject2.addDatum(newDependantStory2);

    UndoRedoObject undoRedoObject3 = new UndoRedoObject();
    undoRedoObject3.setAction(Action.STORY_EDIT);
    undoRedoObject3.setAgileItem(dependantStory3);
    undoRedoObject3.addDatum(lastDependantStory3);
    undoRedoObject3.addDatum(newDependantStory3);

    CompositeUndoRedo compositeUndoRedo = new CompositeUndoRedo("Edit Story Dependencies");
    compositeUndoRedo.addUndoRedo(undoRedoObject1);
    compositeUndoRedo.addUndoRedo(undoRedoObject2);
    compositeUndoRedo.addUndoRedo(undoRedoObject3);

    undoRedoHandler.newAction(compositeUndoRedo);
  }

  @Test
  public void testPeekUndoStack() throws Exception {
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();

    UndoRedo peekObject = undoRedoHandler.peekUndoStack();
    ArrayList<AgileItem> data = peekObject.getData();
    Person peekPerson = (Person) data.get(0);

    assertEquals(Action.PERSON_CREATE, peekObject.getAction());
    assertEquals(personLabel, peekPerson.getLabel());
    assertEquals(firstName, peekPerson.getFirstName());
    assertEquals(lastName, peekPerson.getLastName());
  }

  @Test
  public void testClearStacks() throws Exception {
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    newPerson();
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    // Add a random person to the redo stack
    Person tempPerson = new Person(personLabel, firstName, lastName, skillSet);
    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.PERSON_CREATE);
    undoRedoObject.addDatum(new Person(tempPerson));
    undoRedoHandler.getRedoStack().add(undoRedoObject);

    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    // Clear stacks
    undoRedoHandler.clearStacks();
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testPersonCreateUndo() throws Exception {
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
  }

  @Test
  public void testPersonCreateRedo() throws Exception {
    Person before;
    Person after;

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    before = mainApp.getPeople().get(0);
    assertNotNull(before);

    undoRedoHandler.undo();
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    after = mainApp.getPeople().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testPersonDeleteUndo() throws Exception {
    Person before;
    Person after;

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    before = mainApp.getPeople().get(0);
    assertNotNull(before);

    deleteNewestPerson();
    assertEquals(0, mainApp.getPeople().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    after = mainApp.getPeople().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testPersonDeleteRedo() throws Exception {
    Person before;
    Person after;

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    before = mainApp.getPeople().get(0);
    assertNotNull(before);

    deleteNewestPerson();
    assertEquals(0, mainApp.getPeople().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());
    after = mainApp.getPeople().get(0);
    assertNotNull(after);

    assertSame(before, after);

    undoRedoHandler.redo();
    assertEquals(0, mainApp.getPeople().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testPersonEditUndo() throws Exception {
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newSkill();
    newPerson();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Person createdPerson = mainApp.getPeople().get(mainApp.getPeople().size() - 1);
    assertEquals(personLabel, createdPerson.getLabel());
    assertEquals(firstName, createdPerson.getFirstName());
    assertEquals(lastName, createdPerson.getLastName());
    assertTrue(createdPerson.getSkillSet().isEmpty());  // no skills yet

    editNewestPersonWithSkill();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    Person editedPerson = mainApp.getPeople().get(mainApp.getPeople().size() - 1);
    assertEquals(newPersonLabel, editedPerson.getLabel());
    assertEquals(newFirstName, editedPerson.getFirstName());
    assertEquals(newLastName, editedPerson.getLastName());
    assertEquals(newSkillSet, editedPerson.getSkillSet());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Person undonePerson = mainApp.getPeople().get(mainApp.getPeople().size() - 1);
    assertEquals(personLabel, undonePerson.getLabel());
    assertEquals(firstName, undonePerson.getFirstName());
    assertEquals(lastName, undonePerson.getLastName());
    assertTrue(undonePerson.getSkillSet().isEmpty());  // no skills now

    assertEquals(1, mainApp.getSkills().size());
  }

  @Test
  public void testPersonEditRedo() throws Exception {
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newSkill();
    newPerson();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Person createdPerson = mainApp.getPeople().get(mainApp.getPeople().size() - 1);
    assertEquals(personLabel, createdPerson.getLabel());
    assertEquals(firstName, createdPerson.getFirstName());
    assertEquals(lastName, createdPerson.getLastName());
    assertTrue(createdPerson.getSkillSet().isEmpty());  // no skills yet

    editNewestPersonWithSkill();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Person editedPerson = mainApp.getPeople().get(mainApp.getPeople().size() - 1);
    assertEquals(newPersonLabel, editedPerson.getLabel());
    assertEquals(newFirstName, editedPerson.getFirstName());
    assertEquals(newLastName, editedPerson.getLastName());
    assertEquals(newSkillSet, editedPerson.getSkillSet());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Person undonePerson = mainApp.getPeople().get(mainApp.getPeople().size() - 1);
    assertEquals(personLabel, undonePerson.getLabel());
    assertEquals(firstName, undonePerson.getFirstName());
    assertEquals(lastName, undonePerson.getLastName());
    assertTrue(undonePerson.getSkillSet().isEmpty());  // no skills now

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Person redonePerson = mainApp.getPeople().get(mainApp.getPeople().size() - 1);
    assertEquals(newPersonLabel, redonePerson.getLabel());
    assertEquals(newFirstName, redonePerson.getFirstName());
    assertEquals(newLastName, redonePerson.getLastName());
    assertEquals(newSkillSet, redonePerson.getSkillSet()); // has skills again
  }

  /**
   * Undo an edit for a skill which a person already has
   */
  @Test
  public void testPersonEditExistingSkillUndo() throws Exception {
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newSkill();
    newPersonWithSkill();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Skill oldSkill = person.getSkillSet().get(0);
    assertEquals(skillLabel, oldSkill.getLabel());
    assertEquals(skillDescription, oldSkill.getSkillDescription());

    editNewestSkill();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    Skill newSkill = person.getSkillSet().get(0);
    assertEquals(newSkillLabel, newSkill.getLabel());
    assertEquals(newSkillDescription, newSkill.getSkillDescription());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Skill undoneSkill = person.getSkillSet().get(0);
    assertEquals(skillLabel, undoneSkill.getLabel());
    assertEquals(skillDescription, undoneSkill.getSkillDescription());
  }

  /**
   * Redo an edit for a skill which a person already has
   */
  @Test
  public void testPersonEditExistingSkillRedo() throws Exception {
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newSkill();
    newPersonWithSkill();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Skill oldSkill = person.getSkillSet().get(0);
    assertEquals(skillLabel, oldSkill.getLabel());
    assertEquals(skillDescription, oldSkill.getSkillDescription());

    editNewestSkill();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Skill newSkill = person.getSkillSet().get(0);
    assertEquals(newSkillLabel, newSkill.getLabel());
    assertEquals(newSkillDescription, newSkill.getSkillDescription());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Skill undoneSkill = person.getSkillSet().get(0);
    assertEquals(skillLabel, undoneSkill.getLabel());
    assertEquals(skillDescription, undoneSkill.getSkillDescription());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(0, undoRedoHandler.getRedoStack().size());

    Skill redoneSkill = person.getSkillSet().get(0);
    assertEquals(newSkillLabel, redoneSkill.getLabel());
    assertEquals(newSkillDescription, redoneSkill.getSkillDescription());
  }

  /**
   * Redo an edit for a skill which a person already has but undo back to beginning
   */
  @Test
  public void testPersonEditExistingSkillRedoDeep() throws Exception {
    // Object references will change. Avoid person variable.
    Skill skillOfPerson;
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newSkill();
    newPersonWithSkill();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    skillOfPerson = mainApp.getPeople().get(0).getSkillSet().get(0);
    assertEquals(skillLabel, skillOfPerson.getLabel());
    assertEquals(skillDescription, skillOfPerson.getSkillDescription());

    editNewestSkill();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    skillOfPerson = mainApp.getPeople().get(0).getSkillSet().get(0);
    assertEquals(newSkillLabel, skillOfPerson.getLabel());
    assertEquals(newSkillDescription, skillOfPerson.getSkillDescription());

    undoRedoHandler.undo(); // edit skill
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    skillOfPerson = mainApp.getPeople().get(0).getSkillSet().get(0);
    assertEquals(skillLabel, skillOfPerson.getLabel());
    assertEquals(skillDescription, skillOfPerson.getSkillDescription());

    undoRedoHandler.undo(); // new person
    undoRedoHandler.undo(); // new skill
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
    undoRedoHandler.redo(); // new skill
    undoRedoHandler.redo(); // new person

    skillOfPerson = mainApp.getPeople().get(0).getSkillSet().get(0);
    assertEquals(skillLabel, skillOfPerson.getLabel());
    assertEquals(skillDescription, skillOfPerson.getSkillDescription());

    undoRedoHandler.redo(); // edit skill
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(0, undoRedoHandler.getRedoStack().size());

    skillOfPerson = mainApp.getPeople().get(0).getSkillSet().get(0);
    assertEquals(newSkillLabel, skillOfPerson.getLabel());
    assertEquals(newSkillDescription, skillOfPerson.getSkillDescription());
  }

  @Test
  public void testSkillCreateUndo() throws Exception {
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newSkill();
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
  }

  @Test
  public void testSkillCreateRedo() throws Exception {
    Skill before;
    Skill after;

    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newSkill();
    assertEquals(1, mainApp.getSkills().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    before = mainApp.getSkills().get(0);
    assertNotNull(before);

    undoRedoHandler.undo();
    assertTrue(mainApp.getSkills().isEmpty());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    after = mainApp.getSkills().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testSkillDeleteUndo() throws Exception {
    Skill before;
    Skill after;

    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newSkill();
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    before = mainApp.getSkills().get(0);
    assertNotNull(before);

    deleteNewestSkill();
    assertEquals(0, mainApp.getSkills().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    after = mainApp.getSkills().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testSkillDeleteRedo() throws Exception {
    Skill before;
    Skill after;

    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newSkill();
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    before = mainApp.getSkills().get(0);
    assertNotNull(before);

    deleteNewestSkill();
    assertEquals(0, mainApp.getSkills().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());
    after = mainApp.getSkills().get(0);
    assertNotNull(after);

    assertSame(before, after);

    undoRedoHandler.redo();
    assertEquals(0, mainApp.getSkills().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testSkillEditUndo() throws Exception {
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newSkill();
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    Skill createdSkill = mainApp.getSkills().get(mainApp.getSkills().size() - 1);
    assertEquals(skillLabel, createdSkill.getLabel());
    assertEquals(skillDescription, createdSkill.getSkillDescription());

    editNewestSkill();
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Skill editedSkill = mainApp.getSkills().get(mainApp.getSkills().size() - 1);
    assertEquals(newSkillLabel, editedSkill.getLabel());
    assertEquals(newSkillDescription, editedSkill.getSkillDescription());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    Skill undoneSkill = mainApp.getSkills().get(mainApp.getSkills().size() - 1);
    assertEquals(skillLabel, undoneSkill.getLabel());
    assertEquals(skillDescription, undoneSkill.getSkillDescription());
  }

  @Test
  public void testSkillEditRedo() throws Exception {
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newSkill();
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Skill createdSkill = mainApp.getSkills().get(mainApp.getSkills().size() - 1);
    assertEquals(skillLabel, createdSkill.getLabel());
    assertEquals(skillDescription, createdSkill.getSkillDescription());

    editNewestSkill();
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Skill editedSkill = mainApp.getSkills().get(mainApp.getSkills().size() - 1);
    assertEquals(newSkillLabel, editedSkill.getLabel());
    assertEquals(newSkillDescription, editedSkill.getSkillDescription());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Skill undoneSkill = mainApp.getSkills().get(mainApp.getSkills().size() - 1);
    assertEquals(skillLabel, undoneSkill.getLabel());
    assertEquals(skillDescription, undoneSkill.getSkillDescription());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Skill redoneSkill = mainApp.getSkills().get(mainApp.getSkills().size() - 1);
    assertEquals(newSkillLabel, redoneSkill.getLabel());
    assertEquals(newSkillDescription, redoneSkill.getSkillDescription());
  }

  @Test
  public void testStoryCreateUndo() throws Exception {
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());

    newStory();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();

    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
  }

  @Test
  public void testStoryWithACCreateUndo() throws Exception {
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());

    newStoryWithAC();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();

    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
  }

  @Test
  public void testStoryCreateRedo() throws Exception {
    Story before;
    Story after;

    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    newStory();

    assertEquals(1, mainApp.getStories().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    before = mainApp.getStories().get(0);
    assertNotNull(before);

    undoRedoHandler.undo();

    assertTrue(mainApp.getStories().isEmpty());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getStories().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    after = mainApp.getStories().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testStoryWithACCreateRedo() throws Exception {
    Story before;
    Story after;

    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    newStoryWithAC();

    assertEquals(1, mainApp.getStories().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    before = mainApp.getStories().get(0);
    assertNotNull(before);

    undoRedoHandler.undo();

    assertTrue(mainApp.getStories().isEmpty());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getStories().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    after = mainApp.getStories().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testStoryDeleteUndo() throws Exception {
    Story before;
    Story after;

    //testing deleting and undoing an empty team
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());

    newStory();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    before = mainApp.getStories().get(0);
    assertNotNull(before);

    deleteNewestStory();

    assertTrue(mainApp.getStories().isEmpty());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    after = mainApp.getStories().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testStoryWithACDeleteUndo() throws Exception {
    Story before;
    Story after;

    //testing deleting and undoing an empty team
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());

    newStoryWithAC();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    before = mainApp.getStories().get(0);
    assertNotNull(before);

    deleteNewestStory();

    assertTrue(mainApp.getStories().isEmpty());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    after = mainApp.getStories().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testStoryWithReadyDeleteUndo() throws Exception {
    Story before;
    Story after;

    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());

    newStoryMarkedAsReady();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    before = mainApp.getStories().get(0);
    assertNotNull(before);
    assertTrue(before.getStoryState());

    deleteNewestStory();

    assertTrue(mainApp.getStories().isEmpty());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    after = mainApp.getStories().get(0);
    assertNotNull(after);
    assertTrue(after.getStoryState());

    assertSame(before, after);
  }

  @Test
  public void testStoryDeleteRedo() throws Exception {
    Story before;
    Story after;

    //testing deleting and redoing an empty team
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    newStory();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    before = mainApp.getStories().get(0);
    assertNotNull(before);

    deleteNewestStory();

    assertTrue(mainApp.getStories().isEmpty());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());
    after = mainApp.getStories().get(0);
    assertNotNull(after);

    assertSame(before, after);

    undoRedoHandler.redo();
    assertTrue(mainApp.getStories().isEmpty());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
  }

  @Test
  public void testStoryWithACDeleteRedo() throws Exception {
    Story before;
    Story after;

    //testing deleting and redoing an empty team
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    newStoryWithAC();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    before = mainApp.getStories().get(0);
    assertNotNull(before);

    deleteNewestStory();

    assertTrue(mainApp.getStories().isEmpty());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());
    after = mainApp.getStories().get(0);
    assertNotNull(after);

    assertSame(before, after);

    undoRedoHandler.redo();
    assertTrue(mainApp.getStories().isEmpty());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
  }

  @Test
  public void testStoryWithReadyDeleteRedo() throws Exception {
    Story before;
    Story after;

    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    newStoryMarkedAsReady();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    before = mainApp.getStories().get(0);
    assertNotNull(before);
    assertTrue(before.getStoryState());

    deleteNewestStory();

    assertTrue(mainApp.getStories().isEmpty());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());
    after = mainApp.getStories().get(0);
    assertNotNull(after);
    assertTrue(after.getStoryState());

    assertSame(before, after);

    undoRedoHandler.redo();
    assertTrue(mainApp.getStories().isEmpty());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
  }

  @Test
  public void testStoryEditUndo() throws Exception {
    //testing editing and redoing an empty team
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newStory();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    Story createdStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(storyLabel, createdStory.getLabel());
    assertEquals(storyName, createdStory.getStoryName());
    assertEquals(storyDescription, createdStory.getDescription());
    assertEquals(storyCreator, createdStory.getCreator());

    editNewestStory();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Story editedStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(newStoryLabel, editedStory.getLabel());
    assertEquals(newStoryName, editedStory.getStoryName());
    assertEquals(newStoryDescription, editedStory.getDescription());
    assertEquals(storyCreator, editedStory.getCreator());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    Story undoneStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(storyLabel, undoneStory.getLabel());
    assertEquals(storyName, undoneStory.getStoryName());
    assertEquals(storyDescription, undoneStory.getDescription());
    assertEquals(storyCreator, undoneStory.getCreator());
  }

  @Test
  public void testStoryWithACEditUndo() throws Exception {
    //testing editing and redoing an empty team
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newStoryWithAC();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    Story createdStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(storyLabel, createdStory.getLabel());
    assertEquals(storyName, createdStory.getStoryName());
    assertEquals(storyDescription, createdStory.getDescription());
    assertEquals(storyCreator, createdStory.getCreator());
    assertEquals(storyAC, createdStory.getAcceptanceCriteria());

    editNewestStoryWithAC();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Story editedStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(newStoryLabel, editedStory.getLabel());
    assertEquals(newStoryName, editedStory.getStoryName());
    assertEquals(newStoryDescription, editedStory.getDescription());
    assertEquals(storyCreator, editedStory.getCreator());
    assertEquals(newStoryAC, editedStory.getAcceptanceCriteria());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    Story undoneStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(storyLabel, undoneStory.getLabel());
    assertEquals(storyName, undoneStory.getStoryName());
    assertEquals(storyDescription, undoneStory.getDescription());
    assertEquals(storyCreator, undoneStory.getCreator());
    assertEquals(storyAC, story.getAcceptanceCriteria());
  }

  @Test
  public void testStoryWithReadyEditUndo() throws Exception {
    //testing editing and redoing an empty team
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newStoryWithAC();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    Story createdStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(storyLabel, createdStory.getLabel());
    assertEquals(storyName, createdStory.getStoryName());
    assertEquals(storyDescription, createdStory.getDescription());
    assertEquals(storyCreator, createdStory.getCreator());
    assertEquals(storyAC, createdStory.getAcceptanceCriteria());
    assertFalse(createdStory.getStoryState());

    editNewestStoryMarkedAsReady();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Story editedStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(newStoryLabel, editedStory.getLabel());
    assertEquals(newStoryName, editedStory.getStoryName());
    assertEquals(newStoryDescription, editedStory.getDescription());
    assertEquals(storyCreator, editedStory.getCreator());
    assertEquals(newStoryAC, editedStory.getAcceptanceCriteria());
    assertTrue(editedStory.getStoryState());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    Story undoneStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(storyLabel, undoneStory.getLabel());
    assertEquals(storyName, undoneStory.getStoryName());
    assertEquals(storyDescription, undoneStory.getDescription());
    assertEquals(storyCreator, undoneStory.getCreator());
    assertEquals(storyAC, story.getAcceptanceCriteria());
    assertFalse(undoneStory.getStoryState());
  }

  @Test
  public void testStoryEditRedo() throws Exception {
    //testing editing and redoing an empty team
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newStory();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Story createdStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(storyLabel, createdStory.getLabel());
    assertEquals(storyName, createdStory.getStoryName());
    assertEquals(storyDescription, createdStory.getDescription());
    assertEquals(storyCreator, createdStory.getCreator());

    editNewestStory();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Story editedStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(newStoryLabel, editedStory.getLabel());
    assertEquals(newStoryName, editedStory.getStoryName());
    assertEquals(newStoryDescription, editedStory.getDescription());
    assertEquals(storyCreator, editedStory.getCreator());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Story undoneStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(storyLabel, undoneStory.getLabel());
    assertEquals(storyName, undoneStory.getStoryName());
    assertEquals(storyDescription, undoneStory.getDescription());
    assertEquals(storyCreator, undoneStory.getCreator());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getStories().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Story redoneStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(newStoryLabel, redoneStory.getLabel());
    assertEquals(newStoryName, redoneStory.getStoryName());
    assertEquals(newStoryDescription, redoneStory.getDescription());
    assertEquals(storyCreator, redoneStory.getCreator());
  }

  @Test
  public void testStoryWithACEditRedo() throws Exception {
    //testing editing and redoing an empty team
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newStoryWithAC();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Story createdStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(storyLabel, createdStory.getLabel());
    assertEquals(storyName, createdStory.getStoryName());
    assertEquals(storyDescription, createdStory.getDescription());
    assertEquals(storyCreator, createdStory.getCreator());
    assertEquals(storyAC, createdStory.getAcceptanceCriteria());

    editNewestStoryWithAC();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Story editedStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(newStoryLabel, editedStory.getLabel());
    assertEquals(newStoryName, editedStory.getStoryName());
    assertEquals(newStoryDescription, editedStory.getDescription());
    assertEquals(storyCreator, editedStory.getCreator());
    assertEquals(newStoryAC, editedStory.getAcceptanceCriteria());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Story undoneStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(storyLabel, undoneStory.getLabel());
    assertEquals(storyName, undoneStory.getStoryName());
    assertEquals(storyDescription, undoneStory.getDescription());
    assertEquals(storyCreator, undoneStory.getCreator());
    assertEquals(storyAC, undoneStory.getAcceptanceCriteria());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getStories().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Story redoneStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(newStoryLabel, redoneStory.getLabel());
    assertEquals(newStoryName, redoneStory.getStoryName());
    assertEquals(newStoryDescription, redoneStory.getDescription());
    assertEquals(storyCreator, redoneStory.getCreator());
    assertEquals(newStoryAC,redoneStory.getAcceptanceCriteria());
  }

  @Test
  public void testStoryWithReadyEditRedo() throws Exception {
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newStoryWithAC();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Story createdStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(storyLabel, createdStory.getLabel());
    assertEquals(storyName, createdStory.getStoryName());
    assertEquals(storyDescription, createdStory.getDescription());
    assertEquals(storyCreator, createdStory.getCreator());
    assertEquals(storyAC, createdStory.getAcceptanceCriteria());
    assertFalse(createdStory.getStoryState());

    editNewestStoryMarkedAsReady();

    assertEquals(1, mainApp.getStories().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Story editedStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(newStoryLabel, editedStory.getLabel());
    assertEquals(newStoryName, editedStory.getStoryName());
    assertEquals(newStoryDescription, editedStory.getDescription());
    assertEquals(storyCreator, editedStory.getCreator());
    assertEquals(newStoryAC, editedStory.getAcceptanceCriteria());
    assertTrue(editedStory.getStoryState());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Story undoneStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(storyLabel, undoneStory.getLabel());
    assertEquals(storyName, undoneStory.getStoryName());
    assertEquals(storyDescription, undoneStory.getDescription());
    assertEquals(storyCreator, undoneStory.getCreator());
    assertEquals(storyAC, undoneStory.getAcceptanceCriteria());
    assertFalse(undoneStory.getStoryState());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getStories().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Story redoneStory = mainApp.getStories().get(mainApp.getStories().size() - 1);
    assertEquals(newStoryLabel, redoneStory.getLabel());
    assertEquals(newStoryName, redoneStory.getStoryName());
    assertEquals(newStoryDescription, redoneStory.getDescription());
    assertEquals(storyCreator, redoneStory.getCreator());
    assertEquals(newStoryAC,redoneStory.getAcceptanceCriteria());
    assertTrue(redoneStory.getStoryState());
  }

  @Test
  public void testTeamCreateUndo() throws Exception {
    //testing creating and undoing an empty team
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());

    newTeam();

    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();

    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
  }

  @Test
  public void testTeamCreateRedo() throws Exception {
    Team before;
    Team after;

    //testing creating and redoing an empty team
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    newTeam();

    assertEquals(1, mainApp.getTeams().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    before = mainApp.getTeams().get(0);
    assertNotNull(before);

    undoRedoHandler.undo();

    assertTrue(mainApp.getTeams().isEmpty());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getTeams().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    after = mainApp.getTeams().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testTeamDeleteUndo() throws Exception {
    Team before;
    Team after;

    //testing deleting and undoing an empty team
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());

    newTeam();

    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    before = mainApp.getTeams().get(0);
    assertNotNull(before);

    deleteNewestTeam();

    assertTrue(mainApp.getTeams().isEmpty());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    after = mainApp.getTeams().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testTeamDeleteRedo() throws Exception {
    Team before;
    Team after;

    //testing deleting and redoing an empty team
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    newTeam();

    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    before = mainApp.getTeams().get(0);
    assertNotNull(before);

    deleteNewestTeam();

    assertTrue(mainApp.getTeams().isEmpty());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());
    after = mainApp.getTeams().get(0);
    assertNotNull(after);

    assertSame(before, after);

    undoRedoHandler.redo();
    assertTrue(mainApp.getTeams().isEmpty());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
  }

  @Test
  public void testTeamEditUndo() throws Exception {
    //testing editing and redoing an empty team
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newTeam();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    Team createdTeam = mainApp.getTeams().get(mainApp.getTeams().size() - 1);
    assertEquals(teamLabel, createdTeam.getLabel());
    assertEquals(teamMembers, createdTeam.getTeamMembers());
    assertEquals(teamDescription, createdTeam.getTeamDescription());

    editNewestTeam();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Team editedTeam = mainApp.getTeams().get(mainApp.getTeams().size() - 1);
    assertEquals(newTeamLabel, editedTeam.getLabel());
    assertEquals(newTeamMembers, editedTeam.getTeamMembers());
    assertEquals(newTeamDescription, editedTeam.getTeamDescription());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    Team undoneTeam = mainApp.getTeams().get(mainApp.getTeams().size() - 1);
    assertEquals(teamLabel, undoneTeam.getLabel());
    assertEquals(teamMembers, undoneTeam.getTeamMembers());
    assertEquals(teamDescription, undoneTeam.getTeamDescription());
  }

  @Test
  public void testTeamEditRedo() throws Exception {
    //testing editing and redoing an empty team
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newTeam();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Team createdTeam = mainApp.getTeams().get(mainApp.getTeams().size() - 1);
    assertEquals(teamLabel, createdTeam.getLabel());
    assertEquals(teamMembers, createdTeam.getTeamMembers());
    assertEquals(teamDescription, createdTeam.getTeamDescription());

    editNewestTeam();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Team editedTeam = mainApp.getTeams().get(mainApp.getTeams().size() - 1);
    assertEquals(newTeamLabel, editedTeam.getLabel());
    assertEquals(newTeamMembers, editedTeam.getTeamMembers());
    assertEquals(newTeamDescription, editedTeam.getTeamDescription());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Team undoneTeam = mainApp.getTeams().get(mainApp.getTeams().size() - 1);
    assertEquals(teamLabel, undoneTeam.getLabel());
    assertEquals(teamMembers, undoneTeam.getTeamMembers());
    assertEquals(teamDescription, undoneTeam.getTeamDescription());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Team redoneTeam = mainApp.getTeams().get(mainApp.getTeams().size() - 1);
    assertEquals(newTeamLabel, redoneTeam.getLabel());
    assertEquals(newTeamMembers, redoneTeam.getTeamMembers());
    assertEquals(newTeamDescription, redoneTeam.getTeamDescription());
  }

  /**
   * Undo an edit for a person which is part of a team
   */
  @Test
  public void testTeamEditExistingMemberUndo() throws Exception {
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    newTeamWithMember();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Person oldPerson = team.getTeamMembers().get(0);
    assertEquals(personLabel, oldPerson.getLabel());
    assertEquals(firstName, oldPerson.getFirstName());
    assertEquals(lastName, oldPerson.getLastName());

    editNewestPerson();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    Person newPerson = team.getTeamMembers().get(0);
    assertEquals(newPersonLabel, newPerson.getLabel());
    assertEquals(newFirstName, newPerson.getFirstName());
    assertEquals(newLastName, newPerson.getLastName());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Person undonePerson = team.getTeamMembers().get(0);
    assertEquals(personLabel, undonePerson.getLabel());
    assertEquals(firstName, undonePerson.getFirstName());
    assertEquals(lastName, undonePerson.getLastName());
  }

  /**
   * Redo an edit for a person which is part of a team
   */
  @Test
  public void testTeamEditExistingMemberRedo() throws Exception {
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    newTeamWithMember();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Person oldPerson = team.getTeamMembers().get(0);
    assertEquals(personLabel, oldPerson.getLabel());
    assertEquals(firstName, oldPerson.getFirstName());
    assertEquals(lastName, oldPerson.getLastName());

    editNewestPerson();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    Person newPerson = team.getTeamMembers().get(0);
    assertEquals(newPersonLabel, newPerson.getLabel());
    assertEquals(newFirstName, newPerson.getFirstName());
    assertEquals(newLastName, newPerson.getLastName());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Person undonePerson = team.getTeamMembers().get(0);
    assertEquals(personLabel, undonePerson.getLabel());
    assertEquals(firstName, undonePerson.getFirstName());
    assertEquals(lastName, undonePerson.getLastName());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(0, undoRedoHandler.getRedoStack().size());

    Person redonePerson = team.getTeamMembers().get(0);
    assertEquals(newPersonLabel, redonePerson.getLabel());
    assertEquals(newFirstName, redonePerson.getFirstName());
    assertEquals(newLastName, redonePerson.getLastName());
  }

  /**
   * Redo an edit for a person which is part of a team  but undo back to beginning
   */
  @Test
  public void testTeamEditExistingMemberRedoDeep() throws Exception {
    Person teamMember;

    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    newTeamWithMember();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    teamMember = mainApp.getTeams().get(0).getTeamMembers().get(0);
    assertEquals(personLabel, teamMember.getLabel());
    assertEquals(firstName, teamMember.getFirstName());
    assertEquals(lastName, teamMember.getLastName());

    editNewestPerson();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    teamMember = mainApp.getTeams().get(0).getTeamMembers().get(0);
    assertEquals(newPersonLabel, teamMember.getLabel());
    assertEquals(newFirstName, teamMember.getFirstName());
    assertEquals(newLastName, teamMember.getLastName());

    undoRedoHandler.undo(); // edit person
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    teamMember = mainApp.getTeams().get(0).getTeamMembers().get(0);
    assertEquals(personLabel, teamMember.getLabel());
    assertEquals(firstName, teamMember.getFirstName());
    assertEquals(lastName, teamMember.getLastName());

    undoRedoHandler.undo(); // new team
    undoRedoHandler.undo(); // new person
    assertTrue(undoRedoHandler.getUndoStack().empty());
    undoRedoHandler.redo(); // new person
    undoRedoHandler.redo(); // new team

    teamMember = mainApp.getTeams().get(0).getTeamMembers().get(0);
    assertEquals(personLabel, teamMember.getLabel());
    assertEquals(firstName, teamMember.getFirstName());
    assertEquals(lastName, teamMember.getLastName());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(0, undoRedoHandler.getRedoStack().size());

    teamMember = mainApp.getTeams().get(0).getTeamMembers().get(0);
    assertEquals(newPersonLabel, teamMember.getLabel());
    assertEquals(newFirstName, teamMember.getFirstName());
    assertEquals(newLastName, teamMember.getLastName());
  }

  @Test
  public void testProjectCreateUndo() throws Exception {
    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newProject();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
  }

  @Test
  public void testProjectCreateRedo() throws Exception {
    Project before;
    Project after;

    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newProject();
    assertEquals(1, mainApp.getProjects().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    before = mainApp.getProjects().get(0);
    assertNotNull(before);

    undoRedoHandler.undo();
    assertTrue(mainApp.getProjects().isEmpty());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    after = mainApp.getProjects().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testProjectDeleteUndo() throws Exception {
    Project before;
    Project after;

    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newProject();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    before = mainApp.getProjects().get(0);
    assertNotNull(before);

    deleteNewestProject();
    assertEquals(0, mainApp.getProjects().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    after = mainApp.getProjects().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testProjectDeleteRedo() throws Exception {
    Project before;
    Project after;

    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newProject();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    before = mainApp.getProjects().get(0);
    assertNotNull(before);

    deleteNewestProject();
    assertEquals(0, mainApp.getProjects().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());
    after = mainApp.getProjects().get(0);
    assertNotNull(after);

    assertSame(before, after);

    undoRedoHandler.redo();
    assertEquals(0, mainApp.getProjects().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testProjectEditUndo() throws Exception {
    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newProject();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    Project createdProject = mainApp.getProjects().get(mainApp.getProjects().size() - 1);
    assertEquals(projectLabel, createdProject.getLabel());
    assertEquals(projectName, createdProject.getProjectName());
    assertEquals(projectDescription, createdProject.getProjectDescription());

    editNewestProject();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Project editedProject = mainApp.getProjects().get(mainApp.getProjects().size() - 1);
    assertEquals(newProjectLabel, createdProject.getLabel());
    assertEquals(newProjectName, editedProject.getProjectName());
    assertEquals(newProjectDescription, editedProject.getProjectDescription());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    Project undoneProject = mainApp.getProjects().get(mainApp.getProjects().size() - 1);
    assertEquals(projectLabel, createdProject.getLabel());
    assertEquals(projectName, undoneProject.getProjectName());
    assertEquals(projectDescription, undoneProject.getProjectDescription());
  }

  @Test
  public void testProjectEditRedo() throws Exception {
    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newProject();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Project createdProject = mainApp.getProjects().get(mainApp.getProjects().size() - 1);
    assertEquals(projectLabel, createdProject.getLabel());
    assertEquals(projectName, createdProject.getProjectName());
    assertEquals(projectDescription, createdProject.getProjectDescription());

    editNewestProject();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Project editedProject = mainApp.getProjects().get(mainApp.getProjects().size() - 1);
    assertEquals(newProjectName, editedProject.getProjectName());
    assertEquals(newProjectDescription, editedProject.getProjectDescription());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Project undoneProject = mainApp.getProjects().get(mainApp.getProjects().size() - 1);
    assertEquals(projectLabel, createdProject.getLabel());
    assertEquals(projectName, undoneProject.getProjectName());
    assertEquals(projectDescription, undoneProject.getProjectDescription());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Project redoneProject = mainApp.getProjects().get(mainApp.getProjects().size() - 1);
    assertEquals(newProjectLabel, createdProject.getLabel());
    assertEquals(newProjectName, redoneProject.getProjectName());
    assertEquals(newProjectDescription, redoneProject.getProjectDescription());
  }

  @Test
  public void testReleaseCreateUndo() throws Exception {
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newRelease();
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
  }

  @Test
  public void testReleaseCreateRedo() throws Exception {
    Release before;
    Release after;

    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newRelease();
    assertEquals(1, mainApp.getReleases().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    before = mainApp.getReleases().get(0);
    assertNotNull(before);

    undoRedoHandler.undo();
    assertTrue(mainApp.getReleases().isEmpty());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    after = mainApp.getReleases().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testReleaseDeleteUndo() throws Exception {
    Release before;
    Release after;

    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newRelease();
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    before = mainApp.getReleases().get(0);
    assertNotNull(before);

    deleteNewestRelease();
    assertEquals(0, mainApp.getReleases().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    after = mainApp.getReleases().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testReleaseDeleteRedo() throws Exception {
    Release before;
    Release after;

    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newRelease();
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    before = mainApp.getReleases().get(0);
    assertNotNull(before);

    deleteNewestRelease();
    assertEquals(0, mainApp.getReleases().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());
    after = mainApp.getReleases().get(0);
    assertNotNull(after);

    assertSame(before, after);

    undoRedoHandler.redo();
    assertEquals(0, mainApp.getReleases().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testReleaseEditUndo() throws Exception {
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newRelease();
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    Release createdRelease = mainApp.getReleases().get(mainApp.getReleases().size() - 1);
    assertEquals(releaseLabel, createdRelease.getLabel());
    assertEquals(releaseDescription, createdRelease.getReleaseDescription());
    assertEquals(releaseDate, createdRelease.getReleaseDate());
    assertEquals(releaseNotes, createdRelease.getReleaseNotes());
    assertEquals(projectRelease, createdRelease.getProjectRelease());

    editNewestRelease();
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Release editedRelease = mainApp.getReleases().get(mainApp.getReleases().size() - 1);
    assertEquals(newReleaseLabel, editedRelease.getLabel());
    assertEquals(newReleaseDescription, editedRelease.getReleaseDescription());
    assertEquals(newReleaseDate, editedRelease.getReleaseDate());
    assertEquals(newReleaseNotes, editedRelease.getReleaseNotes());
    assertEquals(newProjectRelease, editedRelease.getProjectRelease());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    Release undoneRelease = mainApp.getReleases().get(mainApp.getReleases().size() - 1);
    assertEquals(releaseLabel, undoneRelease.getLabel());
    assertEquals(releaseDescription, undoneRelease.getReleaseDescription());
    assertEquals(releaseDate, undoneRelease.getReleaseDate());
    assertEquals(releaseNotes, undoneRelease.getReleaseNotes());
    assertEquals(projectRelease, undoneRelease.getProjectRelease());
  }

  @Test
  public void testReleaseEditRedo() throws Exception {
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newRelease();
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Release createdRelease = mainApp.getReleases().get(mainApp.getReleases().size() - 1);
    assertEquals(releaseLabel, createdRelease.getLabel());
    assertEquals(releaseDescription, createdRelease.getReleaseDescription());
    assertEquals(releaseDate, createdRelease.getReleaseDate());
    assertEquals(releaseNotes, createdRelease.getReleaseNotes());
    assertEquals(projectRelease, createdRelease.getProjectRelease());

    editNewestRelease();
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Release editedRelease = mainApp.getReleases().get(mainApp.getReleases().size() - 1);
    assertEquals(newReleaseLabel, editedRelease.getLabel());
    assertEquals(newReleaseDescription, editedRelease.getReleaseDescription());
    assertEquals(newReleaseDate, editedRelease.getReleaseDate());
    assertEquals(newReleaseNotes, editedRelease.getReleaseNotes());
    assertEquals(newProjectRelease, editedRelease.getProjectRelease());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Release undoneRelease = mainApp.getReleases().get(mainApp.getReleases().size() - 1);
    assertEquals(releaseLabel, undoneRelease.getLabel());
    assertEquals(releaseDescription, undoneRelease.getReleaseDescription());
    assertEquals(releaseDate, undoneRelease.getReleaseDate());
    assertEquals(releaseNotes, undoneRelease.getReleaseNotes());
    assertEquals(projectRelease, undoneRelease.getProjectRelease());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Release redoneRelease = mainApp.getReleases().get(mainApp.getReleases().size() - 1);
    assertEquals(newReleaseLabel, redoneRelease.getLabel());
    assertEquals(newReleaseDescription, redoneRelease.getReleaseDescription());
    assertEquals(newReleaseDate, redoneRelease.getReleaseDate());
    assertEquals(newReleaseNotes, redoneRelease.getReleaseNotes());
    assertEquals(newProjectRelease, redoneRelease.getProjectRelease());
  }

  /**
   * Undo an edit for a Release which is part of a Project
   */
  @Test
  public void testReleaseEditExistingProjectUndo() throws Exception {
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newProject();
    newRelease();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Release oldRelease = mainApp.getReleases().get(0);
    assertEquals(releaseLabel, oldRelease.getLabel());
    assertEquals(releaseDescription, oldRelease.getReleaseDescription());
    assertEquals(releaseDate, oldRelease.getReleaseDate());
    assertEquals(releaseNotes, oldRelease.getReleaseNotes());
    assertEquals(projectRelease, oldRelease.getProjectRelease());

    editNewestRelease();
    Release newRelease = mainApp.getReleases().get(0);
    assertEquals(newReleaseLabel, newRelease.getLabel());
    assertEquals(newReleaseDescription, newRelease.getReleaseDescription());
    assertEquals(newReleaseDate, newRelease.getReleaseDate());
    assertEquals(newReleaseNotes, newRelease.getReleaseNotes());
    assertEquals(newProjectRelease, newRelease.getProjectRelease());
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Release undoneRelease = mainApp.getReleases().get(0);
    assertEquals(releaseLabel, undoneRelease.getLabel());
    assertEquals(releaseDescription, undoneRelease.getReleaseDescription());
    assertEquals(releaseDate, undoneRelease.getReleaseDate());
    assertEquals(releaseNotes, undoneRelease.getReleaseNotes());
    assertEquals(projectRelease, undoneRelease.getProjectRelease());

  }

  /**
   * Redo an edit for a release which is part of a project
   */
  @Test
  public void testReleaseEditExistingProjectRedo() throws Exception {
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newProject();
    newRelease();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Release oldRelease = mainApp.getReleases().get(0);
    assertEquals(releaseLabel, oldRelease.getLabel());
    assertEquals(releaseDescription, oldRelease.getReleaseDescription());
    assertEquals(releaseDate, oldRelease.getReleaseDate());
    assertEquals(releaseNotes, oldRelease.getReleaseNotes());
    assertEquals(projectRelease, oldRelease.getProjectRelease());

    editNewestRelease();
    Release newRelease = mainApp.getReleases().get(0);
    assertEquals(newReleaseLabel, newRelease.getLabel());
    assertEquals(newReleaseDescription, newRelease.getReleaseDescription());
    assertEquals(newReleaseDate, newRelease.getReleaseDate());
    assertEquals(newReleaseNotes, newRelease.getReleaseNotes());
    assertEquals(newProjectRelease, newRelease.getProjectRelease());

    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Release undoneRelease = mainApp.getReleases().get(0);
    assertEquals(releaseLabel, undoneRelease.getLabel());
    assertEquals(releaseDescription, undoneRelease.getReleaseDescription());
    assertEquals(releaseDate, undoneRelease.getReleaseDate());
    assertEquals(releaseNotes, undoneRelease.getReleaseNotes());
    assertEquals(projectRelease, undoneRelease.getProjectRelease());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Release redoneRelease = mainApp.getReleases().get(0);
    assertEquals(newReleaseLabel, redoneRelease.getLabel());
    assertEquals(newReleaseDescription, redoneRelease.getReleaseDescription());
    assertEquals(newReleaseDate, redoneRelease.getReleaseDate());
    assertEquals(newReleaseNotes, redoneRelease.getReleaseNotes());
    assertEquals(newProjectRelease, redoneRelease.getProjectRelease());
  }

  /**
   * Redo an edit for a release which is part of a project  but undo back to beginning
   */
  @Test
  public void testReleaseEditExistingProjectRedoDeep() throws Exception {
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newProject();
    newRelease();
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Release oldRelease = mainApp.getReleases().get(0);
    assertEquals(releaseLabel, oldRelease.getLabel());
    assertEquals(releaseDescription, oldRelease.getReleaseDescription());
    assertEquals(releaseDate, oldRelease.getReleaseDate());
    assertEquals(releaseNotes, oldRelease.getReleaseNotes());
    assertEquals(projectRelease, oldRelease.getProjectRelease());

    editNewestRelease();
    Release newRelease = mainApp.getReleases().get(0);
    assertEquals(newReleaseLabel, newRelease.getLabel());
    assertEquals(newReleaseDescription, newRelease.getReleaseDescription());
    assertEquals(newReleaseDate, newRelease.getReleaseDate());
    assertEquals(newReleaseNotes, newRelease.getReleaseNotes());
    assertEquals(newProjectRelease, newRelease.getProjectRelease());
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo(); //edit release
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    Release undoneRelease = mainApp.getReleases().get(0);
    assertEquals(releaseLabel, undoneRelease.getLabel());
    assertEquals(releaseDescription, undoneRelease.getReleaseDescription());
    assertEquals(releaseDate, undoneRelease.getReleaseDate());
    assertEquals(releaseNotes, undoneRelease.getReleaseNotes());
    assertEquals(projectRelease, undoneRelease.getProjectRelease());

    undoRedoHandler.undo(); // new release
    undoRedoHandler.undo(); // new project
    assertTrue(undoRedoHandler.getUndoStack().empty());
    undoRedoHandler.redo(); // new project
    undoRedoHandler.redo(); // new release

    oldRelease = mainApp.getReleases().get(0); //before the edit redo is done
    assertEquals(releaseLabel, oldRelease.getLabel());
    assertEquals(releaseDescription, oldRelease.getReleaseDescription());
    assertEquals(releaseDate, oldRelease.getReleaseDate());
    assertEquals(releaseNotes, oldRelease.getReleaseNotes());
    assertEquals(projectRelease, oldRelease.getProjectRelease());

    undoRedoHandler.redo(); //redoes the edit
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    newRelease = mainApp.getReleases().get(0);
    assertEquals(newReleaseLabel, newRelease.getLabel());
    assertEquals(newReleaseDescription, newRelease.getReleaseDescription());
    assertEquals(newReleaseDate, newRelease.getReleaseDate());
    assertEquals(newReleaseNotes, newRelease.getReleaseNotes());
    assertEquals(newProjectRelease, newRelease.getProjectRelease());
  }

  @Test
  public void testBacklogCreateUndo() throws Exception {
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());

    newPerson();
    newStory();
    newEstimate();
    newBacklog();

    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();

    assertTrue(mainApp.getBacklogs().isEmpty());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
  }

  @Test
  public void testBacklogCreateRedo() throws Exception {
    Backlog before;
    Backlog after;

    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    newPerson();
    newStory();
    newEstimate();
    newBacklog();

    assertEquals(1, mainApp.getBacklogs().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    before = mainApp.getBacklogs().get(0);
    assertNotNull(before);

    undoRedoHandler.undo();

    assertTrue(mainApp.getBacklogs().isEmpty());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getBacklogs().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    after = mainApp.getBacklogs().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testBacklogDeleteUndo() throws Exception {
    Backlog before;
    Backlog after;

    //testing deleting and undoing an empty backlog
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());

    newPerson();
    newStory();
    newEstimate();
    newBacklog();

    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    before = mainApp.getBacklogs().get(0);
    assertNotNull(before);

    deleteNewestBacklog();

    assertTrue(mainApp.getBacklogs().isEmpty());
    assertEquals(4, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    after = mainApp.getBacklogs().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testBacklogDeleteRedo() throws Exception {
    Backlog before;
    Backlog after;

    //testing deleting and redoing an empty backlog
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    newPerson();
    newStory();
    newEstimate();
    newBacklog();

    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    before = mainApp.getBacklogs().get(0);
    assertNotNull(before);

    deleteNewestBacklog();

    assertTrue(mainApp.getBacklogs().isEmpty());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());
    after = mainApp.getBacklogs().get(0);
    assertNotNull(after);

    assertSame(before, after);

    undoRedoHandler.redo();
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
  }

  @Test
  public void testBacklogEditUndo() throws Exception {
    //testing editing and redoing an empty backlog
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    newStory();
    newEstimate();
    newBacklog();

    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    Backlog createdBacklog = mainApp.getBacklogs().get(mainApp.getBacklogs().size() - 1);
    assertEquals(backlogLabel, createdBacklog.getLabel());
    assertEquals(backlogName, createdBacklog.getBacklogName());
    assertEquals(backlogDescription, createdBacklog.getBacklogDescription());
    assertEquals(productOwner, createdBacklog.getProductOwner());
    assertEquals(backlogEstimate, createdBacklog.getEstimate());

    editNewestBacklog();

    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());

    Backlog editedBacklog = mainApp.getBacklogs().get(mainApp.getBacklogs().size() - 1);
    assertEquals(newBacklogLabel, editedBacklog.getLabel());
    assertEquals(newBacklogName, editedBacklog.getBacklogName());
    assertEquals(newBacklogDescription, editedBacklog.getBacklogDescription());
    assertEquals(newProductOwner, editedBacklog.getProductOwner());
    assertEquals(newBacklogEstimate, editedBacklog.getEstimate());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    Backlog undoneBacklog = mainApp.getBacklogs().get(mainApp.getBacklogs().size() - 1);
    assertEquals(backlogLabel, undoneBacklog.getLabel());
    assertEquals(backlogName, undoneBacklog.getBacklogName());
    assertEquals(backlogDescription, undoneBacklog.getBacklogDescription());
    assertEquals(productOwner, undoneBacklog.getProductOwner());
    assertEquals(backlogEstimate, undoneBacklog.getEstimate());
  }

  @Test
  public void testBacklogEditRedo() throws Exception {
    //testing editing and redoing an empty backlog
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    newStory();
    newEstimate();
    newBacklog();

    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Backlog createdBacklog = mainApp.getBacklogs().get(mainApp.getBacklogs().size() - 1);
    assertEquals(backlogLabel, createdBacklog.getLabel());
    assertEquals(backlogName, createdBacklog.getBacklogName());
    assertEquals(backlogDescription, createdBacklog.getBacklogDescription());
    assertEquals(productOwner, createdBacklog.getProductOwner());
    assertEquals(backlogEstimate, createdBacklog.getEstimate());

    editNewestBacklog();

    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Backlog editedBacklog = mainApp.getBacklogs().get(mainApp.getBacklogs().size() - 1);
    assertEquals(newBacklogLabel, editedBacklog.getLabel());
    assertEquals(newBacklogName, editedBacklog.getBacklogName());
    assertEquals(newBacklogDescription, editedBacklog.getBacklogDescription());
    assertEquals(newProductOwner, editedBacklog.getProductOwner());
    assertEquals(newBacklogEstimate, editedBacklog.getEstimate());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Backlog undoneBacklog = mainApp.getBacklogs().get(mainApp.getBacklogs().size() - 1);
    assertEquals(backlogLabel, undoneBacklog.getLabel());
    assertEquals(backlogName, undoneBacklog.getBacklogName());
    assertEquals(backlogDescription, undoneBacklog.getBacklogDescription());
    assertEquals(productOwner, undoneBacklog.getProductOwner());
    assertEquals(backlogEstimate, undoneBacklog.getEstimate());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Backlog redoneBacklog = mainApp.getBacklogs().get(mainApp.getBacklogs().size() - 1);
    assertEquals(newBacklogLabel, redoneBacklog.getLabel());
    assertEquals(newBacklogName, redoneBacklog.getBacklogName());
    assertEquals(newBacklogDescription, redoneBacklog.getBacklogDescription());
    assertEquals(newProductOwner, redoneBacklog.getProductOwner());
    assertEquals(newBacklogEstimate, redoneBacklog.getEstimate());
  }

  @Test
  public void testSprintCreateUndo() throws Exception {
    assertTrue(mainApp.getSprints().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());

    newPerson();
    newTeam();
    newProject();
    newRelease();
    newStory();
    newBacklog();
    newSprint();

    assertEquals(1, mainApp.getSprints().size());
    assertEquals(7, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();

    assertTrue(mainApp.getSprints().isEmpty());
    assertEquals(6, undoRedoHandler.getUndoStack().size());
  }

  @Test
  public void testSprintCreateRedo() throws Exception {
    Sprint before;
    Sprint after;

    assertTrue(mainApp.getSprints().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    newPerson();
    newTeam();
    newProject();
    newRelease();
    newStory();
    newBacklog();
    newSprint();

    assertEquals(1, mainApp.getSprints().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    before = mainApp.getSprints().get(0);
    assertNotNull(before);

    undoRedoHandler.undo();

    assertTrue(mainApp.getSprints().isEmpty());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getSprints().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    after = mainApp.getSprints().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testSprintDeleteUndo() throws Exception {
    Sprint before;
    Sprint after;

    //testing deleting and undoing a sprint
    assertTrue(mainApp.getSprints().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());

    newPerson();
    newTeam();
    newProject();
    newRelease();
    newStory();
    newBacklog();
    newSprint();

    assertEquals(1, mainApp.getSprints().size());
    assertEquals(7, undoRedoHandler.getUndoStack().size());
    before = mainApp.getSprints().get(0);
    assertNotNull(before);

    deleteNewestSprint();

    assertTrue(mainApp.getSprints().isEmpty());
    assertEquals(8, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getSprints().size());
    assertEquals(7, undoRedoHandler.getUndoStack().size());
    after = mainApp.getSprints().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testSprintDeleteRedo() throws Exception {
    Sprint before;
    Sprint after;

    //testing deleting and redoing a sprint
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    newPerson();
    newTeam();
    newProject();
    newRelease();
    newStory();
    newBacklog();
    newSprint();

    assertEquals(1, mainApp.getSprints().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    before = mainApp.getSprints().get(0);
    assertNotNull(before);

    deleteNewestSprint();

    assertTrue(mainApp.getSprints().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getSprints().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());
    after = mainApp.getSprints().get(0);
    assertNotNull(after);

    assertSame(before, after);

    undoRedoHandler.redo();
    assertTrue(mainApp.getSprints().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
  }

  @Test
  public void testSprintEditUndo() throws Exception {
    //testing editing and redoing a sprint
    assertTrue(mainApp.getSprints().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    newTeam();
    newProject();
    newRelease();
    newStory();
    newBacklog();
    newSprint();

    assertEquals(1, mainApp.getSprints().size());
    assertEquals(7, undoRedoHandler.getUndoStack().size());

    Sprint createdSprint = mainApp.getSprints().get(0);

    assertEquals(sprintGoal, createdSprint.getSprintGoal());
    assertEquals(sprintFullName, createdSprint.getSprintFullName());
    assertEquals(sprintDescription, createdSprint.getSprintDescription());
    assertEquals(sprintImpediments, createdSprint.getSprintImpediments());
    assertEquals(sprintBacklog, createdSprint.getSprintBacklog());
    assertEquals(sprintStart, createdSprint.getSprintStart());
    assertEquals(sprintEnd, createdSprint.getSprintEnd());
    assertEquals(sprintProject, createdSprint.getSprintProject());
    assertEquals(sprintRelease, createdSprint.getSprintRelease());
    assertEquals(sprintStories, createdSprint.getSprintStories());
    assertEquals(sprintTeam, createdSprint.getSprintTeam());

    editNewestSprint();

    assertEquals(1, mainApp.getSprints().size());
    assertEquals(8, undoRedoHandler.getUndoStack().size());

    Sprint editedSprint = mainApp.getSprints().get(0);
    assertEquals(newSprintGoal, editedSprint.getSprintGoal());
    assertEquals(newSprintFullName, editedSprint.getSprintFullName());
    assertEquals(newSprintDescription, editedSprint.getSprintDescription());
    assertEquals(newSprintImpediments, editedSprint.getSprintImpediments());
    assertEquals(newSprintBacklog, editedSprint.getSprintBacklog());
    assertEquals(newSprintStart, editedSprint.getSprintStart());
    assertEquals(newSprintEnd, editedSprint.getSprintEnd());
    assertEquals(newSprintProject, editedSprint.getSprintProject());
    assertEquals(newSprintRelease, editedSprint.getSprintRelease());
    assertEquals(newSprintStories, editedSprint.getSprintStories());
    assertEquals(newSprintTeam, editedSprint.getSprintTeam());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getSprints().size());
    assertEquals(7, undoRedoHandler.getUndoStack().size());

    Sprint undoneSprint = mainApp.getSprints().get(0);
    assertEquals(sprintGoal, undoneSprint.getSprintGoal());
    assertEquals(sprintFullName, undoneSprint.getSprintFullName());
    assertEquals(sprintDescription, undoneSprint.getSprintDescription());
    assertEquals(sprintImpediments, undoneSprint.getSprintImpediments());
    assertEquals(sprintBacklog, undoneSprint.getSprintBacklog());
    assertEquals(sprintStart, undoneSprint.getSprintStart());
    assertEquals(sprintEnd, undoneSprint.getSprintEnd());
    assertEquals(sprintProject, undoneSprint.getSprintProject());
    assertEquals(sprintRelease, undoneSprint.getSprintRelease());
    assertEquals(sprintStories, undoneSprint.getSprintStories());
    assertEquals(sprintTeam, undoneSprint.getSprintTeam());
  }

  @Test
  public void testTaskCreateUndo() throws Exception {
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    newStory();
    newTask();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getStories().get(0).getTasks().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertTrue(mainApp.getStories().get(0).getTasks().isEmpty());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
  }

  @Test
  public void testTaskCreateRedo() throws Exception {
    Task before;
    Task after;

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    newStory();
    newTask();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getStories().get(0).getTasks().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    before = mainApp.getStories().get(0).getTasks().get(0);
    assertNotNull(before);

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertTrue(mainApp.getStories().get(0).getTasks().isEmpty());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getStories().get(0).getTasks().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    after = mainApp.getStories().get(0).getTasks().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testTaskDeleteUndo() throws Exception {
    Task before;
    Task after;

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    newStory();
    newTask();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getStories().get(0).getTasks().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    before = mainApp.getStories().get(0).getTasks().get(0);
    assertNotNull(before);

    deleteNewestTask();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertTrue(mainApp.getStories().get(0).getTasks().isEmpty());
    assertEquals(4, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getStories().get(0).getTasks().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    after = mainApp.getStories().get(0).getTasks().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testTaskDeleteRedo() throws Exception {
    Task before;
    Task after;

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    newStory();
    newTask();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getStories().get(0).getTasks().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    before = mainApp.getStories().get(0).getTasks().get(0);
    assertNotNull(before);

    deleteNewestTask();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertTrue(mainApp.getStories().get(0).getTasks().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getStories().get(0).getTasks().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());
    after = mainApp.getStories().get(0).getTasks().get(0);
    assertNotNull(after);

    assertSame(before, after);

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertTrue(mainApp.getStories().get(0).getTasks().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testTaskEditUndo() throws Exception {
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    newStory();
    newTask();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getStories().get(0).getTasks().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    Task createdTask = mainApp.getStories().get(0).getTasks().get(0);
    assertEquals(taskLabel, createdTask.getLabel());
    assertEquals(taskDescription, createdTask.getTaskDescription());
    assertEquals((long) taskEstimate, (long) createdTask.getTaskEstimation());
    assertEquals(taskStatus, createdTask.getStatus());
    assertEquals(taskPeople, createdTask.getTaskPeople());
//    assertEquals(taskEffort, createdTask.getSpentEffort());

    editNewestTask();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getStories().get(0).getTasks().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());

    Task editedTask = mainApp.getStories().get(0).getTasks().get(0);
    assertEquals(newTaskLabel, editedTask.getLabel());
    assertEquals(newTaskDescription, editedTask.getTaskDescription());
    assertEquals((long) newTaskEstimate, (long) editedTask.getTaskEstimation());
    assertEquals(newTaskStatus, editedTask.getStatus());
    assertEquals(newTaskPeople, editedTask.getTaskPeople());
//    assertEquals(newTaskEffort, editedTask.getSpentEffort());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getStories().get(0).getTasks().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    Task undoneTask = mainApp.getStories().get(0).getTasks().get(0);
    assertEquals(taskLabel, undoneTask.getLabel());
    assertEquals(taskDescription, undoneTask.getTaskDescription());
    assertEquals((long) taskEstimate, (long) undoneTask.getTaskEstimation());
    assertEquals(taskStatus, undoneTask.getStatus());
    assertEquals(taskPeople, undoneTask.getTaskPeople());
//    assertEquals(taskEffort, undoneTask.getSpentEffort());
  }

  @Test
  public void testTaskEditRedo() throws Exception {
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    newStory();
    newTask();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getStories().get(0).getTasks().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Task createdTask = mainApp.getStories().get(0).getTasks().get(0);
    assertEquals(taskLabel, createdTask.getLabel());
    assertEquals(taskDescription, createdTask.getTaskDescription());
    assertEquals((long) taskEstimate, (long) createdTask.getTaskEstimation());
    assertEquals(taskStatus, createdTask.getStatus());
    assertEquals(taskPeople, createdTask.getTaskPeople());
//    assertEquals(taskEffort, createdTask.getSpentEffort());

    editNewestTask();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getStories().get(0).getTasks().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Task editedTask = mainApp.getStories().get(0).getTasks().get(0);
    assertEquals(newTaskLabel, editedTask.getLabel());
    assertEquals(newTaskDescription, editedTask.getTaskDescription());
    assertEquals((long) newTaskEstimate, (long) editedTask.getTaskEstimation());
    assertEquals(newTaskStatus, editedTask.getStatus());
    assertEquals(newTaskPeople, editedTask.getTaskPeople());
//    assertEquals(newTaskEffort, editedTask.getSpentEffort());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getStories().get(0).getTasks().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Task undoneTask = mainApp.getStories().get(0).getTasks().get(0);
    assertEquals(taskLabel, undoneTask.getLabel());
    assertEquals(taskDescription, undoneTask.getTaskDescription());
    assertEquals((long) taskEstimate, (long) undoneTask.getTaskEstimation());
    assertEquals(taskStatus, undoneTask.getStatus());
    assertEquals(taskPeople, undoneTask.getTaskPeople());
//    assertEquals(taskEffort, undoneTask.getSpentEffort());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getStories().get(0).getTasks().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Task redoneTask = mainApp.getStories().get(0).getTasks().get(0);
    assertEquals(newTaskLabel, redoneTask.getLabel());
    assertEquals(newTaskDescription, redoneTask.getTaskDescription());
    assertEquals((long) newTaskEstimate, (long) redoneTask.getTaskEstimation());
    assertEquals(newTaskStatus, redoneTask.getStatus());
    assertEquals(newTaskPeople, redoneTask.getTaskPeople());
//    assertEquals(newTaskEffort, redoneTask.getSpentEffort());
  }

  @Test
  public void testEffortCreateUndo() throws Exception {
    List<Task> tasks;

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    newStory();
    newTask();
    newEffort();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertEquals(1, tasks.get(0).getEfforts().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertTrue(tasks.get(0).getEfforts().isEmpty());

    assertEquals(3, undoRedoHandler.getUndoStack().size());
  }

  @Test
  public void testEffortCreateRedo() throws Exception {
    Effort before;
    Effort after;
    List<Task> tasks;

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    newStory();
    newTask();
    newEffort();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertEquals(1, tasks.get(0).getEfforts().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    before = tasks.get(0).getEfforts().get(0);
    assertNotNull(before);

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertTrue(tasks.get(0).getEfforts().isEmpty());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertEquals(1, tasks.get(0).getEfforts().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    after = tasks.get(0).getEfforts().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testEffortDeleteUndo() throws Exception {
    Effort before;
    Effort after;
    List<Task> tasks;

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    newStory();
    newTask();
    newEffort();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertEquals(1, tasks.get(0).getEfforts().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    before = tasks.get(0).getEfforts().get(0);
    assertNotNull(before);

    deleteNewestEffort();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertTrue(tasks.get(0).getEfforts().isEmpty());
    assertEquals(5, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertEquals(1, tasks.get(0).getEfforts().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    after = tasks.get(0).getEfforts().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testEffortDeleteRedo() throws Exception {
    Effort before;
    Effort after;
    List<Task> tasks;

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    newStory();
    newTask();
    newEffort();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertEquals(1, tasks.get(0).getEfforts().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
    before = tasks.get(0).getEfforts().get(0);
    assertNotNull(before);

    deleteNewestEffort();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertTrue(tasks.get(0).getEfforts().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertEquals(1, tasks.get(0).getEfforts().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());
    after = tasks.get(0).getEfforts().get(0);
    assertNotNull(after);

    assertSame(before, after);

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertTrue(tasks.get(0).getEfforts().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testEffortEditUndo() throws Exception {
    List<Task> tasks;

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    newStory();
    newTask();
    newEffort();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertEquals(1, tasks.get(0).getEfforts().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());

    Effort createdEffort = tasks.get(0).getEfforts().get(0);
    assertEquals(worker, createdEffort.getWorker());
    assertEquals(spentEffort, createdEffort.getSpentEffort());
    assertEquals(comments, createdEffort.getComments());
    assertEquals(dateTime, createdEffort.getDateTime());

    editNewestEffort();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertEquals(1, tasks.get(0).getEfforts().size());
    assertEquals(5, undoRedoHandler.getUndoStack().size());

    Effort editedEffort = tasks.get(0).getEfforts().get(0);
    assertEquals(newWorker, editedEffort.getWorker());
    assertEquals(newSpentEffort, editedEffort.getSpentEffort());
    assertEquals(newComments, editedEffort.getComments());
    assertEquals(newDateTime, editedEffort.getDateTime());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertEquals(1, tasks.get(0).getEfforts().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());

    Effort undoneEffort = tasks.get(0).getEfforts().get(0);
    assertEquals(worker, undoneEffort.getWorker());
    assertEquals(spentEffort, undoneEffort.getSpentEffort());
    assertEquals(comments, undoneEffort.getComments());
    assertEquals(dateTime, undoneEffort.getDateTime());
  }

  @Test
  public void testEffortEditRedo() throws Exception {
    List<Task> tasks;

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    newStory();
    newTask();
    newEffort();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertEquals(1, tasks.get(0).getEfforts().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Effort createdEffort = tasks.get(0).getEfforts().get(0);
    assertEquals(worker, createdEffort.getWorker());
    assertEquals(spentEffort, createdEffort.getSpentEffort());
    assertEquals(comments, createdEffort.getComments());
    assertEquals(dateTime, createdEffort.getDateTime());

    editNewestEffort();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertEquals(1, tasks.get(0).getEfforts().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Effort editedEffort = tasks.get(0).getEfforts().get(0);
    assertEquals(newWorker, editedEffort.getWorker());
    assertEquals(newSpentEffort, editedEffort.getSpentEffort());
    assertEquals(newComments, editedEffort.getComments());
    assertEquals(newDateTime, editedEffort.getDateTime());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertEquals(1, tasks.get(0).getEfforts().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Effort undoneEffort = tasks.get(0).getEfforts().get(0);
    assertEquals(worker, undoneEffort.getWorker());
    assertEquals(spentEffort, undoneEffort.getSpentEffort());
    assertEquals(comments, undoneEffort.getComments());
    assertEquals(dateTime, undoneEffort.getDateTime());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    tasks = mainApp.getStories().get(0).getTasks();
    assertEquals(1, tasks.size());
    assertEquals(1, tasks.get(0).getEfforts().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    Effort redoneEffort = tasks.get(0).getEfforts().get(0);
    assertEquals(newWorker, redoneEffort.getWorker());
    assertEquals(newSpentEffort, redoneEffort.getSpentEffort());
    assertEquals(newComments, redoneEffort.getComments());
    assertEquals(newDateTime, redoneEffort.getDateTime());
  }

  @Test
  public void testSpecialDeleteSkillUndo() throws Exception {
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newSkill();
    newPersonWithSkill();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, person.getSkillSet().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    deleteNewestSkill();
    assertEquals(1, mainApp.getPeople().size(), 1);
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(person.getSkillSet().isEmpty());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, person.getSkillSet().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
  }

  @Test
  public void testSpecialDeleteSkillRedo() throws Exception {
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newSkill();
    newPersonWithSkill();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, person.getSkillSet().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    deleteNewestSkill();
    assertEquals(1, mainApp.getPeople().size(), 1);
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(person.getSkillSet().isEmpty());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, person.getSkillSet().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getPeople().size(), 1);
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(person.getSkillSet().isEmpty());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testSpecialDeleteSkillRedoDeep() throws Exception {
    // Object references will change. Avoid person variable.
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newSkill();
    newPerson();
    editNewestPersonWithSkill();

    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, mainApp.getPeople().get(0).getSkillSet().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    deleteNewestSkill();
    assertEquals(1, mainApp.getPeople().size());
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(mainApp.getPeople().get(0).getSkillSet().isEmpty());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo(); // deleteNewestSkill
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, mainApp.getPeople().get(0).getSkillSet().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.undo(); // edit person
    undoRedoHandler.undo(); // new person
    undoRedoHandler.undo(); // new skill
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
    undoRedoHandler.redo(); // new skill
    undoRedoHandler.redo(); // new person
    undoRedoHandler.redo(); // edit person
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, mainApp.getPeople().get(0).getSkillSet().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo(); // deleteNewestSkill
    assertEquals(1, mainApp.getPeople().size());
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(mainApp.getPeople().get(0).getSkillSet().isEmpty());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testSpecialDeleteSkillRedoDeeper() throws Exception {
    // Object references will change. Avoid person variable.
    Skill skillInList;
    Person personInList;

    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newSkill();
    newPerson();
    editNewestPersonWithSkill();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, mainApp.getPeople().get(0).getSkillSet().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    skillInList = mainApp.getSkills().get(0);
    personInList = mainApp.getPeople().get(0);
    assertEquals(skillInList, personInList.getSkillSet().get(0));

    editNewestSkill();
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, mainApp.getPeople().get(0).getSkillSet().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    skillInList = mainApp.getSkills().get(0);
    personInList = mainApp.getPeople().get(0);
    assertEquals(skillInList, personInList.getSkillSet().get(0));

    deleteNewestSkill();
    assertEquals(1, mainApp.getPeople().size());
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(mainApp.getPeople().get(0).getSkillSet().isEmpty());
    assertEquals(5, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo(); // deleteNewestSkill
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, mainApp.getPeople().get(0).getSkillSet().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    skillInList = mainApp.getSkills().get(0);
    personInList = mainApp.getPeople().get(0);
    assertEquals(skillInList, personInList.getSkillSet().get(0));

    undoRedoHandler.undo(); // edit skill
    undoRedoHandler.undo(); // edit person
    undoRedoHandler.undo(); // new person
    undoRedoHandler.undo(); // new skill
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
    undoRedoHandler.redo(); // new skill
    undoRedoHandler.redo(); // new person
    assertTrue(mainApp.getPeople().get(0).getSkillSet().isEmpty());

    undoRedoHandler.redo(); // edit person
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, mainApp.getPeople().get(0).getSkillSet().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(2, undoRedoHandler.getRedoStack().size());

    skillInList = mainApp.getSkills().get(0);
    personInList = mainApp.getPeople().get(0);
    assertEquals(skillInList, personInList.getSkillSet().get(0));

    undoRedoHandler.redo(); // edit skill
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getSkills().size());
    assertEquals(1, mainApp.getPeople().get(0).getSkillSet().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    skillInList = mainApp.getSkills().get(0);
    personInList = mainApp.getPeople().get(0);
    assertEquals(skillInList, personInList.getSkillSet().get(0));

    undoRedoHandler.redo(); // deleteNewestSkill
    assertEquals(1, mainApp.getPeople().size());
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(mainApp.getPeople().get(0).getSkillSet().isEmpty());
    assertEquals(5, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testSpecialDeletePersonUndo() throws Exception {
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    newTeamWithMember();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, team.getTeamMembers().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    deleteNewestPerson();
    assertEquals(1, mainApp.getTeams().size());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(team.getTeamMembers().isEmpty());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, team.getTeamMembers().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
  }

  @Test
  public void testSpecialDeletePersonRedo() throws Exception {
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    newTeamWithMember();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, team.getTeamMembers().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    deleteNewestPerson();
    assertEquals(1, mainApp.getTeams().size());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(team.getTeamMembers().isEmpty());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, team.getTeamMembers().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo();
    assertEquals(1, mainApp.getTeams().size());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(team.getTeamMembers().isEmpty());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testSpecialDeletePersonRedoDeep() throws Exception {
    // Object references will change. Avoid team variable.
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    newTeam();
    editNewestTeamWithMember();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    deleteNewestPerson();
    assertEquals(1, mainApp.getTeams().size());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getTeams().get(0).getTeamMembers().isEmpty());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo(); // deleteNewestPerson
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.undo(); // edit team
    undoRedoHandler.undo(); // new team
    undoRedoHandler.undo(); // new person
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
    undoRedoHandler.redo(); // new person
    undoRedoHandler.redo(); // new team
    undoRedoHandler.redo(); // edit team
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo(); // deleteNewestPerson
    assertEquals(1, mainApp.getTeams().size());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getTeams().get(0).getTeamMembers().isEmpty());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testSpecialDeletePersonRedoDeeper() throws Exception {
    // Object references will change. Avoid team variable.
    Person personInList;
    Team teamInList;

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    newTeam();
    editNewestTeamWithMember();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    personInList = mainApp.getPeople().get(0);
    teamInList = mainApp.getTeams().get(0);
    assertEquals(personInList, teamInList.getTeamMembers().get(0));
    assertEquals(teamInList, personInList.getTeam());

    editNewestPerson();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    personInList = mainApp.getPeople().get(0);
    teamInList = mainApp.getTeams().get(0);
    assertEquals(personInList, teamInList.getTeamMembers().get(0));
    assertEquals(teamInList, personInList.getTeam());

    deleteNewestPerson();
    assertEquals(1, mainApp.getTeams().size());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getTeams().get(0).getTeamMembers().isEmpty());
    assertEquals(5, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo(); // deleteNewestPerson
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    personInList = mainApp.getPeople().get(0);
    teamInList = mainApp.getTeams().get(0);
    assertEquals(personInList, teamInList.getTeamMembers().get(0));
    assertEquals(teamInList, personInList.getTeam());

    undoRedoHandler.undo(); // edit person
    undoRedoHandler.undo(); // edit team
    undoRedoHandler.undo(); // new team
    undoRedoHandler.undo(); // new person
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
    undoRedoHandler.redo(); // new person
    undoRedoHandler.redo(); // new team
    assertTrue(mainApp.getTeams().get(0).getTeamMembers().isEmpty());

    undoRedoHandler.redo(); // edit team
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(2, undoRedoHandler.getRedoStack().size());

    personInList = mainApp.getPeople().get(0);
    teamInList = mainApp.getTeams().get(0);
    assertEquals(personInList, teamInList.getTeamMembers().get(0));
    assertEquals(teamInList, personInList.getTeam());

    undoRedoHandler.redo(); // edit person
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    personInList = mainApp.getPeople().get(0);
    teamInList = mainApp.getTeams().get(0);
    assertEquals(personInList, teamInList.getTeamMembers().get(0));
    assertEquals(teamInList, personInList.getTeam());

    undoRedoHandler.redo(); // deleteNewestPerson
    assertEquals(1, mainApp.getTeams().size());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getTeams().get(0).getTeamMembers().isEmpty());
    assertEquals(5, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testSpecialDeleteTeamUndo() throws Exception {
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    newPerson();
    newTeamWithMember();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, team.getTeamMembers().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());

    deleteNewestTeam();
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertEquals(3, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, team.getTeamMembers().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
  }

  @Test
  public void testSpecialDeleteTeamRedo() throws Exception {
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    newTeamWithMember();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, team.getTeamMembers().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    deleteNewestTeam();
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, team.getTeamMembers().size());
    assertEquals(2, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.redo();
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testSpecialDeleteTeamRedoDeep() throws Exception {
    // Object references will change. Avoid team variable.
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    newTeam();
    editNewestTeamWithMember();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    deleteNewestTeam();
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo(); // deleteNewestTeam
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.undo(); // edit team
    undoRedoHandler.undo(); // new team
    undoRedoHandler.undo(); // new person
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
    undoRedoHandler.redo(); // new person
    undoRedoHandler.redo(); // new team

    undoRedoHandler.redo(); // edit team
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Person personInList = mainApp.getPeople().get(0);
    Team teamInList = mainApp.getTeams().get(0);
    assertEquals(personInList, teamInList.getTeamMembers().get(0));
    assertEquals(teamInList, personInList.getTeam());

    undoRedoHandler.redo(); // deleteNewestTeam
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testSpecialDeleteTeamRedoDeeper() throws Exception {
    // Object references will change. Avoid team variable.
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    newPerson();
    newTeam();
    editNewestTeamWithMember();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    editNewestPerson();
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    deleteNewestTeam();
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertEquals(5, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    undoRedoHandler.undo(); // deleteNewestTeam
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    undoRedoHandler.undo(); // edit person
    undoRedoHandler.undo(); // edit team
    undoRedoHandler.undo(); // new team
    undoRedoHandler.undo(); // new person
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
    undoRedoHandler.redo(); // new person
    undoRedoHandler.redo(); // new team
    assertTrue(mainApp.getTeams().get(0).getTeamMembers().isEmpty());

    undoRedoHandler.redo(); // edit team
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(3, undoRedoHandler.getUndoStack().size());
    assertEquals(2, undoRedoHandler.getRedoStack().size());

    Person personInList = mainApp.getPeople().get(0);
    Team teamInList = mainApp.getTeams().get(0);
    assertEquals(personInList, teamInList.getTeamMembers().get(0));
    assertEquals(teamInList, personInList.getTeam());

    undoRedoHandler.redo(); // edit person
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().get(0).getTeamMembers().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    Person editedPersonInList = mainApp.getPeople().get(0);
    Team editedTeamInList = mainApp.getTeams().get(0);
    assertEquals(editedPersonInList, editedTeamInList.getTeamMembers().get(0));
    assertEquals(editedTeamInList, editedPersonInList.getTeam());

    undoRedoHandler.redo(); // deleteNewestTeam
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertEquals(5, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testEditStoryDependenciesUndo() throws Exception {
    //testing editing dependencies of multiple stories in one action
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    createAndEditStoryDependencies();

    assertEquals(3, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());

    dependantStory1 = mainApp.getStories().get(0);
    dependantStory2 = mainApp.getStories().get(1);
    dependantStory3 = mainApp.getStories().get(2);

    assertEquals(2, dependantStory1.getDependencies().size());
    assertSame(dependantStory2, dependantStory1.getDependencies().get(0));
    assertSame(dependantStory3, dependantStory1.getDependencies().get(1));
    assertEquals(1, dependantStory2.getDependencies().size());
    assertSame(dependantStory3, dependantStory2.getDependencies().get(0));
    assertTrue(dependantStory3.getDependencies().isEmpty());

    undoRedoHandler.undo();

    assertEquals(3, mainApp.getStories().size());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    dependantStory1 = mainApp.getStories().get(0);
    dependantStory2 = mainApp.getStories().get(1);
    dependantStory3 = mainApp.getStories().get(2);

    assertTrue(dependantStory1.getDependencies().isEmpty());
    assertEquals(1, dependantStory2.getDependencies().size());
    assertSame(dependantStory1, dependantStory2.getDependencies().get(0));
    assertEquals(2, dependantStory3.getDependencies().size());
    assertSame(dependantStory1, dependantStory3.getDependencies().get(0));
    assertSame(dependantStory2, dependantStory3.getDependencies().get(1));
  }


  @Test
  public void testEditStoryDependenciesRedo() throws Exception {
    //testing editing dependencies of multiple stories in one action
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());

    createAndEditStoryDependencies();

    assertEquals(3, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    dependantStory1 = mainApp.getStories().get(0);
    dependantStory2 = mainApp.getStories().get(1);
    dependantStory3 = mainApp.getStories().get(2);

    Story origDependantStory1 = dependantStory1;
    Story origDependantStory2 = dependantStory2;
    Story origDependantStory3 = dependantStory3;

    assertEquals(2, dependantStory1.getDependencies().size());
    assertSame(dependantStory2, dependantStory1.getDependencies().get(0));
    assertSame(dependantStory3, dependantStory1.getDependencies().get(1));
    assertEquals(1, dependantStory2.getDependencies().size());
    assertSame(dependantStory3, dependantStory2.getDependencies().get(0));
    assertTrue(dependantStory3.getDependencies().isEmpty());

    undoRedoHandler.undo();

    assertEquals(3, mainApp.getStories().size());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    dependantStory1 = mainApp.getStories().get(0);
    dependantStory2 = mainApp.getStories().get(1);
    dependantStory3 = mainApp.getStories().get(2);

    assertSame(origDependantStory1, dependantStory1);
    assertSame(origDependantStory2, dependantStory2);
    assertSame(origDependantStory3, dependantStory3);

    assertTrue(dependantStory1.getDependencies().isEmpty());
    assertEquals(1, dependantStory2.getDependencies().size());
    assertSame(dependantStory1, dependantStory2.getDependencies().get(0));
    assertEquals(2, dependantStory3.getDependencies().size());
    assertSame(dependantStory1, dependantStory3.getDependencies().get(0));
    assertSame(dependantStory2, dependantStory3.getDependencies().get(1));

    undoRedoHandler.redo();

    assertEquals(3, mainApp.getStories().size());
    assertEquals(1, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().empty());

    dependantStory1 = mainApp.getStories().get(0);
    dependantStory2 = mainApp.getStories().get(1);
    dependantStory3 = mainApp.getStories().get(2);

    assertSame(origDependantStory1, dependantStory1);
    assertSame(origDependantStory2, dependantStory2);
    assertSame(origDependantStory3, dependantStory3);

    assertEquals(2, dependantStory1.getDependencies().size());
    assertSame(dependantStory2, dependantStory1.getDependencies().get(0));
    assertSame(dependantStory3, dependantStory1.getDependencies().get(1));
    assertEquals(1, dependantStory2.getDependencies().size());
    assertSame(dependantStory3, dependantStory2.getDependencies().get(0));
    assertTrue(dependantStory3.getDependencies().isEmpty());
  }

  @Test
  public void testBacklogCascadingDeleteUndo() throws Exception {
    Backlog before;
    Backlog after;

    //testing deleting and undoing an empty backlog
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());

    newPerson();
    newStory();
    newEstimate();
    newSprint();
    newBacklogWithSprint();

    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    before = mainApp.getBacklogs().get(0);
    assertNotNull(before);

    deleteNewestBacklog();

    assertTrue(mainApp.getBacklogs().isEmpty());
    assertEquals(5, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    after = mainApp.getBacklogs().get(0);
    assertNotNull(after);

    assertSame(before, after);
  }

  @Test
  public void testBacklogCascadingDeleteRedo() throws Exception {
    Backlog before;
    Backlog after;

    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    newPerson();
    newStory();
    newEstimate();
    newSprint();
    newBacklogWithSprint();


    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
    before = mainApp.getBacklogs().get(0);
    assertNotNull(before);

    deleteNewestBacklog();

    assertTrue(mainApp.getBacklogs().isEmpty());
    assertEquals(5, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(4, undoRedoHandler.getUndoStack().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());
    after = mainApp.getBacklogs().get(0);
    assertNotNull(after);

    assertSame(before, after);

    undoRedoHandler.redo();
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertEquals(5, undoRedoHandler.getUndoStack().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());
  }

  @Test
  public void testSpecialDeleteProjectUndo() throws Exception {
    // assign both release and sprints to a project and test that undo brings them back
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(mainApp.getSprints().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().isEmpty());

    newPerson();
    newTeamWithMember();
    newStoryWithAC();
    newBacklog();
    newProjectWithTeamAndBacklog();
    newRelease();
    newSprint();

    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, mainApp.getSprints().size());
    assertEquals(7, undoRedoHandler.getUndoStack().size());

    Project origProject = mainApp.getProjects().get(0);
    Release origRelease = mainApp.getReleases().get(0);
    Sprint origSprint = mainApp.getSprints().get(0);

    deleteNewestProject();

    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getBacklogs().size());
    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(mainApp.getSprints().isEmpty());
    assertEquals(8, undoRedoHandler.getUndoStack().size());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, mainApp.getSprints().size());
    assertEquals(7, undoRedoHandler.getUndoStack().size());

    assertSame(origProject, mainApp.getProjects().get(0));
    assertSame(origRelease, mainApp.getReleases().get(0));
    assertSame(origProject, mainApp.getReleases().get(0).getProjectRelease());
    assertSame(origSprint, mainApp.getSprints().get(0));
    assertSame(origProject, mainApp.getSprints().get(0).getSprintProject());
    assertSame(origRelease, mainApp.getSprints().get(0).getSprintRelease());
  }

  @Test
  public void testSpecialDeleteProjectRedo() throws Exception {
    // assign both release and sprints to a project and test that undo brings them back and redo
    // deletes them again
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(mainApp.getSprints().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    newPerson();
    newTeamWithMember();
    newStoryWithAC();
    newBacklog();
    newProjectWithTeamAndBacklog();
    newRelease();
    newSprint();

    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, mainApp.getSprints().size());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    Project origProject = mainApp.getProjects().get(0);
    Release origRelease = mainApp.getReleases().get(0);
    Sprint origSprint = mainApp.getSprints().get(0);

    deleteNewestProject();

    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getBacklogs().size());
    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(mainApp.getSprints().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    undoRedoHandler.undo();

    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, mainApp.getSprints().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    assertSame(origProject, mainApp.getProjects().get(0));
    assertSame(origRelease, mainApp.getReleases().get(0));
    assertSame(origProject, mainApp.getReleases().get(0).getProjectRelease());
    assertSame(origSprint, mainApp.getSprints().get(0));
    assertSame(origProject, mainApp.getSprints().get(0).getSprintProject());
    assertSame(origRelease, mainApp.getSprints().get(0).getSprintRelease());

    undoRedoHandler.redo();

    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getBacklogs().size());
    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(mainApp.getSprints().isEmpty());
    assertTrue(undoRedoHandler.getRedoStack().isEmpty());

    // undo again for good measure, mainly for testing object references
    undoRedoHandler.undo();

    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getTeams().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(1, mainApp.getSprints().size());
    assertEquals(1, undoRedoHandler.getRedoStack().size());

    assertSame(origProject, mainApp.getProjects().get(0));
    assertSame(origRelease, mainApp.getReleases().get(0));
    assertSame(origProject, mainApp.getReleases().get(0).getProjectRelease());
    assertSame(origSprint, mainApp.getSprints().get(0));
    assertSame(origProject, mainApp.getSprints().get(0).getSprintProject());
    assertSame(origRelease, mainApp.getSprints().get(0).getSprintRelease());
  }
}
