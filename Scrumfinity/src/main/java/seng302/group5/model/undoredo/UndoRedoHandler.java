package seng302.group5.model.undoredo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import seng302.group5.Main;
import seng302.group5.model.AgileItem;
import seng302.group5.model.Backlog;
import seng302.group5.model.Effort;
import seng302.group5.model.Person;
import seng302.group5.model.Project;
import seng302.group5.model.Release;
import seng302.group5.model.Skill;
import seng302.group5.model.Sprint;
import seng302.group5.model.Story;
import seng302.group5.model.Task;
import seng302.group5.model.Taskable;
import seng302.group5.model.Team;

/**
 * Created by Michael on 3/17/2015.
 *
 * The handler for all undo/redo functionality
 */
public class UndoRedoHandler {

  private Main mainApp;
  private Stack<UndoRedo> undoStack;
  private Stack<UndoRedo> redoStack;
  /**
   * Constructor. Set the main app to communicate with and initialise stacks
   *
   * @param mainApp Main app to communicate with
   */
  public UndoRedoHandler(Main mainApp) {
    this.mainApp = mainApp;
    undoStack = new Stack<>();
    redoStack = new Stack<>();
  }

  /**
   * Peek at what is on top of the undo stack
   *
   * @return The top element of the undo stack, or null if it's empty
   */
  public UndoRedo peekUndoStack() {
    if (undoStack.isEmpty()) {
      return null;
    } else {
      return undoStack.peek();
    }
  }

  /**
   * Peek at what is top of the redo stack
   *
   * @return The top element of the redo stack, or null if it's empty
   */
  public UndoRedo peekRedoStack() {
    if (redoStack.isEmpty()) {
      return null;
    } else {
      return redoStack.peek();
    }
  }

  /**
   * Clear the undo and redo stacks
   */
  public void clearStacks() {
    undoStack.clear();
    redoStack.clear();
  }

  /**
   * Add a new action onto the undo stack
   *
   * @param undoRedo The action object
   */
  public void newAction(UndoRedo undoRedo) {
    undoStack.push(undoRedo);
    redoStack.clear();
  }

  /**
   * Generic undo function to undo the latest action
   *
   * @throws Exception Error message if data is invalid
   */
  public void undo() throws Exception {
    if (undoStack.isEmpty()) {
//      System.out.println("Nothing to undo");
      return;
    }
    // Rearrange stacks
    redoStack.push(undoStack.peek());
    UndoRedo undoRedo = undoStack.pop();

    // Handle the action
    handleUndoRedoObject(undoRedo, UndoOrRedo.UNDO);
  }

  /**
   * Generic redo function to redo the latest undone action
   *
   * @throws Exception Error message if data is invalid
   */
  public void redo() throws Exception {
    if (redoStack.isEmpty()) {
//      System.out.println("Nothing to redo");
      return;
    }
    // Rearrange stacks
    undoStack.push(redoStack.peek());
    UndoRedo undoRedo = redoStack.pop();

    // Handle the action
    handleUndoRedoObject(undoRedo, UndoOrRedo.REDO);
  }

  /**
   * Undo an action which was not stored on the stacks. Use with care.
   *
   * @param undoRedo The UndoRedo instance to undo.
   * @throws Exception Error message if data is invalid.
   */
  public void quickUndo(UndoRedo undoRedo) throws Exception {
    handleUndoRedoObject(undoRedo, UndoOrRedo.UNDO);
  }

