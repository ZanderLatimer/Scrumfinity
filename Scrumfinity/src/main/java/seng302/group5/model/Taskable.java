package seng302.group5.model;

import java.util.Collection;
import java.util.List;

/**
 * Interface for representing an object that can contain tasks.
 */
public interface Taskable extends AgileItem {

  /**
   * Get the tasks of the object.
   *
   * @return List of Task objects assigned to object.
   */
  List<Task> getTasks();

  /**
   * Add a single task to the object.
   *
   * @param task Task to add.
   */
  void addTask(Task task);

  /**
   * Add a collection of tasks to the object.
   *
   * @param tasks Collection of tasks to add.
   */
  void addAllTasks(Collection<Task> tasks);

  /**
   * Remove a single task from the object.
   *
   * @param task Task to remove.
   */
  void removeTask(Task task);

  /**
   * Remove all tasks from the object.
   */
  void removeAllTasks();
}
