package seng302.group5.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This is the Sprint model.
 * This will contain:
 * Goal, Description, Full name, Team, Backlog, Project, Release, start and end date and a list
 * of stories.
 *
 * The project is found through the backlog. This logic should be implemented in the controller.
 *
 * Created by Alex Woo
 */
public class Sprint implements AgileItem, Taskable, Comparable<Sprint> {

  private String sprintGoal;
  private String sprintDescription;
  private String sprintFullName;
  private Team sprintTeam;
  private Backlog sprintBacklog;
  private Project sprintProject;
  private Release sprintRelease;
  private String sprintImpediments;

  private LocalDate sprintStart;
  private LocalDate sprintEnd;

  private List<Story> sprintStories;
  private List<Task> tasks;

  /**
   * Empty constructor used for save/load.
   */
  public Sprint() {
    sprintGoal = "";
    sprintDescription = "";
    sprintFullName = "";
    sprintImpediments = "";
    sprintTeam = null;
    sprintBacklog = null;
    sprintProject = null;
    sprintRelease = null;
    sprintStart = null;
    sprintEnd = null;
    sprintStories = new ArrayList<>();
    tasks = new ArrayList<>();
  }

  /**
   * This is the main constructor for the Sprint model. It takes in all the needed variables to fill
   * in the models variables.
   *
   * @param sprintGoal Goal for the sprint. Behaves as a label.
   * @param sprintFullName Full name for the sprint
   * @param sprintDescription Description of the sprint
   * @param sprintBacklog Backlog which this sprint is assigned to
   * @param sprintProject Project which this sprint is assigned to
   * @param sprintTeam Team assigned to this sprint
   * @param sprintRelease Release which comes from this sprint
   * @param sprintStart Start of the sprint
   * @param sprintEnd End of the sprint
   * @param sprintStories Stories in the sprint, in priority order
   */
  public Sprint(String sprintGoal, String sprintFullName, String sprintDescription,
                Backlog sprintBacklog, Project sprintProject, Team sprintTeam,
                Release sprintRelease, LocalDate sprintStart, LocalDate sprintEnd,
                List<Story> sprintStories) {
    this.sprintGoal = sprintGoal;
    this.sprintFullName = sprintFullName;
    this.sprintDescription = sprintDescription;
    this.sprintBacklog = sprintBacklog;
    this.sprintProject = sprintProject;
    this.sprintTeam = sprintTeam;
    this.sprintRelease = sprintRelease;
    this.sprintStart = sprintStart;
    this.sprintEnd = sprintEnd;
    this.sprintImpediments = "";
    this.sprintStories = new ArrayList<>();
    this.sprintStories.addAll(sprintStories);
    this.tasks = new ArrayList<>();
  }

  /**
   * Constructor to create a clone of existing Sprint.
   *
   * @param clone Sprint to clone.
   */
  public Sprint(Sprint clone) {
    this.sprintGoal = clone.getSprintGoal();
    this.sprintFullName = clone.getSprintFullName();
    this.sprintDescription = clone.getSprintDescription();
    this.sprintBacklog = clone.getSprintBacklog();
    this.sprintProject = clone.getSprintProject();
    this.sprintTeam = clone.getSprintTeam();
    this.sprintRelease = clone.getSprintRelease();
    this.sprintStart = clone.getSprintStart();
    this.sprintEnd = clone.getSprintEnd();
    this.sprintImpediments = clone.getSprintImpediments();
    this.sprintStories = new ArrayList<>();
    this.sprintStories.addAll(clone.getSprintStories());
    this.tasks = new ArrayList<>();
    this.tasks.addAll(clone.getTasks());
  }

  @Override
  public String getLabel() {
    return sprintGoal;
  }

  @Override
  public void setLabel(String label) {
    sprintGoal = label;
  }

  public String getSprintGoal() {
    return sprintGoal;
  }

  public void setSprintGoal(String sprintGoal) {
    this.sprintGoal = sprintGoal;
  }

