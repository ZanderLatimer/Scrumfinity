package seng302.group5.model.undoredo;

import java.util.EnumMap;
import java.util.Map;

/**
 * Defines actions to be stored into the undo/redo handler
 *
 * @author Su-Shing Chen
 */
public enum Action {
  PROJECT_CREATE, // Project creation - 1 AgileItem
  PROJECT_EDIT,   // Project edit - 2 AgileItems
  PROJECT_DELETE, // Project deletion - 1 AgileItem plus all related releases
  PERSON_CREATE,  // Person creation - 1 AgileItem
  PERSON_EDIT,    // Person edit - 2 AgileItems
  PERSON_DELETE,  // Person deletion - 1 AgileItem
  SKILL_CREATE,   // Skill creation - 1 AgileItem
  SKILL_EDIT,     // Skill edit - 2 AgileItems
  SKILL_DELETE,   // Skill deletion - 1 AgileItem plus all skill users
  TEAM_CREATE,    // Team creation - 1 AgileItem
  TEAM_EDIT,      // Team edit - 2 AgileItems
  TEAM_DELETE,    // Team deletion - 1 AgileItem
  RELEASE_CREATE, // Release creation - 1 AgileItem
  RELEASE_EDIT,   // Release edit - 2 AgileItems
  RELEASE_DELETE, // Release delete - 1 AgileItem
  STORY_CREATE,   // Story creation - 1 AgileItem [plus 2 backlogs: orig ref, clone after create]
  STORY_EDIT,     // Story edit - 2 AgileItems
  STORY_DELETE,   // Story delete - 1 AgileItem [plus 3 backlogs: orig ref, clone before or null, clone after]
  BACKLOG_CREATE, // Backlog creation - 1 AgileItem
  BACKLOG_EDIT,   // Backlog edit - 2 AgileItems
  BACKLOG_DELETE, // Backlog delete - 1 AgileItem
  SPRINT_CREATE,  // Sprint creation - 1 AgileItem
  SPRINT_EDIT,    // Sprint edit - 2 AgileItems
  SPRINT_DELETE,  // Sprint delete - 1 AgileItem
  TASK_CREATE,    // Task creation - 1 AgileItem plus 1 Taskable
  TASK_EDIT,      // Task edit - 2 AgileItems
  TASK_DELETE,    // Task delete - 1 AgileItem plus 1 Taskable
  EFFORT_CREATE,  // Effort creation - 1 AgileItem plus 1 Task
  EFFORT_EDIT,    // Effort edit - 2 AgileItems
  EFFORT_DELETE,  // Effort delete - 1 AgileItem plus 1 Task
  COMPOSITE,      // Composite undo/redo instances
  UNDEFINED;      // Undefined action - no AgileItems

  private static Map<Action, String> actionStringMap;
  static {
    actionStringMap = new EnumMap<>(Action.class);

    String createStr = "Create ";
    String editStr = "Edit ";
    String deleteStr = "Delete ";

    String projectStr = "Project";
    String personStr = "Person";
    String skillStr = "Skill";
    String teamStr = "Team";
    String releaseStr = "Release";
    String storyStr = "Story";
    String backlogStr = "Backlog";
    String sprintStr = "Sprint";
    String taskStr = "Task";
    String effortStr = "Effort";

    String actionStr;
    String typeStr;

    for (Action action : Action.values()) {
      // for action string
      if (action.name().contains("CREATE")) {
        actionStr = createStr;
      } else if (action.name().contains("EDIT")) {
        actionStr = editStr;
      } else if (action.name().contains("DELETE")) {
        actionStr = deleteStr;
      } else {
        actionStr = null;
      }
      // for type string
      if (action.name().contains("PROJECT")) {
        typeStr = projectStr;
      } else if (action.name().contains("PERSON")) {
        typeStr = personStr;
      } else if (action.name().contains("SKILL")) {
        typeStr = skillStr;
      } else if (action.name().contains("TEAM")) {
        typeStr = teamStr;
      } else if (action.name().contains("RELEASE")) {
        typeStr = releaseStr;
      } else if (action.name().contains("STORY")) {
        typeStr = storyStr;
      } else if (action.name().contains("BACKLOG")) {
        typeStr = backlogStr;
      } else if (action.name().contains("SPRINT")) {
        typeStr = sprintStr;
      } else if (action.name().contains("TASK")) {
        typeStr = taskStr;
      } else if (action.name().contains("EFFORT")) {
        typeStr = effortStr;
      } else {
        typeStr = null;
      }

      if (actionStr == null || typeStr == null) {
        actionStringMap.put(action, "Undefined");
      } else {
        actionStringMap.put(action, actionStr + typeStr);
      }
    }
  }

  public static String getActionString(Action action) {
    return actionStringMap.get(action);
  }
}
