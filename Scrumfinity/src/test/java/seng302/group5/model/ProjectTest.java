package seng302.group5.model;

import org.junit.Before;
import org.junit.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static org.junit.Assert.*;

/**
 * Created by Shingy on 3/04/2015.
 */
public class ProjectTest {

  private String projectLabel;
  private String projectName;
  private String projectDescription;
  private Project project;
  private ObservableList<Person> teamMembers;

  @Before
  public void setUp() throws Exception {
    projectLabel = "abcedef";
    projectName = "New Project";
    projectDescription = "This is a description. This field can be much longer than the others.";
    project = new Project(projectLabel, projectName, projectDescription);
    teamMembers  = FXCollections.observableArrayList();
  }

  @Test
  public void testAddTeam() throws Exception {
    assertTrue(project.getAllocatedTeams().isEmpty());

    AgileHistory team = new AgileHistory();

    project.addTeam(team);

    assertEquals(1, project.getAllocatedTeams().size());
    assertEquals(team, project.getAllocatedTeams().get(0));
  }

  @Test
  public void testRemoveTeam() throws Exception {
    assertTrue(project.getAllocatedTeams().isEmpty());

    AgileHistory team1 = new AgileHistory();

    project.addTeam(team1);
    assertEquals(1, project.getAllocatedTeams().size());
    assertEquals(team1, project.getAllocatedTeams().get(0));

    project.removeTeam(team1);
    assertTrue(project.getAllocatedTeams().isEmpty());
  }

  @Test
  public void testCopyValues() throws Exception {
    Backlog backlog = new Backlog();
    project.setBacklog(backlog);

    assertEquals(projectLabel, project.getLabel());
    assertEquals(projectName, project.getProjectName());
    assertEquals(projectDescription, project.getProjectDescription());
    assertEquals(backlog, project.getBacklog());

    Project clone = new Project();
    clone.copyValues(project);

    assertEquals(projectLabel, clone.getLabel());
    assertEquals(projectName, clone.getProjectName());
    assertEquals(projectDescription, clone.getProjectDescription());
    assertEquals(backlog, clone.getBacklog());
  }

  @Test
  public void testToString() {
    String result = project.toString();
    assertEquals(projectLabel, result);
  }
}