  public String getSprintDescription() {
    return sprintDescription;
  }

  public void setSprintDescription(String sprintDescription) {
    this.sprintDescription = sprintDescription;
  }

  public String getSprintImpediments() {
    return this.sprintImpediments;
  }

  public void setSprintImpediments(String impediment) {
    this.sprintImpediments = impediment;
  }

  public String getSprintFullName() {
    return sprintFullName;
  }

  public void setSprintFullName(String sprintFullName) {
    this.sprintFullName = sprintFullName;
  }

  public Team getSprintTeam() {
    return sprintTeam;
  }

  public void setSprintTeam(Team sprintTeam) {
    this.sprintTeam = sprintTeam;
  }

  public Backlog getSprintBacklog() {
    return sprintBacklog;
  }

  public void setSprintBacklog(Backlog sprintBacklog) {
    this.sprintBacklog = sprintBacklog;
  }

  public Project getSprintProject() {
    return sprintProject;
  }

  public void setSprintProject(Project sprintProject) {
    this.sprintProject = sprintProject;
  }

  public Release getSprintRelease() {
    return sprintRelease;
  }

  public void setSprintRelease(Release sprintRelease) {
    this.sprintRelease = sprintRelease;
  }

  public LocalDate getSprintStart() {
    return sprintStart;
  }

  public void setSprintStart(LocalDate sprintStart) {
    this.sprintStart = sprintStart;
  }

  public LocalDate getSprintEnd() {
    return sprintEnd;
  }

  public void setSprintEnd(LocalDate sprintEnd) {
    this.sprintEnd = sprintEnd;
  }

  public List<Story> getSprintStories() {
    return Collections.unmodifiableList(sprintStories);
  }


  /**
   * Add all stories in a collection to the sprint.
   *
   * @param storyCollection Collection of stories
   */
  public void addAllStories(Collection<Story> storyCollection) {
    sprintStories.addAll(storyCollection);
  }

  /**
   * Add a story to the sprint.
   *
   * @param story Story to add
   */
  public void addStory(Story story) {
    sprintStories.add(story);
  }

  /**
   * Remove all stories in the sprint.
   */
  public void removeAllStories() {
    sprintStories.clear();
  }

  /**
   * Remove a story from the sprint.
   *
   * @param story Story to remove
   */
  public void removeStory(Story story) {
    sprintStories.remove(story);
  }

  /**
   * Get the tasks of the sprint.
   *
   * @return List of Task objects assigned to sprint.
   */
  @Override
  public List<Task> getTasks() {
    return Collections.unmodifiableList(tasks);
  }

  /**
   * Add a single task to the sprint.
   *
   * @param task Task to add.
   */
  @Override
  public void addTask(Task task) {
    this.tasks.add(task);
  }

  public void addTask(int pos, Task task) {
    this.tasks.add(pos, task);
  }

  /**
   * Add a collection of tasks to the sprint.
   *
   * @param tasks Collection of tasks to add.
   */
  @Override
  public void addAllTasks(Collection<Task> tasks) {
    this.tasks.addAll(tasks);
  }

  /**
   * Remove a single task from the sprint.
   *
   * @param task Task to remove.
   */
  @Override
  public void removeTask(Task task) {
    this.tasks.remove(task);
  }

  /**
   * Remove all tasks from the sprint.
   */
  @Override
  public void removeAllTasks() {
    this.tasks.clear();
  }

  /**
   * Copies the Sprint input fields into current object.
   * @param agileItem Sprint that's fields are to be copied.
   */
  @Override
  public void copyValues(AgileItem agileItem) {
    if (agileItem instanceof Sprint) {
      Sprint clone = (Sprint) agileItem;
      this.sprintGoal = clone.getSprintGoal();
      this.sprintDescription = clone.getSprintDescription();
      this.sprintFullName = clone.getSprintFullName();
      this.sprintTeam = clone.getSprintTeam();
      this.sprintBacklog = clone.getSprintBacklog();
      this.sprintRelease = clone.getSprintRelease();
      this.sprintStart = clone.getSprintStart();
      this.sprintEnd = clone.getSprintEnd();
      this.sprintImpediments = clone.getSprintImpediments();
      this.sprintStories.clear();
      this.sprintStories.addAll(clone.getSprintStories());
      this.tasks.clear();
      this.tasks.addAll(clone.getTasks());
      this.sprintProject = clone.getSprintProject();
    }
  }

