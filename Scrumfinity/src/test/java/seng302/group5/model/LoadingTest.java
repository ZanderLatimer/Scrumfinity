package seng302.group5.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng302.group5.Main;
import seng302.group5.model.util.Loading;
import seng302.group5.model.util.Saving;
import seng302.group5.model.util.Settings;

/**
 * Testing loading functionality, as these will fail if saving is done incorrectly, doubles as saving test.
 * Created by Michael on 4/23/2015.
 */
public class LoadingTest {
  Saving saving;
  Loading loading;
  Main savedMain;
  Main loadedMain;
  Project project1;
  Project project2;
  Project project3;
  Person person1;
  Person person2;
  Person person3;
  Skill skill1;
  Skill skill2;
  Skill skill3;
  ObservableList<Skill> skillSet1;
  ObservableList<Skill> skillSet2;
  ObservableList<Skill> skillSet3;
  Team team1;
  Team team2;
  Team team3;
  ObservableList<Person> memberList1;
  ObservableList<Person> memberList2;
  Release release1;
  Release release2;
  Release release3;
  AgileHistory teamHistory1;
  AgileHistory teamHistory2;
  AgileHistory teamHistory3;
  Story story1;
  Story story2;
  Story story3;
  Story storyWithLoggedEffort;
  Backlog backlog1;
  Backlog backlog2;
  Backlog backlog3;
  Estimate estimate1;
  Estimate estimate2;
  Task task1;
  Task task2;
  Task task3;

  //for testing acs:
  ObservableList<String> acs;
  ObservableList<String> acs2;

  @Before
  public void setUp() {
    savedMain = new Main();
    loadedMain = new Main();

  }

  /**
   * Since everything uses this though might as well put it in a function.
   */
  private void saveAndLoad() {
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator + "BacklogSave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);
  }

  public void createVanillaProjects() {
    project1 = new Project();
    project1.setLabel("Project1");
    project1.setProjectName("No Project Description");
    savedMain.addProject(project1);
    project2 = new Project();
    project2.setLabel("Project2");
    project2.setProjectName("Has Description");
    project2.setProjectDescription("Proj Descroption");
    savedMain.addProject(project2);
    project3 = new Project();
    project3.setLabel("Project3");
    project3.setProjectName("Back to no description");
    savedMain.addProject(project3);
  }

  public void createVanillaPeople() {
    person1 = new Person();
    person1.setLabel("Person1");
    person1.setFirstName("Only first name");
    savedMain.addPerson(person1);
    person2 = new Person();
    person2.setLabel("Person2");
    person2.setFirstName("Both first");
    person2.setLastName("And last");
    savedMain.addPerson(person2);
    person3 = new Person();
    person3.setLabel("Person3");
    person3.setLastName("Only last name");
    savedMain.addPerson(person3);
  }

  public void createSkillsWithDependency() {
    skill1 = new Skill();
    skill1.setLabel("Skill1");
    skill1.setSkillDescription("Skill description 1");
    savedMain.addSkill(skill1);
    skill2 = new Skill();
    skill2.setLabel("Skill2");
    savedMain.addSkill(skill2);
    skill3 = new Skill();
    skill3.setLabel("Skill3");
    skill3.setSkillDescription("Skill description 3");
    savedMain.addSkill(skill3);

    createVanillaPeople();
    skillSet1 = FXCollections.observableArrayList();
    skillSet1.add(skill2);
    person1.setSkillSet(skillSet1);
    skillSet2 = FXCollections.observableArrayList();
    skillSet2.addAll(skill1, skill3);
    person2.setSkillSet(skillSet2);
    skillSet3 = FXCollections.observableArrayList();
    skillSet3.addAll(skill3, skill2, skill1);
    person3.setSkillSet(skillSet3);
  }

  public void createTeamWithDependency() {
    createVanillaPeople();
    team1 = new Team();
    team1.setLabel("Team1");
    team1.setTeamDescription("Description Team1");
    memberList1 = FXCollections.observableArrayList();
    memberList1.addAll(person1);
    team1.setTeamMembers(memberList1);
    savedMain.addTeam(team1);
    team2 = new Team();
    team2.setLabel("Team2");
    memberList2 = FXCollections.observableArrayList();
    memberList2.addAll(person3, person2);
    team2.setTeamMembers(memberList2);
    savedMain.addTeam(team2);
    team3 = new Team();
    team3.setLabel("Team3");
    team3.setTeamDescription("Description Team2");
    savedMain.addTeam(team3);
  }