  /**
   * Handle a undo or redo call
   *
   * @param undoRedo Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleUndoRedoObject(UndoRedo undoRedo,
                                    UndoOrRedo undoOrRedo) throws Exception {

    Action action = undoRedo.getAction();

    if (undoRedo instanceof UndoRedoObject) {
      UndoRedoObject undoRedoObject = (UndoRedoObject) undoRedo;

//    TODO: Put commented printlns into a JavaFX status bar
//    String undoOrRedoStr;
//    if (undoOrRedo == UndoOrRedo.UNDO) {
//      undoOrRedoStr = "undo";
//    } else {
//      undoOrRedoStr = "redo";
//    }

      switch (action) {
        case PROJECT_CREATE:
//        System.out.println(String.format("I am %sing a project creation", undoOrRedoStr)); // temp
          handleProjectCreate(undoRedoObject, undoOrRedo);
          break;

        case PROJECT_EDIT:
//        System.out.println(String.format("I am %sing a project edit", undoOrRedoStr));     // temp
          handleProjectEdit(undoRedoObject, undoOrRedo);
          break;

        case PROJECT_DELETE:
//        System.out.println(String.format("I am %sing a project deletion", undoOrRedoStr)); // temp
          handleProjectDelete(undoRedoObject, undoOrRedo);
          break;

        case PERSON_CREATE:
//        System.out.println(String.format("I am %sing a person creation", undoOrRedoStr)); // temp
          handlePersonCreate(undoRedoObject, undoOrRedo);
          break;

        case PERSON_EDIT:
//        System.out.println(String.format("I am %sing a person edit", undoOrRedoStr)); // temp
          handlePersonEdit(undoRedoObject, undoOrRedo);
          break;

        case PERSON_DELETE:
//        System.out.println(String.format("I am %sing a person deletion", undoOrRedoStr)); // temp
          handlePersonDelete(undoRedoObject, undoOrRedo);
          break;

        case SKILL_CREATE:
//        System.out.println(String.format("I am %sing a skill creation", undoOrRedoStr)); // temp
          handleSkillCreate(undoRedoObject, undoOrRedo);
          break;

        case SKILL_EDIT:
//        System.out.println(String.format("I am %sing a skill edit", undoOrRedoStr)); // temp
          handleSkillEdit(undoRedoObject, undoOrRedo);
          break;

        case SKILL_DELETE:
//        System.out.println(String.format("I am %sing a skill deletion", undoOrRedoStr));   // temp
          handleSkillDelete(undoRedoObject, undoOrRedo);
          break;

        case TEAM_CREATE:
//        System.out.println(String.format("I am %sing a team creation", undoOrRedoStr));   // temp
          handleTeamCreate(undoRedoObject, undoOrRedo);
          break;

        case TEAM_EDIT:
//        System.out.println(String.format("I am %sing a team edit", undoOrRedoStr));   // temp
          handleTeamEdit(undoRedoObject, undoOrRedo);
          break;

        case TEAM_DELETE:
//        System.out.println(String.format("I am %sing a team deletion", undoOrRedoStr));   // temp
          handleTeamDelete(undoRedoObject, undoOrRedo);
          break;

        case RELEASE_CREATE:
//        System.out.println(String.format("I am %sing a release creation", undoOrRedoStr)); // temp
          handleReleaseCreate(undoRedoObject, undoOrRedo);
          break;

        case RELEASE_EDIT:
//        System.out.println(String.format("I am %sing a release edit", undoOrRedoStr));   // temp
          handleReleaseEdit(undoRedoObject, undoOrRedo);
          break;

        case RELEASE_DELETE:
//        System.out.println(String.format("I am %sing a release delete", undoOrRedoStr));   // temp
          handleReleaseDelete(undoRedoObject, undoOrRedo);
          break;

        case STORY_CREATE:
//        System.out.println(String.format("I am %sing a story creation", undoOrRedoStr));   // temp
          handleStoryCreate(undoRedoObject, undoOrRedo);
          break;

        case STORY_EDIT:
//        System.out.println(String.format("I am %sing a story creation", undoOrRedoStr));   // temp
          handleStoryEdit(undoRedoObject, undoOrRedo);
          break;

        case STORY_DELETE:
//        System.out.println(String.format("I am %sing a story creation", undoOrRedoStr));   // temp
          handleStoryDelete(undoRedoObject, undoOrRedo);
          break;

        case BACKLOG_CREATE:
          handleBacklogCreate(undoRedoObject, undoOrRedo);
          break;

        case BACKLOG_EDIT:
          handleBacklogEdit(undoRedoObject, undoOrRedo);
          break;

        case BACKLOG_DELETE:
          handleBacklogDelete(undoRedoObject, undoOrRedo);
          break;

        case SPRINT_CREATE:
          handleSprintCreate(undoRedoObject, undoOrRedo);
          break;

        case SPRINT_EDIT:
          handleSprintEdit(undoRedoObject, undoOrRedo);
          break;

        case SPRINT_DELETE:
          handleSprintDelete(undoRedoObject, undoOrRedo);
          break;

        case TASK_CREATE:
          handleTaskCreate(undoRedoObject, undoOrRedo);
          break;

        case TASK_EDIT:
          handleTaskEdit(undoRedoObject, undoOrRedo);
          break;

        case TASK_DELETE:
          handleTaskDelete(undoRedoObject, undoOrRedo);
          break;

        case EFFORT_CREATE:
          handleEffortCreate(undoRedoObject, undoOrRedo);
          break;

        case EFFORT_EDIT:
          handleEffortEdit(undoRedoObject, undoOrRedo);
          break;

        case EFFORT_DELETE:
          handleEffortDelete(undoRedoObject, undoOrRedo);
          break;

        case UNDEFINED:
          throw new Exception("Unreadable UndoRedoObject");

        default:
          throw new Exception("Undo/Redo case is not handled");
      }
    } else if (undoRedo instanceof CompositeUndoRedo) {
      CompositeUndoRedo compositeUndoRedo = (CompositeUndoRedo) undoRedo;
      for (UndoRedo nestedUndoRedo : compositeUndoRedo.getUndoRedos()) {
        handleUndoRedoObject(nestedUndoRedo, undoOrRedo);
      }
    }
  }

  /**
   * Undo or redo a project creation
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleProjectCreate(UndoRedoObject undoRedoObject,
                                   UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the project
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo project creation - No variables");
    }

    // Get the project label which is currently in the list and the project to change
    Project projectToChange = (Project) undoRedoObject.getAgileItem();
    Project projectData = (Project) data.get(0);

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      mainApp.deleteProject(projectToChange);
    } else {
      projectToChange.copyValues(projectData);
      mainApp.addProject(projectToChange);
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a project edit
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleProjectEdit(UndoRedoObject undoRedoObject,
                                 UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the projects both before and after
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 2) {
      throw new Exception("Can't undo/redo project edit - Less than 2 variables");
    }

    // Get the project label which is currently in the list and the project to edit
    Project projectToChange = (Project) undoRedoObject.getAgileItem();
    Project projectData;

    if (undoOrRedo == UndoOrRedo.UNDO) {
      projectData = (Project) data.get(0);
    } else {
      projectData = (Project) data.get(1);
    }

    // Make the changes and refresh the list
    projectToChange.copyValues(projectData);
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a project deletion
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleProjectDelete(UndoRedoObject undoRedoObject,
                                   UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the project
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo project deletion - No variables");
    }

    // Get the project and label to undo/redo deletion of
    Project projectToChange = (Project) undoRedoObject.getAgileItem();
    Project projectData = (Project) data.get(0);

    // Get the projects releases and sprints
    List<AgileItem> additionalData = data.subList(1, data.size());

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      // Create the deleted project again
      projectToChange.copyValues(projectData);
      mainApp.addProject(projectToChange);
      // for each of its releases and sprints make them again and add them to the project
      for (AgileItem agileItem : additionalData) {
        if (agileItem instanceof Release) {
          Release release = (Release) agileItem;
          mainApp.addRelease(release);
        } else if (agileItem instanceof Sprint) {
          Sprint sprint = (Sprint) agileItem;
          mainApp.addSprint(sprint);
        }
      }
    } else {
      mainApp.deleteProject(projectToChange);
      for (AgileItem agileItem : additionalData) {
        if (agileItem instanceof Release) {
          Release release = (Release) agileItem;
          mainApp.deleteRelease(release);
        } else if (agileItem instanceof Sprint) {
          Sprint sprint = (Sprint) agileItem;
          mainApp.deleteSprint(sprint);
        }
      }
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a person creation
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handlePersonCreate(UndoRedoObject undoRedoObject,
                                  UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the person
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo person creation - No variables");
    }

    Person personToChange = (Person) undoRedoObject.getAgileItem();
    Person personData = (Person) data.get(0);

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      // Find the person in the list and ensure it exists so it can be deleted
      mainApp.deletePerson(personToChange);
    } else {
      personToChange.copyValues(personData);
      mainApp.addPerson(personToChange);
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a person edit
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handlePersonEdit(UndoRedoObject undoRedoObject,
                                UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the people both before and after
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 2) {
      throw new Exception("Can't undo/redo person edit - Less than 2 variables");
    }

    Person personToChange = (Person) undoRedoObject.getAgileItem();
    Person personData;

    if (undoOrRedo == UndoOrRedo.UNDO) {
      personData = (Person) data.get(0);
    } else {
      personData = (Person) data.get(1);
    }

    // Make the changes and refresh the list
    personToChange.copyValues(personData);
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a person deletion
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handlePersonDelete(UndoRedoObject undoRedoObject,
                                  UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the person
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo person deletion - No variables");
    }

    Person personToChange = (Person) undoRedoObject.getAgileItem();
    Person personData = (Person) data.get(0);

    // Get the person's team which is in the list
    Team team = personToChange.getTeam();

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      // Create the deleted person again
      personToChange.copyValues(personData);
      mainApp.addPerson(personToChange);
      if (team != null) {
        team.getTeamMembers().add(personToChange);
      }
    } else {
      if (team != null) {
        team.getTeamMembers().remove(personToChange);
      }
      mainApp.deletePerson(personToChange);
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a skill creation
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleSkillCreate(UndoRedoObject undoRedoObject,
                                 UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the skill
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo skill creation - No variables");
    }

    Skill skillToChange = (Skill) undoRedoObject.getAgileItem();
    Skill skillData = (Skill) data.get(0);

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      mainApp.deleteSkill(skillToChange);
    } else {
      skillToChange.copyValues(skillData);
      mainApp.addSkill(skillToChange);
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a skill edit
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleSkillEdit(UndoRedoObject undoRedoObject,
                               UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the skills both before and after
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 2) {
      throw new Exception("Can't undo/redo skill edit - Less than 2 variables");
    }

    Skill skillToChange = (Skill) undoRedoObject.getAgileItem();
    Skill skillData;

    if (undoOrRedo == UndoOrRedo.UNDO) {
      skillData = (Skill) data.get(0);
    } else {
      skillData = (Skill) data.get(1);
    }

    skillToChange.copyValues(skillData);
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a skill deletion
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleSkillDelete(UndoRedoObject undoRedoObject,
                                 UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the skill
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo skill deletion - No variables");
    }

    Skill skillToChange = (Skill) undoRedoObject.getAgileItem();
    Skill skillData = (Skill) data.get(0);

    // Get the skill users
    List<AgileItem> skillUsers = data.subList(1, data.size());

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      // Create the deleted skill again
      skillToChange.copyValues(skillData);
      mainApp.addSkill(skillToChange);
      // For each person stored, add the skill to their skill set
      for (AgileItem agileItem : skillUsers) {
        Person skillUser = (Person) agileItem;
        skillUser.getSkillSet().add(skillToChange);
      }
    } else {
      // For each person stored, remove the skill to their skill set
      for (AgileItem agileItem : skillUsers) {
        Person skillUser = (Person) agileItem;
        skillUser.getSkillSet().remove(skillToChange);
      }
      mainApp.deleteSkill(skillToChange);
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a team creation
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleTeamCreate(UndoRedoObject undoRedoObject,
                                UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the team
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo team creation - No variables");
    }

    // Get the team to change which is currently in the list and the target team data
    Team teamToChange = (Team) undoRedoObject.getAgileItem();
    Team teamData = (Team) data.get(0);

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      for (Person person : teamToChange.getTeamMembers()) {
        person.removeFromTeam();
      }
      mainApp.deleteTeam(teamToChange);
    } else {
      teamToChange.copyValues(teamData);
      mainApp.addTeam(teamToChange);
      for (Person member : teamToChange.getTeamMembers()) {
        member.assignToTeam(teamToChange);
      }
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a team edit
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleTeamEdit(UndoRedoObject undoRedoObject,
                              UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the teams both before and after
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 2) {
      throw new Exception("Can't undo/redo team edit - Less than 2 variables");
    }

    // Get the team name which is currently in the list and the team to edit
    Team teamToChange = (Team) undoRedoObject.getAgileItem();
    Team teamOldData;
    Team teamData;

    if (undoOrRedo == UndoOrRedo.UNDO) {
      teamData = (Team) data.get(0);
      teamOldData = (Team) data.get(1);
    } else {
      teamData = (Team) data.get(1);
      teamOldData = (Team) data.get(0);
    }

    // Make the changes and refresh the list
    for (Person oldMember : teamOldData.getTeamMembers()) {
      oldMember.removeFromTeam();
    }
    for (Person newMember : teamData.getTeamMembers()) {
      newMember.assignToTeam(teamToChange);
    }
    teamToChange.copyValues(teamData);

    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a team deletion
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleTeamDelete(UndoRedoObject undoRedoObject,
                                UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the team
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo team deletion - No variables");
    }

    // Get the team and id to undo/redo deletion of
    Team teamToChange = (Team) undoRedoObject.getAgileItem();
    Team teamData = (Team) data.get(0);

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      // Create the deleted team again
      teamToChange.copyValues(teamData);
      for (Person teamMember : teamToChange.getTeamMembers()) {
        mainApp.addPerson(teamMember);
      }
      mainApp.addTeam(teamToChange);
    } else {
      for (Person teamMember : teamToChange.getTeamMembers()) {
        mainApp.deletePerson(teamMember);
      }
      mainApp.deleteTeam(teamToChange);
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a release creation
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleReleaseCreate(UndoRedoObject undoRedoObject,
                                   UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the team
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo team creation - No variables");
    }

    // Get the team name which is currently in the list and the team to change
    Release releaseToChange = (Release) undoRedoObject.getAgileItem();
    Release releaseData = (Release) data.get(0);

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      mainApp.deleteRelease(releaseToChange);
    } else {
      releaseToChange.copyValues(releaseData);
      mainApp.addRelease(releaseToChange);
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a release edit
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleReleaseEdit(UndoRedoObject undoRedoObject,
                                 UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the releases both before and after
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 2) {
      throw new Exception("Can't undo/redo release edit - Less than 2 variables");
    }

    Release releaseToChange = (Release) undoRedoObject.getAgileItem();
    Release releaseData;

    if (undoOrRedo == UndoOrRedo.UNDO) {
//      currentRelease = (Release) data.get(1);
      releaseData = (Release) data.get(0);
    } else {
//      currentRelease = (Release) data.get(0);
      releaseData = (Release) data.get(1);
    }

    releaseToChange.copyValues(releaseData);
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a release deletion
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleReleaseDelete(UndoRedoObject undoRedoObject,
                                   UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the skill
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo release deletion - No variables");
    }

    // Get the Release to undo/redo deletion of
    Release releaseToChange = (Release) undoRedoObject.getAgileItem();
    Release releaseData = (Release) data.get(0);

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      releaseToChange.copyValues(releaseData);
      mainApp.addRelease(releaseToChange);
    } else {
      mainApp.deleteRelease(releaseToChange);
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a story creation
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleStoryCreate(UndoRedoObject undoRedoObject,
                                 UndoOrRedo undoOrRedo) throws Exception {
    // Get the data and ensure it has data for the team
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo story creation - No variables");
    }

    // Get the team name which is currently in the list and the team to change
    Story storyToChange = (Story) undoRedoObject.getAgileItem();
    Story storyData = (Story) data.get(0);
    Backlog storyBacklog = null;
    int storyEstimate = 0;
    if (data.size() > 1) {
      storyBacklog = (Backlog) data.get(1);
      Backlog storyBacklogWithEstimate = (Backlog) data.get(2);
      storyEstimate = storyBacklogWithEstimate.getSizes().get(storyToChange);
    }

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      mainApp.deleteStory(storyToChange);
      if (storyBacklog != null) {
        storyBacklog.removeStory(storyToChange);
      }
    } else {
      storyToChange.copyValues(storyData);
      mainApp.addStory(storyToChange);
      if (storyBacklog != null) {
        storyBacklog.addStory(storyToChange, storyEstimate);
      }
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a story edit
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleStoryEdit(UndoRedoObject undoRedoObject,
                               UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the releases both before and after
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 2) {
      throw new Exception("Can't undo/redo story edit - Less than 2 variables");
    }

    Story storyToChange = (Story) undoRedoObject.getAgileItem();
    Story storyData;
    Backlog storyBacklog = null;
    if (data.size() > 2) {
      storyBacklog = (Backlog) data.get(2);
    }

    if (undoOrRedo == UndoOrRedo.UNDO) {
      storyData = (Story) data.get(0);
    } else {
      storyData = (Story) data.get(1);
    }

    storyToChange.copyValues(storyData);

    // handle allocation to backlog
    if (storyBacklog != null) {
      int storyEstimate;
      Backlog before = (Backlog) data.get(3);
      Backlog after = (Backlog) data.get(4);
      if (before == null) {
        // it wasn't in a backlog before
        if (undoOrRedo == UndoOrRedo.UNDO) {
          storyBacklog.removeStory(storyToChange);              // remove from backlog
        } else {
          storyEstimate = after.getSizes().get(storyToChange);
          storyBacklog.addStory(storyToChange, storyEstimate);  // add to backlog with estimate
        }
      } else {
        if (undoOrRedo == UndoOrRedo.UNDO) {
          storyEstimate = before.getSizes().get(storyToChange);
        } else {
          storyEstimate = after.getSizes().get(storyToChange);
        }
        storyBacklog.updateStory(storyToChange, storyEstimate); // simply update estimate
      }
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a story deletion
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleStoryDelete(UndoRedoObject undoRedoObject,
                                   UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the skill
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo story deletion - No variables");
    }

    // Get the Release to undo/redo deletion of
    Story storyToChange = (Story) undoRedoObject.getAgileItem();
    Story storyData = (Story) data.get(0);
    Backlog storyBacklog = (Backlog) data.get(1);
    Backlog estimateBacklog = (Backlog) data.get(2);
    List<AgileItem> dependencyStories = data.subList(3, data.size());

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      storyToChange.copyValues(storyData);
      if (storyBacklog != null) {
        int index = estimateBacklog.getStories().indexOf(storyToChange);
        int estimate = estimateBacklog.getSizes().get(storyToChange);
        storyBacklog.addStory(index, storyToChange, estimate);
      }
      mainApp.addStory(storyToChange);
      // Re adding the dependencies to stories.
      Story mainStory;
      for (AgileItem agileStory : dependencyStories) {
        mainStory = (Story) agileStory;
        mainStory.addDependency(storyToChange);
      }
    } else {
      if (storyBacklog != null) {
        storyBacklog.removeStory(storyToChange);
      }
      mainApp.deleteStory(storyToChange);
    }

    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a backlog creation
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleBacklogCreate(UndoRedoObject undoRedoObject,
                                   UndoOrRedo undoOrRedo) throws Exception {
    // Get the data and ensure it has data for the team
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo backlog creation - No variables");
    }

    // Get the team name which is currently in the list and the team to change
    Backlog backlogToChange = (Backlog) undoRedoObject.getAgileItem();
    Backlog backlogData = (Backlog) data.get(0);

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      mainApp.deleteBacklog(backlogToChange);
    } else {
      backlogToChange.copyValues(backlogData);
      mainApp.addBacklog(backlogToChange);
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a backlog edit
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleBacklogEdit(UndoRedoObject undoRedoObject,
                                 UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the releases both before and after
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 2) {
      throw new Exception("Can't undo/redo backlog edit - Less than 2 variables");
    }

    Backlog backlogToChange = (Backlog) undoRedoObject.getAgileItem();
    Backlog backlogData;

    // Get stories whose readiness got removed
    List<AgileItem> readyStories = data.subList(2, data.size());

    if (undoOrRedo == UndoOrRedo.UNDO) {
//      currentStory = (Story) data.get(1);
      backlogData = (Backlog) data.get(0);
      for (AgileItem agileItem : readyStories) {
        Story readyStory = (Story) agileItem;
        readyStory.setStoryState(true);
      }
    } else {
//      currentStory = (Story) data.get(0);
      backlogData = (Backlog) data.get(1);
      for (AgileItem agileItem : readyStories) {
        Story readyStory = (Story) agileItem;
        readyStory.setStoryState(false);
      }
    }

    backlogToChange.copyValues(backlogData);
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a backlog deletion
   * This includes re-adding/removing stories that were deleted during a cascading delete
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleBacklogDelete(UndoRedoObject undoRedoObject,
                                 UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the skill
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo backlog deletion - No variables");
    }

    // Get the Release to undo/redo deletion of
    Backlog backlogToChange = (Backlog) undoRedoObject.getAgileItem();
    Backlog backlogData = (Backlog) data.get(0);
    Project project = null;
    List<AgileItem> blSprints = new ArrayList<>();
    // backlog was not empty when deleted
    if (data.size() > 1) {
      project = (Project) data.get(1);
      blSprints = new ArrayList<>(data.subList(2, data.size()));
    }

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      backlogToChange.copyValues(backlogData);
      for (Story blStory: backlogToChange.getStories()) {
        mainApp.addStory(blStory);
      }
      for (AgileItem blSprint : blSprints) {
        Sprint mainSprint = (Sprint) blSprint;
        mainApp.addSprint(mainSprint);
      }
      if (project != null) {
        project.setBacklog(backlogToChange);
      }
      mainApp.addBacklog(backlogToChange);
    } else {
      for (Story blStory : backlogToChange.getStories()) {
        mainApp.deleteStory(blStory);
      }
      for (AgileItem blSprint : blSprints) {
        Sprint mainSprint = (Sprint) blSprint;
        mainApp.deleteSprint(mainSprint);
      }
      if (project != null) {
        project.setBacklog(null);
      }
      mainApp.deleteBacklog(backlogToChange);
    }

    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a sprint creation
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleSprintCreate(UndoRedoObject undoRedoObject,
                                  UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the sprint
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo sprint creation - No variables");
    }

    Sprint sprintToChange = (Sprint) undoRedoObject.getAgileItem();
    Sprint sprintData = (Sprint) data.get(0);

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      // Delete the sprint from the list in the main application
      mainApp.deleteSprint(sprintToChange);
    } else {
      sprintToChange.copyValues(sprintData);
      mainApp.addSprint(sprintToChange);
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a sprint edit
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleSprintEdit(UndoRedoObject undoRedoObject,
                                UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the sprint both before and after
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 2) {
      throw new Exception("Can't undo/redo sprint edit - Less than 2 variables");
    }

    Sprint sprintToChange = (Sprint) undoRedoObject.getAgileItem();
    Sprint sprintData;

    if (undoOrRedo == UndoOrRedo.UNDO) {
      sprintData = (Sprint) data.get(0);
    } else {
      sprintData = (Sprint) data.get(1);
    }

    // Make the changes and refresh the list
    sprintToChange.copyValues(sprintData);
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a sprint deletion
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleSprintDelete(UndoRedoObject undoRedoObject,
                                  UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the sprint
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 1) {
      throw new Exception("Can't undo/redo sprint deletion - No variables");
    }

    Sprint sprintToChange = (Sprint) undoRedoObject.getAgileItem();
    Sprint sprintData = (Sprint) data.get(0);

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      // Create the deleted sprint again
      sprintToChange.copyValues(sprintData);
      mainApp.addSprint(sprintToChange);
    } else {
      mainApp.deleteSprint(sprintToChange);
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a task create
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleTaskCreate(UndoRedoObject undoRedoObject,
                                UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the task and its container
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 2) {
      throw new Exception("Can't undo/redo task create - Less than 2 variables");
    }

    Task taskToChange = (Task) undoRedoObject.getAgileItem();
    Task taskData = (Task) data.get(0);
    Taskable taskable = (Taskable) data.get(1);

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      // Delete the task from its container
      taskable.removeTask(taskToChange);
    } else {
      taskToChange.copyValues(taskData);
      taskable.addTask(taskToChange);
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a task edit
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleTaskEdit(UndoRedoObject undoRedoObject,
                              UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the task both before and after
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 2) {
      throw new Exception("Can't undo/redo task edit - Less than 2 variables");
    }

    Task taskToChange = (Task) undoRedoObject.getAgileItem();
    Task taskData;

    if (undoOrRedo == UndoOrRedo.UNDO) {
      taskData = (Task) data.get(0);
    } else {
      taskData = (Task) data.get(1);
    }

    // Make the changes and refresh the list
    taskToChange.copyValues(taskData);
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo a task deletion
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleTaskDelete(UndoRedoObject undoRedoObject,
                                UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the task
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 2) {
      throw new Exception("Can't undo/redo task deletion - Less than 2 variables");
    }

    Task taskToChange = (Task) undoRedoObject.getAgileItem();
    Task taskData = (Task) data.get(0);
    Taskable taskable = (Taskable) data.get(1);

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      // Create the deleted task again
      taskToChange.copyValues(taskData);
      taskable.addTask(taskToChange);
    } else {
      taskable.removeTask(taskToChange);
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo an effort create
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleEffortCreate(UndoRedoObject undoRedoObject,
                                  UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the effort and its container
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 2) {
      throw new Exception("Can't undo/redo effort create - Less than 2 variables");
    }

    Effort effortToChange = (Effort) undoRedoObject.getAgileItem();
    Effort effortData = (Effort) data.get(0);
    Task task = (Task) data.get(1);

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      // Delete the effort from its container
      task.removeEffort(effortToChange);
    } else {
      effortToChange.copyValues(effortData);
      task.addEffort(effortToChange);
    }
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo an effort edit
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleEffortEdit(UndoRedoObject undoRedoObject,
                                UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the effort both before and after
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 2) {
      throw new Exception("Can't undo/redo effort edit - Less than 2 variables");
    }

    Effort effortToChange = (Effort) undoRedoObject.getAgileItem();
    Effort effortData;

    if (undoOrRedo == UndoOrRedo.UNDO) {
      effortData = (Effort) data.get(0);
    } else {
      effortData = (Effort) data.get(1);
    }

    // Make the changes and refresh the list
    effortToChange.copyValues(effortData);
    mainApp.refreshList(null);
  }

  /**
   * Undo or redo an effort deletion
   *
   * @param undoRedoObject Object containing the action data
   * @param undoOrRedo     Whether undoing or redoing the action
   * @throws Exception Error message if data is invalid
   */
  private void handleEffortDelete(UndoRedoObject undoRedoObject,
                                  UndoOrRedo undoOrRedo) throws Exception {

    // Get the data and ensure it has data for the effort
    ArrayList<AgileItem> data = undoRedoObject.getData();
    if (data.size() < 2) {
      throw new Exception("Can't undo/redo effort deletion - Less than 2 variables");
    }

    Effort effortToChange = (Effort) undoRedoObject.getAgileItem();
    Effort effortData = (Effort) data.get(0);
    Task task = (Task) data.get(1);

    // Make the changes and refresh the list
    if (undoOrRedo == UndoOrRedo.UNDO) {
      // Create the deleted effort again
      effortToChange.copyValues(effortData);
      task.addEffort(effortToChange);
    } else {
      task.removeEffort(effortToChange);
    }
    mainApp.refreshList(null);
  }

  public Stack<UndoRedo> getUndoStack() {
    return undoStack;
  }

  public Stack<UndoRedo> getRedoStack() {
    return redoStack;
  }

  /**
   * Local enum for denoting whether the action is an undo or a redo
   */
  private enum UndoOrRedo {
    UNDO, REDO
  }
}
