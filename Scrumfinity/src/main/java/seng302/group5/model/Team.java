package seng302.group5.model;

import java.util.Map;
import java.util.IdentityHashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Basic model of a Team.
 *
 * Created by Zander on 24/03/2015.
 */
public class Team implements AgileItem, Comparable<Team> {

  private String label;
  private String teamDescription;

  private ObservableList<Person> teamMembers = FXCollections.observableArrayList();
  private Map<Person, Role> membersRole;

  /**
   * Default constructor.
   */
  public Team() {
    this.label = "";
    this.teamDescription = "";
    this.membersRole = new IdentityHashMap<>();
  }

  public Team(String label, String teamDescription) {
    this.label = label;
    this.teamDescription = teamDescription;
    this.membersRole = new IdentityHashMap<>();
  }

  /**
   * Team constructor.
   *
   * @param label           Unique, non-null team label.
   * @param teamMembers     List of people in the team.
   * @param teamDescription Description of the team.
   */
  public Team(String label, ObservableList<Person> teamMembers, String teamDescription) {
    this.label = label;
    this.teamMembers = teamMembers;
    this.teamDescription = teamDescription;
    this.membersRole = new IdentityHashMap<>();
    for (Person member : this.teamMembers) {
      this.membersRole.put(member, null);
    }
  }


  /**
   * Constructor to create a clone of an existing team
   *
   * @param clone Person to clone
   */
  public Team(Team clone) {
    this.label = clone.getLabel();
    this.teamDescription = clone.getTeamDescription();
    this.teamMembers.clear();
    this.teamMembers.addAll(clone.getTeamMembers());
    this.membersRole = new IdentityHashMap<>();
    for (Person member : this.teamMembers) {
      this.membersRole.put(member, clone.getMembersRole().get(member));
    }
  }

  public Map<Person, Role> getMembersRole() {
    return membersRole;
  }

  public void setMembersRole(Map<Person, Role> membersRole) {
    this.membersRole = membersRole;
  }

  @Override
  public String getLabel() {
    return this.label;
  }

  @Override
  public void setLabel(String label) {
    this.label = label;
  }

  public String getTeamDescription() {
    return this.teamDescription;
  }

  public void setTeamDescription(String teamDescription) {
    this.teamDescription = teamDescription;
  }

  public ObservableList<Person> getTeamMembers() {
    return teamMembers;
  }

  public void setTeamMembers(ObservableList<Person> teamMembers) {
    this.teamMembers = teamMembers;
  }

  /**
   * Add a Person to the Team as a team member and assign a role
   *
   * @param person The team member to add
   * @param role   The team member's role
   */
  public void addTeamMember(Person person, Role role) {
    this.teamMembers.add(person);
    this.membersRole.put(person, role);
  }

  /**
   * Add a Person to the Team as a team member when a role is not specified
   *
   * @param person The team member to add
   */
  public void addTeamMember(Person person) {
    this.teamMembers.add(person);
    this.membersRole.put(person, null);
  }

  /**
   * Remove a team member from the team
   *
   * @param person The team member to remove
   */
  public void removeTeamMember(Person person) {
    this.teamMembers.remove(person);
    this.membersRole.remove(person);
  }

  /**
   * Copies the input team's fields into current object.
   *
   * @param agileItem Team who's fields are to be copied
   */
  @Override
  public void copyValues(AgileItem agileItem) {
    if (agileItem instanceof Team) {
      Team clone = (Team) agileItem;
      this.label = clone.getLabel();
      this.teamDescription = clone.getTeamDescription();
      this.teamMembers.clear();
      this.teamMembers.addAll(clone.getTeamMembers());
      this.membersRole.clear();
      for (Person member : this.teamMembers) {
        this.membersRole.put(member, clone.getMembersRole().get(member));
      }
    }
  }

  /**
   * Overrides to toString method with the label of team.
   *
   * @return Unique label of team.
   */
  @Override
  public String toString() {
    return label;
  }

  /**
   * Check if two team's ids are equal
   *
   * @param obj Object to compare to.
   * @return Whether team's ids are equal
   */
  @Override
  public boolean equals(Object obj) {
    boolean result = false;
    if (obj instanceof Team) {
      Team team = (Team) obj;
      result = this.label.equals(team.getLabel());
    }
    return result;
  }

  /**
   * Compares the team labels.
   *
   * @param o the team you wish to compare to.
   * @return return whether its greater or lesser.
   */
  @Override
  public int compareTo(Team o) {
    return this.label.toLowerCase().compareTo(o.label.toLowerCase());
  }
}