  public void createReleaseWithDependency() {
    createVanillaProjects();
    release1 = new Release();
    release1.setLabel("Release1");
    release1.setReleaseDescription("Description Release1");
    release1.setReleaseNotes("Notes Release1");
    release1.setProjectRelease(project3);
    release1.setReleaseDate(LocalDate.of(1765, 10, 27));
    savedMain.addRelease(release1);
    release2 = new Release();
    release2.setLabel("Release2");
    release2.setReleaseDescription("Description Release2");
    release2.setReleaseNotes("Notes Release2");
    release2.setProjectRelease(project1);
    release2.setReleaseDate(LocalDate.of(3602, 1, 5));
    savedMain.addRelease(release2);
    release3 = new Release();
    release3.setLabel("Release3");
    release3.setReleaseDescription("Description Release3");
    release3.setReleaseNotes("Notes Release3");
    release3.setProjectRelease(project1);
    release3.setReleaseDate(LocalDate.of(1765, 10, 27));
    savedMain.addRelease(release3);
  }

  public void createProjectsWithDependency() {
    createVanillaPeople();
    createTeamWithDependency();
    project1 = new Project();
    project1.setLabel("Project1");
    project1.setProjectName("Name Project1");
    project1.setProjectDescription("Description Project1");
    project1.getAllocatedTeams().add(new AgileHistory(team1,
                                                      LocalDate.of(2010, Month.APRIL, 3),
                                                      LocalDate.of(2010, Month.APRIL, 3)));
    savedMain.addProject(project1);
    project2 = new Project();
    project2.setLabel("Project2");
    project2.setProjectName("Name Project2");
    project2.setProjectDescription("Description Project2");
    project2.getAllocatedTeams().add(new AgileHistory(team2,
                                                      LocalDate.of(2010, Month.APRIL, 3),
                                                      LocalDate.of(2014, Month.DECEMBER, 30)));
    savedMain.addProject(project2);
    project3 = new Project();
    project3.setLabel("Project3");
    project3.setProjectName("Name Project3");
    project3.setProjectDescription("Description Project3");
    project3.getAllocatedTeams().add(new AgileHistory(team3,
                                                      LocalDate.of(2012, Month.APRIL, 5),
                                                      null));
    savedMain.addProject(project3);
  }

