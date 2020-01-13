package seng302.group5.model.util;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import seng302.group5.Main;
import seng302.group5.controller.mainAppControllers.ListMainPaneController;
import seng302.group5.controller.mainAppControllers.MenuBarController;
import seng302.group5.controller.mainAppControllers.ToolBarController;
import seng302.group5.model.Backlog;
import seng302.group5.model.Effort;
import seng302.group5.model.Estimate;
import seng302.group5.model.Person;
import seng302.group5.model.Project;
import seng302.group5.model.Release;
import seng302.group5.model.Skill;
import seng302.group5.model.Sprint;
import seng302.group5.model.Story;
import seng302.group5.model.Task;
import seng302.group5.model.Team;
import seng302.group5.model.undoredo.Action;
import seng302.group5.model.undoredo.UndoRedoHandler;
import seng302.group5.model.undoredo.UndoRedoObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Unit testing for revert function.
 * @author Alex Woo, Liang Ma
 */
public class RevertHandlerTest {

  private String personLabel;
  private String firstName;
  private String lastName;
  private ObservableList<Skill> skillSet;

  private String skillName;
  private String skillDescription;

  private String teamLabel;
  private ObservableList<Person> teamMembers;
  private String teamDescription;

  private String projectLabel;
  private String projectName;
  private String projectDescription;

  private String releaseName;
  private String releaseDescription;
  private LocalDate releaseDate;
  private String releaseNotes;
  private Project projectRelease;

  private String storyLabel;
  private String storyLongName;
  private String storyDescription;
  private Person storyCreator;
  private ObservableList<String> storyACs;

  private String backlogLabel;
  private String backlogName;
  private String backlogDescription;
  private Person productOwner;
  private List<Story> backlogStories;
  private Estimate backlogEstimate;

  private String estimateLabel;
  private List<String> estimateSizes;

  private Person person;
  private Skill skill;
  private Team team;
  private Project project;
  private Release release;
  private Story story;
  private Backlog backlog;
  private Estimate estimate;

  private UndoRedoHandler undoRedoHandler;
  private RevertHandler revertHandler;
  private Main mainApp;

  @Before
  public void setUp() throws Exception {

    ListMainPaneController listMainPaneController = mock(ListMainPaneController.class);
    MenuBarController menuBarController = mock(MenuBarController.class);
    ToolBarController toolBarController = mock(ToolBarController.class);
    Stage primaryStage = mock(Stage.class);

    mainApp = new Main();
    mainApp.setLMPC(listMainPaneController);
    mainApp.setMBC(menuBarController);
    mainApp.setTBC(toolBarController);
    mainApp.setPrimaryStage(primaryStage);

    undoRedoHandler = mainApp.getUndoRedoHandler();
    revertHandler = mainApp.getRevertHandler();
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

  private void newSkill() {
    skillName = "C#";
    skillDescription = "Person can program in the C# language";
    skill = new Skill(skillName, skillDescription);

    mainApp.addSkill(skill);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.SKILL_CREATE);
    undoRedoObject.setAgileItem(skill);
    undoRedoObject.addDatum(new Skill(skill));

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


  private void newRelease() {
    releaseName = "TheRelease";
    releaseDescription = "The descriptioning";
    releaseDate = LocalDate.of(1994, Month.JANUARY, 06);
    releaseNotes = "Wagga wagga";
    projectRelease = project;

    release = new Release(releaseName, releaseDescription, releaseNotes,
                          releaseDate, projectRelease);

    mainApp.addRelease(release);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.RELEASE_CREATE);
    undoRedoObject.setAgileItem(release);
    undoRedoObject.addDatum(new Release(release));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newStory() {
    storyLabel = "testS";
    storyLongName ="testStory!";
    storyDescription ="This is a story and it is good";
    storyCreator = person;

    story = new Story(storyLabel, storyLongName, storyDescription, storyCreator, null); //null is fine

    mainApp.addStory(story);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.STORY_CREATE);
    undoRedoObject.setAgileItem(story);
    undoRedoObject.addDatum(new Story(story));

    undoRedoHandler.newAction(undoRedoObject);
  }

