package seng302.group5.model;

import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Basic model of a Person.
 *
 * Created by Zander on 17/03/2015.
 */
public class Person implements AgileItem, Comparable<Person> {

  private String label;
  private String firstName;
  private String lastName;

  private Boolean assignedToTeam = false;
  private Team team = null;

  private ObservableList<Skill> skillSet = FXCollections.observableArrayList();

  /**
   * Default constructor.
   */
  public Person() {
    label = "";
    firstName = "";
    lastName = "";

  }

  /**
   * Person constructor.
   *
   * @param label  Unique, non-null person label. Can't be greater than 8 characters.
   * @param firstName First name of person.
   * @param lastName  Last name of person.
   * @param skills    List of person's skills
   */
  public Person(String label, String firstName, String lastName, ObservableList<Skill> skills) {
    this.label = label;
    this.firstName = firstName;
    this.lastName = lastName;
    this.skillSet = skills;
  }

  /**
   * Constructor to create a clone of an existing person
   *
   * @param clone Person to clone
   */
  public Person(Person clone) {
    this.label = clone.getLabel();
    this.firstName = clone.getFirstName();
    this.lastName = clone.getLastName();
    this.skillSet.clear();
    this.skillSet.addAll(clone.getSkillSet());
    this.team = clone.getTeam();
    this.assignedToTeam = clone.isInTeam();
  }


  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public void setLabel(String label) {
    this.label = label;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public Team getTeam() {
    return team;
  }

  public String getTeamLabel() {
    return team.getLabel();
  }


  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public ObservableList<Skill> getSkillSet() {
    return skillSet;
  }

  public void setSkillSet(ObservableList<Skill> skillSet) {
    this.skillSet = skillSet;
  }

  /**
   * Assigns this person to a team.
   *
   * @param team Team object that person is assigned to.
   */
  public void assignToTeam(Team team) {
    this.assignedToTeam = true;
    this.team = team;
  }

  /**
   * Removes this person from a team.
   */
  public void removeFromTeam() {
    this.assignedToTeam = false;
    this.team = null;
  }

  /**
   * Checks if person is in a team.
   *
   * @return True, if person is in team. False, if not.
   */
  public Boolean isInTeam() {
    return assignedToTeam;
  }

  /**
   * Copies the input person's fields into current object.
   *
   * @param agileItem Person who's fields are to be copied
   */
  @Override
  public void copyValues(AgileItem agileItem) {
    if (agileItem instanceof Person) {
      Person clone = (Person) agileItem;
      this.label = clone.getLabel();
      this.firstName = clone.getFirstName();
      this.lastName = clone.getLastName();
      this.skillSet.clear();
      this.skillSet.addAll(clone.getSkillSet());
      this.team = clone.getTeam();
      this.assignedToTeam = clone.isInTeam();
    }
  }

  /**
   * Overrides to toString method with the label of person.
   *
   * @return Unique label of person.
   */
  @Override
  public String toString() {
    return label;
  }

  /**
   * Overrides default to just check if the project's labels are the same
   *
   * @param obj Object to compare to.
   * @return Whether the labels are equal or not
   */
  @Override
  public boolean equals(Object obj) {
    boolean result = false;
    if (obj instanceof Person) {
      Person person = (Person) obj;
      result = this.label.equals(person.getLabel());
    }
    return result;
  }

  /**
   * Compares the person labels
   * @param o the person you wish to compare to.
   * @return whether or not it is greater or lesser
   */
  @Override
  public int compareTo(Person o) {
    return this.label.toLowerCase().compareTo(o.label.toLowerCase());
  }

}