  public void createStoriesWithACs() {
    createVanillaPeople();
    acs = FXCollections.observableArrayList();
    acs.add("ac1\nrunboy runnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
    acs.add("ac2 aiyaaaa why like that onnnneeeee???");
    acs.add("ac3 WHY YOU WASTE DA MONEYYYYY?? \n\n B+?! AGAIN?! \n I no longer have a son");
    story1 = new Story("Story1", "Starter Story", "Huehuehuehue", person1, acs);
    savedMain.addStory(story1);

    acs2 = FXCollections.observableArrayList();
    acs2.add("Holy moley");
    acs2.add("its a bird");
    acs2.add("its a burrito");
    story2 = new Story();
    story2.setLabel("Story2");
    story2.setStoryName("Moar Story");
    story2.setCreator(person2);
    story2.setAcceptanceCriteria(acs2);
    savedMain.addStory(story2);

    story3 = new Story();
    story3.setLabel("Story3");
    story3.setDescription("They story-ening is now");
    story3.setCreator(person1);
    savedMain.addStory(story3);
  }

  public void createStories() {
    createVanillaPeople();
    story1 = new Story("Story1", "Starter Story", "Huehuehuehue", person1);
    savedMain.addStory(story1);

    story2 = new Story();
    story2.setLabel("Story2");
    story2.setStoryName("Moar Story");
    story2.setCreator(person2);
    savedMain.addStory(story2);

    story3 = new Story();
    story3.setLabel("Story3");
    story3.setDescription("They story-ening is now");
    story3.setCreator(person1);
    savedMain.addStory(story3);
  }

  /**
   *   --3
   *  / /
   * | 2
   * |/
   * 1
   */
  public void createStoriesWithDependencies() {
    createVanillaPeople();
    story1 = new Story("Story1", "Starter Story", "Huehuehuehue", person1);
    savedMain.addStory(story1);

    story2 = new Story();
    story2.setLabel("Story2");
    story2.setStoryName("Moar Story");
    story2.setCreator(person2);
    story2.addDependency(story1);
    savedMain.addStory(story2);

    story3 = new Story();
    story3.setLabel("Story3");
    story3.setDescription("They story-ening is now");
    story3.setCreator(person1);
    story3.addDependency(story1);
    story3.addDependency(story2);
    savedMain.addStory(story3);
  }

  public void createStoriesMarkedAsReady() {
    createVanillaPeople();

    acs = FXCollections.observableArrayList();
    acs.add("ac1\nrunboy runnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
    acs.add("ac2 aiyaaaa why like that onnnneeeee???");
    acs.add("ac3 WHY YOU WASTE DA MONEYYYYY?? \n\n B+?! AGAIN?! \n I no longer have a son");

    story1 = new Story("Story1", "Starter Story", "Huehuehuehue", person1);
    story1.setAcceptanceCriteria(acs);
    story1.setStoryState(true);
    savedMain.addStory(story1);

    story2 = new Story();
    story2.setLabel("Story2");
    story2.setStoryName("Moar Story");
    story2.setCreator(person2);
    story2.setAcceptanceCriteria(acs);
    story2.setStoryState(true);
    savedMain.addStory(story2);

    story3 = new Story();
    story3.setLabel("Story3");
    story3.setDescription("The story is not ending now");
    story3.setCreator(person1);
    story3.setAcceptanceCriteria(acs);
    story3.setStoryState(true);
    savedMain.addStory(story3);
  }

  public void createStoryWithEffort() {

    createVanillaPeople();

    List<Person> peeps = new ArrayList<>();
    peeps.add(person2);

    Task task = new Task("Woomandu","what chu think fool?!?!", 1, Status.DONE, peeps);

    storyWithLoggedEffort = new Story();
    storyWithLoggedEffort.setLabel("WOOMANDU");
    storyWithLoggedEffort.setDescription("oh my god he is the big boy");
    storyWithLoggedEffort.setCreator(person1);
    storyWithLoggedEffort.addTask(task);

    Effort effort = new Effort(person2, 200,"Iwork hard", LocalDateTime.of(2015,1,1,12,10));

    task.addEffort(effort);

    savedMain.addStory(storyWithLoggedEffort);
  }

  public void createBacklogs() {
    createStories();
    backlog1 = new Backlog("Backlog1", "Starter Backlog", "Huehuehuehue", person1, null);
    backlog1.addStory(story1);
    savedMain.addBacklog(backlog1);

    backlog2 = new Backlog("Backlog2", "Another Backlog", "Huehuehuehuehue", person2, null);
    backlog2.addStory(story2);
    savedMain.addBacklog(backlog2);

    backlog3 = new Backlog("Backlog3", "Another another Backlog", "DescriptionBack", person3, null);
    backlog3.addStory(story3);
    savedMain.addBacklog(backlog3);
  }

  public void allocateTeams() {
    createVanillaProjects();
    createTeamWithDependency();
    teamHistory1 = new AgileHistory(team1, LocalDate.of(2000, 3, 4), LocalDate.of(2000, 3, 5));
    project1.addTeam(teamHistory1);
    teamHistory2 = new AgileHistory(team3, LocalDate.of(1860, 5, 12), LocalDate.of(1861, 2, 17));
    teamHistory3 = new AgileHistory(team3, LocalDate.of(1861, 2, 18), LocalDate.of(1861, 2, 19));
    project2.addTeam(teamHistory2);
    project2.addTeam(teamHistory3);
  }

  @Test
  public void vanillaProjectLoadTest() {
    createVanillaProjects();
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "VanillaProjectSave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    assertEquals(loadedMain.getProjects().get(0).getLabel(), "Project1");
    assertEquals(loadedMain.getProjects().get(0).getProjectName(), "No Project Description");
    assertEquals(loadedMain.getProjects().get(1).getLabel(), "Project2");
    assertEquals(loadedMain.getProjects().get(1).getProjectName(), "Has Description");
    assertEquals(loadedMain.getProjects().get(1).getProjectDescription(), "Proj Descroption");
    assertEquals(loadedMain.getProjects().get(2).getLabel(), "Project3");
    assertEquals(loadedMain.getProjects().get(2).getProjectName(), "Back to no description");

    if (!file.delete()) {
      fail();
    }
  }

  @Test
  public void vanillaPeopleTest() {
    createVanillaPeople();
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "VanillaPersonSave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    assertEquals(loadedMain.getPeople().get(0).getLabel(), "Person1");
    assertEquals(loadedMain.getPeople().get(0).getFirstName(), "Only first name");
    assertEquals(loadedMain.getPeople().get(1).getLabel(), "Person2");
    assertEquals(loadedMain.getPeople().get(1).getFirstName(), "Both first");
    assertEquals(loadedMain.getPeople().get(1).getLastName(), "And last");
    assertEquals(loadedMain.getPeople().get(2).getLabel(), "Person3");
    assertEquals(loadedMain.getPeople().get(2).getLastName(), "Only last name");

    if (!file.delete()) {
      fail();
    }
  }

  @Test
  public void dependantSkillTest() {
    createSkillsWithDependency();
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "DependantSkillSave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    // Check skills loaded properly
    skill1 = loadedMain.getSkills().get(0);
    assertEquals(skill1.getLabel(), "Skill1");
    assertEquals(skill1.getSkillDescription(), "Skill description 1");
    skill2 = loadedMain.getSkills().get(1);
    assertEquals(skill2.getLabel(), "Skill2");
    skill3 = loadedMain.getSkills().get(2);
    assertEquals(skill3.getLabel(), "Skill3");
    assertEquals(skill3.getSkillDescription(), "Skill description 3");


    person1 = loadedMain.getPeople().get(0);
    assertEquals(person1.getSkillSet().get(0), skill2);
    person2 = loadedMain.getPeople().get(1);
    assertEquals(person2.getSkillSet().get(0), skill1);
    assertEquals(person2.getSkillSet().get(1), skill3);
    person2 = loadedMain.getPeople().get(2);
    assertEquals(person3.getSkillSet().get(0), skill3);
    assertEquals(person3.getSkillSet().get(1), skill2);
    assertEquals(person3.getSkillSet().get(2), skill1);

    if (!file.delete()) {
      fail();
    }
  }

  @Test
  public void dependantTeamTest() {
    createTeamWithDependency();
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "DependantTeamSave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    person1 = loadedMain.getPeople().get(0);
    person2 = loadedMain.getPeople().get(1);
    person3 = loadedMain.getPeople().get(2);

    team1 = loadedMain.getTeams().get(0);
    assertEquals("Team1", team1.getLabel());
    assertEquals("Description Team1", team1.getTeamDescription());
    assertEquals(person1, team1.getTeamMembers().get(0));
    team2 = loadedMain.getTeams().get(1);
    assertEquals("Team2", team2.getLabel());
    assertEquals(person3, team2.getTeamMembers().get(0));
    assertEquals(person2, team2.getTeamMembers().get(1));
    team3 = loadedMain.getTeams().get(2);
    assertEquals("Team3", team3.getLabel());
    assertEquals("Description Team2", team3.getTeamDescription());

    if (!file.delete()) {
      fail();
    }
  }

  @Test
  public void dependantReleaseTest() {
    createReleaseWithDependency();
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "DependantReleaseSave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    project1 = loadedMain.getProjects().get(0);
    project2 = loadedMain.getProjects().get(1);
    project3 = loadedMain.getProjects().get(2);

    release1 = loadedMain.getReleases().get(0);
    assertEquals("Release1", release1.getLabel());
    assertEquals("Description Release1", release1.getReleaseDescription());
    assertEquals("Notes Release1", release1.getReleaseNotes());
    assertEquals(project3, release1.getProjectRelease());
    assertEquals(LocalDate.of(1765, 10, 27).toString(),
                 release1.getReleaseDate().toString());
    release2 = loadedMain.getReleases().get(1);
    assertEquals("Release2", release2.getLabel());
    assertEquals("Description Release2", release2.getReleaseDescription());
    assertEquals("Notes Release2", release2.getReleaseNotes());
    assertEquals(project1, release2.getProjectRelease());
    assertEquals(LocalDate.of(3602, 1, 5).toString(),
                 release2.getReleaseDate().toString());
    release3 = loadedMain.getReleases().get(2);
    assertEquals("Release3", release3.getLabel());
    assertEquals("Description Release3", release3.getReleaseDescription());
    assertEquals("Notes Release3", release3.getReleaseNotes());
    assertEquals(project1, release3.getProjectRelease());
    assertEquals(LocalDate.of(1765, 10, 27).toString(),
                 release3.getReleaseDate().toString());

    if (!file.delete()) {
      fail();
    }
  }

  @Test
  public void dependantProjectTest() {
    createProjectsWithDependency();
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "DependantProjectSave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    team1 = loadedMain.getTeams().get(0);
    team2 = loadedMain.getTeams().get(1);
    team3 = loadedMain.getTeams().get(2);

    project1 = loadedMain.getProjects().get(0);
    assertEquals("Project1", project1.getLabel());
    assertEquals("Name Project1", project1.getProjectName());
    assertEquals("Description Project1", project1.getProjectDescription());
    assertEquals(team1, project1.getAllocatedTeams().get(0).getAgileItem());
    assertEquals(LocalDate.of(2010, Month.APRIL, 3), project1.getAllocatedTeams().get(
        0).getStartDate());
    assertEquals(LocalDate.of(2010, Month.APRIL, 3),
                 project1.getAllocatedTeams().get(0).getEndDate());

    project2 = loadedMain.getProjects().get(1);
    assertEquals("Project2", project2.getLabel());
    assertEquals("Name Project2", project2.getProjectName());
    assertEquals("Description Project2", project2.getProjectDescription());
    assertEquals(team2, project2.getAllocatedTeams().get(0).getAgileItem());
    assertEquals(LocalDate.of(2010, Month.APRIL, 3), project2.getAllocatedTeams().get(0).getStartDate());
    assertEquals(LocalDate.of(2014, Month.DECEMBER, 30), project2.getAllocatedTeams().get(0).getEndDate());

    project3 = loadedMain.getProjects().get(2);
    assertEquals("Project3", project3.getLabel());
    assertEquals("Name Project3", project3.getProjectName());
    assertEquals("Description Project3", project3.getProjectDescription());
    assertEquals(team3, project3.getAllocatedTeams().get(0).getAgileItem());
    assertEquals(LocalDate.of(2012, Month.APRIL, 5), project3.getAllocatedTeams().get(0).getStartDate());
    assertNull(project3.getAllocatedTeams().get(0).getEndDate());

    if (!file.delete()) {
      fail();
    }
  }

  @Test
  public void testTeamAllocation() {
    allocateTeams();
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "DependantReleaseSave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    team1 = loadedMain.getTeams().get(0);
    team3 = loadedMain.getTeams().get(2);

    project1 = loadedMain.getProjects().get(0);
    assertEquals(team1, project1.getAllocatedTeams().get(0).getAgileItem());
    assertEquals(LocalDate.of(2000, 3, 4).toString(),
                 project1.getAllocatedTeams().get(0).getStartDate().toString());
    assertEquals(LocalDate.of(2000, 3, 5).toString(),
                 project1.getAllocatedTeams().get(0).getEndDate().toString());
    project2 = loadedMain.getProjects().get(1);
    assertEquals(team3, project2.getAllocatedTeams().get(0).getAgileItem());
    assertEquals(LocalDate.of(1860, 5, 12).toString(),
                 project2.getAllocatedTeams().get(0).getStartDate().toString());
    assertEquals(LocalDate.of(1861, 2, 17).toString(),
                 project2.getAllocatedTeams().get(0).getEndDate().toString());

    assertEquals(team3, project2.getAllocatedTeams().get(1).getAgileItem());
    assertEquals(LocalDate.of(1861, 2, 18).toString(),
                 project2.getAllocatedTeams().get(1).getStartDate().toString());
    assertEquals(LocalDate.of(1861, 2, 19).toString(),
                 project2.getAllocatedTeams().get(1).getEndDate().toString());

    if (!file.delete()) {
      fail();
    }
  }

  /**
   *Makes sure that if a header is blank for whatever reason, file still loads.
   */
  @Test
  public void testingBlankHeader() {
    Settings.organizationName = "";
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "BlankHeader.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    assertEquals("", Settings.organizationName);

    if (!file.delete()) {
      fail();
    }
  }

  /**
   * Makes sure that the organization name is loaded properly in the Settings fields
   */
  @Test
  public void testingFilledHeader() {
    String expectedName = "test";
    Settings.organizationName = expectedName;
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "FilledHeader.xml");
    saving.saveData(file);

    Settings.organizationName = "";
    loading = new Loading(loadedMain);
    loading.loadFile(file);

    assertEquals(expectedName, Settings.organizationName);

    if (!file.delete()) {
      fail();
    }
  }

  /**
   * Tests the loading of stories, makes sure saved and loaded field strings are the same,
   * and also checks that Person field concurrency is conserved.
   */
  @Test
  public void testingStoriesWithAcs() {
    createStoriesWithACs();
    //empty ac list:
    ObservableList<String> emptyacs = FXCollections.observableArrayList();
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "StorySave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    person1 = loadedMain.getPeople().get(0);
    person2 = loadedMain.getPeople().get(1);

    story1 = loadedMain.getStories().get(0);
    assertEquals("Story1", story1.getLabel());
    assertEquals("Starter Story", story1.getStoryName());
    assertEquals("Huehuehuehue", story1.getDescription());
    assertSame(person1, story1.getCreator());
    assertEquals(acs.get(0), story1.getAcceptanceCriteria().get(0));
    assertEquals(acs.get(1), story1.getAcceptanceCriteria().get(1));
    assertEquals(acs.get(2), story1.getAcceptanceCriteria().get(2));
    assertFalse(story1.getStoryState());

    story2 = loadedMain.getStories().get(1);
    assertEquals("Story2", story2.getLabel());
    assertEquals("Moar Story", story2.getStoryName());
    assertSame(person2, story2.getCreator());
    assertEquals(acs2, story2.getAcceptanceCriteria());
    assertFalse(story2.getStoryState());

    story3 = loadedMain.getStories().get(2);
    assertEquals("Story3", story3.getLabel());
    assertEquals("They story-ening is now", story3.getDescription());
    assertSame(person1, story3.getCreator());
    assertEquals(emptyacs, story3.getAcceptanceCriteria());
    assertFalse(story3.getStoryState());

    if (!file.delete()) {
      fail();
    }
  }

  /**
   * Tests the loading of stories, makes sure saved and loaded field strings are the same,
   * and also checks that Person field concurrency is conserved.
   */
  @Test
  public void testingStories() {
    createStories();
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "StorySave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    person1 = loadedMain.getPeople().get(0);
    person2 = loadedMain.getPeople().get(1);

    story1 = loadedMain.getStories().get(0);
    assertEquals("Story1", story1.getLabel());
    assertEquals("Starter Story", story1.getStoryName());
    assertEquals("Huehuehuehue", story1.getDescription());
    assertSame(person1, story1.getCreator());
    assertFalse(story1.getStoryState());

    story2 = loadedMain.getStories().get(1);
    assertEquals("Story2", story2.getLabel());
    assertEquals("Moar Story", story2.getStoryName());
    assertSame(person2, story2.getCreator());
    assertFalse(story2.getStoryState());

    story3 = loadedMain.getStories().get(2);
    assertEquals("Story3", story3.getLabel());
    assertEquals("They story-ening is now", story3.getDescription());
    assertSame(person1, story3.getCreator());
    assertFalse(story3.getStoryState());

    if (!file.delete()) {
      fail();
    }
  }

  @Test
  public void testingStoriesMarkedAsReady() {
    createStoriesMarkedAsReady();

    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "StorySave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);
    person1 = loadedMain.getPeople().get(0);
    person2 = loadedMain.getPeople().get(1);

    story1 = loadedMain.getStories().get(0);
    assertEquals("Story1", story1.getLabel());
    assertEquals("Starter Story", story1.getStoryName());
    assertEquals("Huehuehuehue", story1.getDescription());
    assertSame(person1, story1.getCreator());
    assertEquals(acs.get(0), story1.getAcceptanceCriteria().get(0));
    assertEquals(acs.get(1), story1.getAcceptanceCriteria().get(1));
    assertEquals(acs.get(2), story1.getAcceptanceCriteria().get(2));
    assertTrue(story1.getStoryState());

    story2 = loadedMain.getStories().get(1);
    assertEquals("Story2", story2.getLabel());
    assertEquals("Moar Story", story2.getStoryName());
    assertSame(person2, story2.getCreator());
    assertTrue(story2.getStoryState());

    story3 = loadedMain.getStories().get(2);
    assertEquals("Story3", story3.getLabel());
    assertEquals("The story is not ending now", story3.getDescription());
    assertSame(person1, story3.getCreator());
    assertTrue(story3.getStoryState());

    if (!file.delete()) {
      fail();
    }
  }

  @Test
  public void testBacklogs() {
    createBacklogs();
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator + "BacklogSave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    person1 = loadedMain.getPeople().get(0);
    person2 = loadedMain.getPeople().get(1);
    person3 = loadedMain.getPeople().get(2);

    story1 = loadedMain.getStories().get(0);
    story2 = loadedMain.getStories().get(1);
    story3 = loadedMain.getStories().get(2);

    backlog1 = loadedMain.getBacklogs().get(0);
    assertEquals("Backlog1", backlog1.getLabel());
    assertEquals("Starter Backlog", backlog1.getBacklogName());
    assertEquals("Huehuehuehue", backlog1.getBacklogDescription());
    assertSame(person1, backlog1.getProductOwner());
    assertSame(story1, backlog1.getStories().get(0));

    backlog2 = loadedMain.getBacklogs().get(1);
    assertEquals("Backlog2", backlog2.getLabel());
    assertEquals("Another Backlog", backlog2.getBacklogName());
    assertEquals("Huehuehuehuehue", backlog2.getBacklogDescription());
    assertSame(person2, backlog2.getProductOwner());
    assertSame(story2, backlog2.getStories().get(0));

    backlog3 = loadedMain.getBacklogs().get(2);
    assertEquals("Backlog3", backlog3.getLabel());
    assertEquals("Another another Backlog", backlog3.getBacklogName());
    assertEquals("DescriptionBack", backlog3.getBacklogDescription());
    assertSame(person3, backlog3.getProductOwner());
    assertSame(story3, backlog3.getStories().get(0));

    if (!file.delete()) {
      fail();
    }
  }

  public void createEstimates() {
    createVanillaPeople();
    createStories();
    List<String> fiboEsts = Arrays.asList("not set", "1", "2", "3", "5", "8", "13", "epic");
    estimate1 = new Estimate("Fibonacci", fiboEsts);
    List<String> dinoEsts = Arrays.asList("not set", "dino egg", "dino baby", "dino toddler",
                                          "kiddy dino", "dino teen", "dino", "elder dino");
    estimate2 = new Estimate("dinos", dinoEsts);

    backlog1 = new Backlog("Backlog1", "Starter Backlog", "Huehuehuehue", person1, null);
    backlog1.setEstimate(estimate1);
    backlog1.addStory(story1, 0);
    savedMain.addBacklog(backlog1);

    backlog2 = new Backlog("Backlog2", "Another Backlog", "Huehuehuehuehue", person2, null);
    backlog2.setEstimate(estimate2);
    backlog2.addStory(story2, 4);
    savedMain.addBacklog(backlog2);

    backlog3 = new Backlog("Backlog3", "Another another Backlog", "DescriptionBack", person3, null);
    backlog3.setEstimate(estimate1);
    backlog3.addStory(story3, 6);
    savedMain.addBacklog(backlog3);
  }

  @Test
  public void testStoryAllocation() {
    createEstimates();
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator + "BacklogSave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    List<Backlog> loadedBacklogs = loadedMain.getBacklogs();
    // Making sure the saved values are the intended integers.
    assertEquals(backlog1.getSizes().get(story1).intValue(), 0);
    assertEquals(backlog2.getSizes().get(story2).intValue(), 4);
    assertEquals(backlog3.getSizes().get(story3).intValue(), 6);

    // Make sure the loaded integers are the correct ones.
    story1 = loadedMain.getStories().get(0);
    story2 = loadedMain.getStories().get(1);
    story3 = loadedMain.getStories().get(2);
    assertEquals(loadedBacklogs.get(0).getSizes().get(story1).intValue(), 0);
    assertEquals(loadedBacklogs.get(1).getSizes().get(story2).intValue(), 4);
    assertEquals(loadedBacklogs.get(2).getSizes().get(story3).intValue(), 6);

    if (!file.delete()) {
      fail();
    }
  }

  /**
   * Since we're only going to test backlog allocation, dont bother with anything else.
   */
  private void createProjectsWithBacklogs() {
    createVanillaProjects();
    createBacklogs();
    project1.setBacklog(backlog1);
    project2.setBacklog(backlog3);
    project3.setBacklog(backlog2);
  }

  /**
   * Testing proper Backlog is stored after loading inside Project
   */
  @Test
  public void testBacklogInProjectLoadingVersion() {
    createProjectsWithBacklogs();
    saveAndLoad();

    List<Project> projects = loadedMain.getProjects();
    List<Backlog> backlogs = loadedMain.getBacklogs();
    // Testing syncing
    assertSame(projects.get(0).getBacklog(), backlogs.get(0));
    assertSame(projects.get(1).getBacklog(), backlogs.get(2));
    assertSame(projects.get(2).getBacklog(), backlogs.get(1));

    assertEquals(backlog1.getLabel(), backlogs.get(0).getLabel());
    assertEquals(backlog2.getLabel(), backlogs.get(1).getLabel());
    assertEquals(backlog3.getLabel(), backlogs.get(2).getLabel());
  }

  @Test
  public void testingStoriesWithDependencies() {
    createStoriesWithDependencies();
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "StorySave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    person1 = loadedMain.getPeople().get(0);
    person2 = loadedMain.getPeople().get(1);

    story1 = loadedMain.getStories().get(0);
    assertEquals("Story1", story1.getLabel());
    assertEquals("Starter Story", story1.getStoryName());
    assertEquals("Huehuehuehue", story1.getDescription());
    assertSame(person1, story1.getCreator());
    assertTrue(story1.getDependencies().isEmpty());

    story2 = loadedMain.getStories().get(1);
    assertEquals("Story2", story2.getLabel());
    assertEquals("Moar Story", story2.getStoryName());
    assertSame(person2, story2.getCreator());
    assertTrue(!story2.getDependencies().isEmpty());
    assertSame(story2.getDependencies().get(0), story1);

    story3 = loadedMain.getStories().get(2);
    assertEquals("Story3", story3.getLabel());
    assertEquals("They story-ening is now", story3.getDescription());
    assertSame(person1, story3.getCreator());
    assertTrue(!story3.getDependencies().isEmpty());
    assertSame(story3.getDependencies().get(0), story1);
    assertSame(story3.getDependencies().get(1), story2);

    if (!file.delete()) {
      fail();
    }
  }

  private void createStoriesWithTasks() {
    createStories();

    task1 = new Task();
    task1.setLabel("Task1");
    task1.setTaskDescription("Descriptioso");
    task1.setStatus(Status.NOT_STARTED);
    task1.addAllTaskPeople(Arrays.asList(person1, person2));
//    task1.updateSpentEffort(person2, 34);
    task1.setTaskEstimation(250);

    task2 = new Task();
    task2.setLabel("Task2");
    task2.setTaskDescription("Descriptiosossss");
    task2.setStatus(Status.DONE);
    task2.addAllTaskPeople(Arrays.asList(person2, person3));
//    task2.updateSpentEffort(person2, 44);
    task2.setTaskEstimation(251);

    task3 = new Task();
    task3.setLabel("Task3");
    task3.setTaskDescription("Nope");
    task3.setStatus(Status.IN_PROGRESS);
    task3.addAllTaskPeople(Arrays.asList(person3, person1));
//    task3.updateSpentEffort(person2, 60);
    task3.setTaskEstimation(253);

    story1.addAllTasks(Arrays.asList(task1, task2));
    story2.addTask(task3);
  }

  @Test
  public void testingStoriesTaskLoading() {
    createStoriesWithTasks();
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "StorySave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    Story storyOne = new Story();
    Story storyTwo = new Story();
    List<Story> stories = loadedMain.getStories();
    for (Story story : stories) {
      if (story.getLabel().equals("Story1")) {
        storyOne = story;
      }
      if (story.getLabel().equals("Story2")) {
        storyTwo = story;
      }
    }

    assertEquals(story1, storyOne);
    assertEquals(story2, storyTwo);

    assertSame(loadedMain.getPeople().get(0),
               storyOne.getTasks().get(0).getTaskPeople().get(0));
  }

  @Test
  public void testStoryImpedimentsLoading() {
    createStories();
    story1.setImpediments("Impedi1");
    story2.setImpediments("Impeedi, dos");
    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "StorySave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);

    assertEquals("Impedi1", loadedMain.getStories().get(0).getImpediments());
    assertEquals("Impeedi, dos", loadedMain.getStories().get(1).getImpediments());
  }

  @Test
  public void testEffort(){
    createStoryWithEffort();


    saving = new Saving(savedMain);
    File file = new File(System.getProperty("user.dir")
                         + File.separator
                         + "StorySave.xml");
    saving.saveData(file);

    loading = new Loading(loadedMain);
    loading.loadFile(file);


    Story story = loadedMain.getStories().get(0);
    Task tTask = story.getTasks().get(0);
    Effort effy = tTask.getEfforts().get(0);

    assertEquals(200, effy.getSpentEffort());
    assertEquals("Iwork hard",effy.getComments());
    assertEquals(LocalDateTime.of(2015,1,1,12,10),effy.getDateTime());
    assertEquals(person2,effy.getWorker());
  }
}