  /**
   * Overrides the toString method with the sprint Goal which acts like a label.
   * @return Label of sprint/ Sprint Goal.
   */
  @Override
  public String toString() {
    return this.sprintGoal;
  }

  /**
   * This is a equals method generated by intellij
   * @param o - The object to be checked if equal or not.
   * @return True or False depending on whether it is equal or not
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Sprint sprint = (Sprint) o;

    if (sprintBacklog != null ? !sprintBacklog.equals(sprint.sprintBacklog)
                              : sprint.sprintBacklog != null) {
      return false;
    }
    if (sprintDescription != null ? !sprintDescription.equals(sprint.sprintDescription)
                                  : sprint.sprintDescription != null) {
      return false;
    }
    if (sprintEnd != null ? !sprintEnd.equals(sprint.sprintEnd) : sprint.sprintEnd != null) {
      return false;
    }
    if (sprintFullName != null ? !sprintFullName.equals(sprint.sprintFullName)
                               : sprint.sprintFullName != null) {
      return false;
    }
    if (sprintImpediments != null ? !sprintImpediments.equals(sprint.sprintImpediments)
                                  : sprint.sprintImpediments != null) {
      return false;
    }
    if (!sprintGoal.equals(sprint.sprintGoal)) {
      return false;
    }
    if (sprintProject != null ? !sprintProject.equals(sprint.sprintProject)
                              : sprint.sprintProject != null) {
      return false;
    }
    if (sprintRelease != null ? !sprintRelease.equals(sprint.sprintRelease)
                              : sprint.sprintRelease != null) {
      return false;
    }
    if (sprintStart != null ? !sprintStart.equals(sprint.sprintStart)
                            : sprint.sprintStart != null) {
      return false;
    }
    if (sprintStories != null ? !sprintStories.equals(sprint.sprintStories)
                              : sprint.sprintStories != null) {
      return false;
    }
    if (sprintTeam != null ? !sprintTeam.equals(sprint.sprintTeam) : sprint.sprintTeam != null) {
      return false;
    }

    return true;
  }

  /**
   * Intellij generated hashCode redo so equals works properly
   * @return the new hashcode
   */
  @Override
  public int hashCode() {
    int result = sprintGoal.hashCode();
    result = 31 * result + (sprintDescription != null ? sprintDescription.hashCode() : 0);
    result = 31 * result + (sprintFullName != null ? sprintFullName.hashCode() : 0);
    result = 31 * result + (sprintTeam != null ? sprintTeam.hashCode() : 0);
    result = 31 * result + (sprintBacklog != null ? sprintBacklog.hashCode() : 0);
    result = 31 * result + (sprintProject != null ? sprintProject.hashCode() : 0);
    result = 31 * result + (sprintRelease != null ? sprintRelease.hashCode() : 0);
    result = 31 * result + (sprintImpediments != null ? sprintImpediments.hashCode() : 0);
    result = 31 * result + (sprintStart != null ? sprintStart.hashCode() : 0);
    result = 31 * result + (sprintEnd != null ? sprintEnd.hashCode() : 0);
    result = 31 * result + (sprintStories != null ? sprintStories.hashCode() : 0);
    return result;
  }

  /**
   * Compare this Sprints goal/label to sprint o's goal/label
   * @param o sprint you wish to compare to
   * @return whether it is greater or lesser.
   */
  @Override
  public int compareTo(Sprint o) {
    return this.sprintGoal.toLowerCase().compareTo(o.sprintGoal.toLowerCase());
  }
}
