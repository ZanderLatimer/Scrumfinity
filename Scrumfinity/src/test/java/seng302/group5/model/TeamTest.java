package seng302.group5.model;

import org.junit.Before;
import org.junit.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static org.junit.Assert.*;

/**
 * Created by Shingy on 3/04/2015.
 */
public class TeamTest {

  private String teamLabel;
  private String teamDescription;
  private ObservableList<Person> teamMembers = FXCollections.observableArrayList();

  private Team team;

  @Before
  public void setUp() throws Exception {
    //creating the team to be tested
    teamLabel = "TheTeam";
    teamDescription = "This is the team to end all teams";
    team = new Team(teamLabel,teamMembers,teamDescription);  //has no team members
  }

  @Test
  public void testToString() throws Exception {
    assertEquals(team.toString(), teamLabel);
  }
}
