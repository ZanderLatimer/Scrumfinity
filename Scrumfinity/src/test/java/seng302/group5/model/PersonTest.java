package seng302.group5.model;

import org.junit.Before;
import org.junit.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static org.junit.Assert.*;

/**
 * Created by Shingy on 3/04/2015.
 */
public class PersonTest {

  private String personLabel;
  private String firstName;
  private String lastName;
  private ObservableList<Skill> skillSet = FXCollections.observableArrayList();

  private Person person;
  private Team team;

  @Before
  public void setUp() throws Exception {
    //create the person to be used for testing
    personLabel = "ssc55";
    firstName = "Su-Shing";
    lastName = "Chen";
    skillSet.addAll(new Skill("C", "C skill"));
    skillSet.add(new Skill("Java", "java Skill"));
    person = new Person(personLabel, firstName, lastName, skillSet);

    //create the team to be used for testing
    team = new Team();
    team.setLabel("Team1");
    team.setTeamDescription("For the tests");
  }

  @Test
  public void testAssignToTeam() throws Exception {
    assertNotNull(person);  //make sure that the person object exists.

    assertNull(person.getTeam()); //check if the person has a team (should be null).

    person.assignToTeam(team);

    assertEquals(team, person.getTeam()); //check if both the person assigned team
                                          //and the test team are the same.
  }

  @Test
  public void testRemoveFromTeam() throws Exception {
    assertNotNull(person);  //make sure that the person object exists.

    assertNull(person.getTeam()); //check if the person has a team (should be null).

    person.assignToTeam(team);  //assign the person to a team.

    assertEquals(team, person.getTeam()); //check that the person is in a team and it is the correct
                                          //one.

    person.removeFromTeam();

    assertNull(person.getTeam()); //check that the persons team is now null as they should have no
                                  //team.
  }

  @Test
  public void testIsInTeam() throws Exception {
    assertNotNull(person);  //make sure that the person object exists
    assertTrue(person.isInTeam() == false);  //should be false as person should not be in a team yet

    person.assignToTeam(team);

    assertTrue(person.isInTeam());  //should be true as the person is now in a team
  }

  @Test
  public void testToString() throws Exception {
    String result = person.getLabel();
    assertEquals(personLabel, result);
  }
}
