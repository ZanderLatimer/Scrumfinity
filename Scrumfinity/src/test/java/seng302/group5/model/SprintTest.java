package seng302.group5.model;


import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alex Woo
 * Edited by Craig Barnard
 */
public class SprintTest {

  private String sprintGoal;
  private String sprintDescription;
  private String sprintFullName;
  private String sprintImpediments;
  private Team sprintTeam;
  private Backlog sprintBacklog;
  private Project sprintProject;
  private Release sprintRelease;

  private LocalDate sprintStart;
  private LocalDate sprintEnd;

  private List<Story> sprintStories;

  private Sprint sprint;
  private Sprint sprint1;
  private Sprint sprint2;
  private Sprint sprint3;

  private Story story;

  @Before
  public void setUp() throws Exception {
    List<String> fiboEsts = Arrays.asList("Not Set", "1", "2", "3", "5", "8", "13", "Epic");
    ObservableList<Skill> skillSet = FXCollections.observableArrayList();

    Estimate fibonacci = new Estimate("Fibonacci", fiboEsts);
    sprintGoal = "This is a goal!";
    sprintDescription = "Omg describe me like one of your french girls";
    sprintFullName = "The prisoner of alakazam baybays";
    sprintImpediments = "Something has gone horribly wrong!";
    sprintTeam = new Team("Team 1", "The First Team");
    Person person1 = new Person("Person 1", "Person", "1", skillSet);
    sprintBacklog = new Backlog("Backlog 1", "Backlog", "Description", person1, fibonacci);
    sprintProject = new Project("Project", "Project", "Description");
    sprintRelease = new Release();
    sprintStart = LocalDate.now();
    sprintEnd = LocalDate.MAX;
    sprintStories = new ArrayList<>();


    story = new Story("First Story", "Story one", "The First Story", person1);
    story.setImpediments("There is a problem!");

    sprint = new Sprint(sprintGoal, sprintFullName, sprintDescription, sprintBacklog, sprintProject,
                        sprintTeam, sprintRelease, sprintStart, sprintEnd, sprintStories);
    sprint.setSprintImpediments(sprintImpediments);

    sprint1 = new Sprint(sprintGoal, sprintFullName, sprintDescription, sprintBacklog,
                         sprintProject, sprintTeam, sprintRelease, sprintStart, sprintEnd,
                         sprintStories);
    sprint1.setSprintImpediments(sprintImpediments);


    String sprintGoal2 = "This is a goalololololol!";
    sprint2 = new Sprint(sprintGoal2, sprintFullName, sprintDescription, sprintBacklog,
                         sprintProject, sprintTeam, sprintRelease, sprintStart, sprintEnd,
                         sprintStories);

    sprint3 = new Sprint("sprint5", sprintFullName, sprintDescription, sprintBacklog,
                                sprintProject, sprintTeam, sprintRelease, LocalDate.MIN, sprintEnd,
                                sprintStories);
    sprint2.setSprintImpediments(sprintImpediments);
  }

  @Test
  public void testTemporalOrder() {

    ObservableList<Sprint> sprints = FXCollections.observableArrayList();
    sprints.add(sprint1);
    sprints.add(sprint2);
    sprints.add(sprint3);
    Comparator<Sprint> byDate = new Comparator<Sprint>() {
      @Override
      public int compare(Sprint o1, Sprint o2) {
        if (o1.getSprintStart().isAfter(o2.getSprintStart())) {
          return 1;
        } else return 0;
      }
      //
    };

    assertTrue(sprints.get(0).equals(sprint1));
    assertTrue(sprints.get(1).equals(sprint2));
    assertTrue(sprints.get(2).equals(sprint3));
    assertTrue(sprints.get(2).getSprintStart().isBefore(sprints.get(0).getSprintStart()));
    assertTrue(sprints.get(2).getSprintStart().isBefore(sprints.get(1).getSprintStart()));

    sprints = sprints.sorted(byDate);
    assertTrue(sprints.get(0).equals(sprint3));

    assertTrue(sprints.get(1).getSprintStart().equals(sprints.get(2).getSprintStart()));
    assertTrue(sprints.get(0).getSprintStart().isBefore(sprints.get(1).getSprintStart()));
    assertTrue(sprints.get(0).getSprintStart().isBefore(sprints.get(2).getSprintStart()));

  }

  @Test
  public void testToString() {
    assertEquals(sprint.toString(), sprint.getSprintGoal());
  }

  @Test
  public void equalsTrueCase() {
    assertTrue(sprint.equals(sprint1));
  }

  @Test
  public void testFalseCase() {
    assertTrue(!sprint.equals(sprint2));
  }

  @Test
  public void testAddStory() {
    assertTrue(sprint1.getSprintStories().isEmpty());
    sprint1.addStory(story);
    assertFalse(sprint1.getSprintStories().isEmpty());
    assertTrue(sprint1.getSprintStories().get(0).equals(story));
  }

  @Test
  public void testRemoveStory() {
    assertTrue(sprint1.getSprintStories().isEmpty());
    sprint1.addStory(story);
    assertFalse(sprint1.getSprintStories().isEmpty());
    assertTrue(sprint1.getSprintStories().contains(story));
    sprint1.removeStory(story);
    assertFalse(sprint1.getSprintStories().contains(story));
  }

  @Test
  public void testCopyValues() throws Exception {
    assertEquals(sprintGoal, sprint1.getLabel());
    assertEquals(sprintFullName, sprint1.getSprintFullName());
    assertEquals(sprintDescription, sprint1.getSprintDescription());
    assertEquals(sprintImpediments, sprint1.getSprintImpediments());
    assertEquals(sprintStart, sprint1.getSprintStart());
    assertEquals(sprintEnd, sprint1.getSprintEnd());
    assertEquals(sprintProject, sprint1.getSprintProject());
    assertEquals(sprintRelease, sprint1.getSprintRelease());
    assertEquals(sprintBacklog, sprint1.getSprintBacklog());
    assertEquals(sprintTeam, sprint1.getSprintTeam());
    assertEquals(sprintStories, sprint1.getSprintStories());

    Sprint clone = new Sprint();
    clone.copyValues(sprint1);

    assertEquals(sprintGoal, clone.getLabel());
    assertEquals(sprintFullName, clone.getSprintFullName());
    assertEquals(sprintDescription, clone.getSprintDescription());
    assertEquals(sprintImpediments, clone.getSprintImpediments());
    assertEquals(sprintStart, clone.getSprintStart());
    assertEquals(sprintEnd, clone.getSprintEnd());
    assertEquals(sprintProject, clone.getSprintProject());
    assertEquals(sprintRelease, clone.getSprintRelease());
    assertEquals(sprintBacklog, clone.getSprintBacklog());
    assertEquals(sprintTeam, clone.getSprintTeam());
    assertEquals(sprintStories, clone.getSprintStories());
  }

  @Test
  public void testHashCode() throws Exception {
    sprint.addStory(story);
    sprint1.addStory(story);
    Sprint sprint3 = new Sprint(sprint1);

    assertEquals(sprint.hashCode(), sprint1.hashCode());
    assertEquals(sprint.hashCode(), sprint3.hashCode());
  }
}