  private void newStoryWithAC() {
    storyLabel = "testS";
    storyLongName ="testStory!";
    storyDescription ="This is a story and it is good";
    storyCreator = null;
    storyACs = FXCollections.observableArrayList();
    storyACs.add("FRREEEDOOOMMMMM!!!!!");

    story = new Story(storyLabel, storyLongName, storyDescription, storyCreator, storyACs);
    story.setStoryState(true);

    mainApp.addStory(story);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.STORY_CREATE);
    undoRedoObject.setAgileItem(story);
    undoRedoObject.addDatum(new Story(story));

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

  private void newEstimate() {
    estimateLabel = "Estimate";
    estimateSizes = Arrays.asList("Est0", "Est1", "Est2");
    estimate = new Estimate(estimateLabel, estimateSizes);
    mainApp.addEstimate(estimate);
  }



  @Test
  public void testPersonRevert() throws Exception {

    assertTrue(mainApp.getPeople().isEmpty());

    newPerson();

    assertEquals(1, mainApp.getPeople().size());

    revertHandler.revert();

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());


  }

  @Test
  public void testProjectRevert() throws Exception {

    assertTrue(mainApp.getProjects().isEmpty());

    newProject();

    assertEquals(1, mainApp.getProjects().size());

    revertHandler.revert();

    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

  }

  @Test
  public void testSkillRevert() throws Exception {

    assertTrue(mainApp.getSkills().isEmpty());

    newSkill();

    assertEquals(1, mainApp.getSkills().size());

    revertHandler.revert();

    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

  }

  @Test
  public void testTeamRevert() throws Exception {

    assertTrue(mainApp.getTeams().isEmpty());

    newTeam();

    assertEquals(1, mainApp.getTeams().size());

    revertHandler.revert();

    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

  }

  @Test
  public void testReleaseRevert() throws Exception {

    assertTrue(mainApp.getReleases().isEmpty());

    newRelease();

    assertEquals(1, mainApp.getReleases().size());

    revertHandler.revert();

    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

  }

  @Test
  public void testStoryRevert() throws Exception {

    assertTrue(mainApp.getStories().isEmpty());

    newStory();

    assertEquals(1, mainApp.getStories().size());

    revertHandler.revert();

    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

  }


  @Test
  public void testStoryWithACRevert() throws Exception {

    assertTrue(mainApp.getStories().isEmpty());

    newStoryWithAC();

    assertEquals(1, mainApp.getStories().size());

    revertHandler.revert();

    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

  }

  @Test
  public void testStoryWith2ACRevert() throws Exception {

    assertTrue(mainApp.getStories().isEmpty());

    newStoryWithAC();
    newStoryWithAC();

    assertEquals(2, mainApp.getStories().size());

    revertHandler.revert();

    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

  }

  @Test
  public void testStoryWithACAndNotRevert() throws Exception {

    assertTrue(mainApp.getStories().isEmpty());

    newStoryWithAC();
    newStory();

    assertEquals(2, mainApp.getStories().size());

    revertHandler.revert();

    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

  }

  @Test
  public void testStoryWith2ACAndRandomObjectsRevert() throws Exception {

    assertTrue(mainApp.getStories().isEmpty());

    newPerson();
    newStoryWithAC();
    newStoryWithAC();
    newTeam();

    assertEquals(2, mainApp.getStories().size());

    revertHandler.revert();

    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

  }

  @Test
  public void testBacklogRevert() throws Exception {

    assertTrue(mainApp.getBacklogs().isEmpty());

    newPerson();
    newStory();
    newBacklog();

    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getBacklogs().size());

    revertHandler.revert();

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

  }

  @Test
  public void testEstimateRevert() throws Exception {

    assertTrue(mainApp.getEstimates().isEmpty());

    newPerson();
    newStory();
    newEstimate();
    newBacklog();

    assertEquals(1, mainApp.getPeople().size());
    assertEquals(1, mainApp.getStories().size());
    assertEquals(1, mainApp.getEstimates().size());
    assertEquals(1, mainApp.getBacklogs().size());

    revertHandler.revert();

    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(mainApp.getEstimates().isEmpty());
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testProjectWithBacklogRevert() throws Exception {

    newProject();
    newBacklog();

    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getBacklogs().size());

    project.setBacklog(backlog);

    assertNotEquals(null, project.getBacklog());
    assertEquals(backlog, project.getBacklog());

    revertHandler.revert();

    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testProjectAddedBacklogRevert() throws Exception {

    newEstimate();
    newPerson();
    newStory();
    newBacklog();
    newProject();

    revertHandler.setLastSaved(); // as if we just loaded

    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(null, project.getBacklog());
    assertNotEquals(backlog, project.getBacklog());

    project.setBacklog(backlog);

    assertNotEquals(null, project.getBacklog());
    assertEquals(backlog, project.getBacklog());

    revertHandler.revert();

    project = mainApp.getProjects().get(0);  //Makes the project the proper project
    assertEquals(null, project.getBacklog());
    assertNotEquals(backlog, project.getBacklog());

    assertEquals(1, mainApp.getProjects().size());
    assertEquals(1, mainApp.getBacklogs().size());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }


  @Test
  public void testEmptyRevert() throws Exception {
    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(mainApp.getEstimates().isEmpty());

    revertHandler.revert();

    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(mainApp.getEstimates().isEmpty());
  }

  @Test
  public void testMultipleRevert() throws Exception {

    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(mainApp.getEstimates().isEmpty());

    newStory();
    newProject();
    newSkill();
    newPersonWithSkill();
    newPerson();
    newProject();
    newRelease();
    newTeam();
    newTeam();
    newTeamWithMember();
    newSkill();
    newSkill();
    newStory();
    newBacklog();
    newEstimate();

    assertEquals(2, mainApp.getStories().size());
    assertEquals(1, mainApp.getReleases().size());
    assertEquals(3, mainApp.getTeams().size());
    assertEquals(2, mainApp.getPeople().size());
    assertEquals(3, mainApp.getSkills().size());
    assertEquals(2, mainApp.getProjects().size());
    assertEquals(1, mainApp.getBacklogs().size());
    assertEquals(1, mainApp.getEstimates().size());

    revertHandler.revert();

    assertTrue(mainApp.getStories().isEmpty());
    assertTrue(mainApp.getReleases().isEmpty());
    assertTrue(mainApp.getTeams().isEmpty());
    assertTrue(mainApp.getPeople().isEmpty());
    assertTrue(mainApp.getSkills().isEmpty());
    assertTrue(mainApp.getProjects().isEmpty());
    assertTrue(mainApp.getBacklogs().isEmpty());
    assertTrue(mainApp.getEstimates().isEmpty());
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());

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
    revertHandler.revert();
    assertTrue(undoRedoHandler.getUndoStack().empty());
    assertTrue(undoRedoHandler.getRedoStack().empty());
  }

  @Test
  public void testDependencyRevert() {
    assertTrue(undoRedoHandler.getUndoStack().empty());
    Person p = new Person();
    p.setLabel("Help me");
    Story story1 = new Story("One", "name", "desc", p);
    Story story2 = new Story("Two", "name", "desc", p);
    Story story3 = new Story("Three", "name", "desc", p);
    Story story4 = new Story("Four", "name", "desc", p);
    Story story5 = new Story("Five", "name", "desc", p);

    story1.addAllDependencies(Arrays.asList(story2, story4));
    story2.addAllDependencies(Arrays.asList(story4, story5));
    story3.addAllDependencies(Arrays.asList(story5));
    story4.addDependency(story3);

    addStoryWithDep(story1);
    addStoryWithDep(story2);
    addStoryWithDep(story3);
    addStoryWithDep(story4);
    addStoryWithDep(story5);
    revertHandler.setLastSaved();

    story1.removeAllDependencies();
    story2.removeAllDependencies();
    story3.removeAllDependencies();
    story4.removeAllDependencies();
    story5.removeAllDependencies();

    revertHandler.revert();

    List<Story> mainStories = mainApp.getStories();
    story1 = mainStories.get(2);
    story2 = mainStories.get(4);
    story3 = mainStories.get(3);
    story4 = mainStories.get(1);
    story5 = mainStories.get(0);
    assertSame(story2, story1.getDependencies().get(0));
    assertSame(story4, story1.getDependencies().get(1));
    assertSame(story4, story2.getDependencies().get(0));
    assertSame(story5, story2.getDependencies().get(1));
    assertSame(story5, story3.getDependencies().get(0));
  }

  public void addStoryWithDep(Story story) {
    mainApp.addStory(story);

    UndoRedoObject undoRedoObject = new UndoRedoObject();
    undoRedoObject.setAction(Action.STORY_CREATE);
    undoRedoObject.setAgileItem(story);
    undoRedoObject.addDatum(new Story(story));

    undoRedoHandler.newAction(undoRedoObject);
  }

  @Test
  public void testSprintRevert() {
    Project p1 = new Project("p1", "proj1", "its a project");
    Person pers1 = new Person();
    pers1.setLabel("pers1");
    Estimate e1 = new Estimate("test est", Arrays.asList("0", "1", "2", "3"));
    Backlog b1 = new Backlog("b1", "back1", "Its a backlog", pers1, e1);
    p1.setBacklog(b1);

    Story s1 = new Story("s1", "story1", "its a story", pers1);
    Story s2 = new Story("s2", "story2", "its a story", pers1);
    Story s3 = new Story("s3", "story3", "its a story", pers1);
    Story s4 = new Story("s4", "story4", "its a story", pers1);

    b1.addStory(s1, 3);
    b1.addStory(s2, 2);
    b1.addStory(s3, 0);
    b1.addStory(s4, 2);

    Team t1 = new Team("t1", "its a team");
    Release r1 = new Release("r1", "its a release", "to be released", LocalDate.now(), p1);
    Sprint
        sprint =
        new Sprint("s1", "sprint1", "its a sprint", b1, p1, t1, r1, LocalDate.of(1994, 03, 20),
                   LocalDate.now(), Arrays.asList(s1, s2, s3, s4));

    mainApp.addProject(p1);
    mainApp.addPerson(pers1);
    mainApp.addEstimate(e1);
    mainApp.addBacklog(b1);
    mainApp.addStory(s1); mainApp.addStory(s2); mainApp.addStory(s3); mainApp.addStory(s4);
    mainApp.addTeam(t1);
    mainApp.addRelease(r1);
    mainApp.addSprint(sprint);
    revertHandler.setLastSaved();

    sprint.setLabel("huehue");
    sprint.setSprintProject(new Project());
    sprint.setSprintBacklog(new Backlog());
    sprint.removeAllStories();
    sprint.setSprintTeam(new Team());

    revertHandler.revert();
    Sprint revSprint = mainApp.getSprints().get(0);
    assertNotSame(sprint, revSprint);
    assertSame(revSprint.getSprintProject(), mainApp.getProjects().get(0));
    assertSame(revSprint.getSprintBacklog(), mainApp.getBacklogs().get(0));
    assertSame(revSprint.getSprintTeam(), mainApp.getTeams().get(0));
    assertSame(revSprint.getSprintRelease(), mainApp.getReleases().get(0));
    List<Story> mainStories = mainApp.getStories();
    List<Story> backlogStories = mainApp.getBacklogs().get(0).getStories();
    List<Story> sprintStories = mainApp.getStories();
    assertSame(mainStories.get(0), sprintStories.get(0));
    assertSame(backlogStories.get(0), sprintStories.get(0));
    assertSame(mainStories.get(1), sprintStories.get(1));
    assertSame(backlogStories.get(1), sprintStories.get(1));
    assertSame(mainStories.get(2), sprintStories.get(2));
    assertSame(backlogStories.get(2), sprintStories.get(2));
    assertSame(mainStories.get(3), sprintStories.get(3));
    assertSame(backlogStories.get(3), sprintStories.get(3));
  }

  @Test
  public void testTaskRevert() {
    Project proj1 = new Project("p1", "proj1", "its a project");
    Person pers1 = new Person();
    pers1.setLabel("pers1");
    Estimate e1 = new Estimate("test est", Arrays.asList("0", "1", "2", "3"));
    Backlog b1 = new Backlog("b1", "back1", "Its a backlog", pers1, e1);
    proj1.setBacklog(b1);

    Story s1 = new Story("s1", "story1", "its a story", pers1);
    Story s2 = new Story("s2", "story2", "its a story", pers1);
    Story s3 = new Story("s3", "story3", "its a story", pers1);
    Story s4 = new Story("s4", "story4", "its a story", pers1);

    b1.addStory(s1, 3);
    b1.addStory(s2, 2);
    b1.addStory(s3, 0);
    b1.addStory(s4, 2);

    Team team1 = new Team("t1", "its a team");
    team1.addTeamMember(pers1);
    Release r1 = new Release("r1", "its a release", "to be released", LocalDate.now(), proj1);
    Sprint sprint =
        new Sprint("s1", "sprint1", "its a sprint", b1, proj1, team1, r1, LocalDate.of(1994, 3, 20),
                   LocalDate.now(), Arrays.asList(s1, s2, s3, s4));

    List<Person> allocatedPeople = new ArrayList<>();
    allocatedPeople.add(pers1);
    Task task1 = new Task("Task Label", "descr", 20, null, allocatedPeople);
//    task1.updateSpentEffort(pers1, 67);
    Task task2 = new Task("Task sprint", "descr", 20, null, allocatedPeople);
//    task2.updateSpentEffort(pers1, 9001);
    s1.addTask(task1);
    sprint.addTask(task2);

    mainApp.addProject(proj1);
    mainApp.addPerson(pers1);
    mainApp.addEstimate(e1);
    mainApp.addBacklog(b1);
    mainApp.addStory(s1); mainApp.addStory(s2); mainApp.addStory(s3); mainApp.addStory(s4);
    mainApp.addTeam(team1);
    mainApp.addRelease(r1);
    mainApp.addSprint(sprint);
    revertHandler.setLastSaved();

    task1.setLabel("HUEHUHFUEHIVUHSIDUH");
    task1.setTaskDescription("HUEHUHFUEHIVUHSIDUH");
    task1.setTaskEstimation(9001);
    task1.removeAllTaskPeople();
    task2.setLabel("HUEHUHFUEHIVUHSIDUH");
    task2.setTaskDescription("HUEHUHFUEHIVUHSIDUH");
    task2.setTaskEstimation(420);
    task2.removeAllTaskPeople();

    revertHandler.revert();

    Person revPerson = mainApp.getPeople().get(0);
    Story revStory = mainApp.getStories().get(0);
    Sprint revSprint = mainApp.getSprints().get(0);
    Task revTask1 = revStory.getTasks().get(0);
    Task revTask2 = revSprint.getTasks().get(0);

    assertSame(revPerson, revTask1.getTaskPeople().get(0));
    assertSame(revPerson, revTask2.getTaskPeople().get(0));
//    assertEquals((long) 67, (long) revTask1.getSpentEffort().get(revPerson));
//    assertEquals((long) 9001, (long) revTask2.getSpentEffort().get(revPerson));
  }

  @Test
  public void testEffortRevert() {
    Project proj1 = new Project("p1", "proj1", "its a project");
    Person pers1 = new Person();
    pers1.setLabel("pers1");
    Estimate e1 = new Estimate("test est", Arrays.asList("0", "1", "2", "3"));
    Backlog b1 = new Backlog("b1", "back1", "Its a backlog", pers1, e1);
    proj1.setBacklog(b1);

    Story s1 = new Story("s1", "story1", "its a story", pers1);
    Story s2 = new Story("s2", "story2", "its a story", pers1);

    b1.addStory(s1, 3);
    b1.addStory(s2, 2);

    Team team1 = new Team("t1", "its a team");
    team1.addTeamMember(pers1);
    Release r1 = new Release("r1", "its a release", "to be released", LocalDate.now(), proj1);
    Sprint sprint =
        new Sprint("s1", "sprint1", "its a sprint", b1, proj1, team1, r1, LocalDate.of(1994, 3, 20),
                   LocalDate.now(), Arrays.asList(s1, s2));

    List<Person> allocatedPeople = new ArrayList<>();
    allocatedPeople.add(pers1);
    Task task1 = new Task("Task Label", "descr", 20, null, allocatedPeople);
    Task task2 = new Task("Task sprint", "descr", 20, null, allocatedPeople);
    Effort effort1 = new Effort(pers1, 120, "session 1",
                                LocalDateTime.of(2015, 10, 1, 1, 1));
    Effort effort2 = new Effort(pers1, 130, "session 2",
                                LocalDateTime.of(2014, 4, 4, 4, 4));
    task1.addEffort(effort1);
    task2.addEffort(effort2);
    s1.addTask(task1);
    sprint.addTask(task2);

    mainApp.addProject(proj1);
    mainApp.addPerson(pers1);
    mainApp.addEstimate(e1);
    mainApp.addBacklog(b1);
    mainApp.addStory(s1); mainApp.addStory(s2);
    mainApp.addTeam(team1);
    mainApp.addRelease(r1);
    mainApp.addSprint(sprint);

    revertHandler.setLastSaved();

    effort1.setWorker(new Person());  // note: not even a valid action
    effort1.setSpentEffort(9001);
    effort1.setComments("session 1 updated");
    effort1.setDateTime(LocalDateTime.of(1, 1, 1, 1, 1));
    effort2.setWorker(new Person());
    effort2.setSpentEffort(9002);
    effort2.setComments("session 2 updated");
    effort2.setDateTime(LocalDateTime.of(2, 2, 2, 2, 2));

    revertHandler.revert();

    Person revPerson = mainApp.getPeople().get(0);
    Story revStory = mainApp.getStories().get(0);
    Sprint revSprint = mainApp.getSprints().get(0);
    Task revTask1 = revStory.getTasks().get(0);
    Task revTask2 = revSprint.getTasks().get(0);
    Effort revEffort1 = revTask1.getEfforts().get(0);
    Effort revEffort2 = revTask2.getEfforts().get(0);

    assertSame(revPerson, revTask1.getTaskPeople().get(0));
    assertSame(revPerson, revTask2.getTaskPeople().get(0));

    assertSame(revPerson, revEffort1.getWorker());
    assertEquals(120, revEffort1.getSpentEffort());
    assertEquals("session 1", revEffort1.getComments());
    assertEquals(LocalDateTime.of(2015, 10, 1, 1, 1), revEffort1.getDateTime());

    assertSame(revPerson, revEffort2.getWorker());
    assertEquals(130, revEffort2.getSpentEffort());
    assertEquals("session 2", revEffort2.getComments());
    assertEquals(LocalDateTime.of(2014, 4, 4, 4, 4), revEffort2.getDateTime());
  }
}
