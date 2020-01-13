package seng302.group5.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Task class is a way to log and store effort spent by people on a story.
 * Contains a list of people assigned, and also a map between people assigned and
 *
 * Created by Michael on 7/8/2015.
 */
public class Task implements AgileItem, Comparable<Task> {

  private String label;
  private String description;
  private int estimation;
  private String impediments;
  private Status status;
  private List<Person> assignedPeople;
  private List<Effort> efforts;
  private LocalDate doneDate;

  /**
   * Default constructor for task. Sets all String to empty strings, initializes list/maps.
   * Status by default is Status.NOT_STARTED. Estimate is 0.0.
   */
  public Task() {
    this.label = "";
    this.description = "";
    this.estimation = 0;
    this.impediments = "";
    this.status = Status.NOT_STARTED;
    assignedPeople = new ArrayList<>();
    this.efforts = new ArrayList<>();
    this.doneDate = null;
  }

  public Task(String label, String description, Integer estimation, Status status,
              List<Person> persons) {
    this.label = label;
    this.description = description;
    this.status = status;
    this.estimation = estimation;
    this.impediments = "";
    this.assignedPeople = new ArrayList<>();
    this.efforts = new ArrayList<>();
    addAllTaskPeople(persons);
    this.doneDate = null;
  }

  /**
   * Cloning function, copies all of clone's values in the
   * @param clone Task to be cloned
   */
  public Task(Task clone) {
    this.label = clone.getLabel();
    this.description = clone.getTaskDescription();
    this.estimation = clone.getTaskEstimation();
    this.impediments = clone.getImpediments();
    this.status = clone.getStatus();
    assignedPeople = new ArrayList<>();
    assignedPeople.addAll(clone.getTaskPeople());
    efforts = new ArrayList<>();
    efforts.addAll(clone.getEfforts());
    this.doneDate = clone.getDoneDate();
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public void setLabel(String label) {
    this.label = label;
  }

  public String getTaskDescription() {
    return description;
  }

  public void setTaskDescription(String description) {
    this.description = description;
  }

  public Integer getTaskEstimation() {
    return estimation;
  }

  public void setTaskEstimation(Integer estimation) {
    this.estimation = estimation;
  }

  public String getImpediments() {
    return impediments;
  }

  public void setImpediments(String impediments) {
    this.impediments = impediments;
  }

  public List<Effort> getEfforts() {
    return Collections.unmodifiableList(efforts);
  }

  public void removeEffort(Effort effort) {
    efforts.remove(effort);
  }

  public void addEffort(Effort effort) {
    efforts.add(effort);
  }

  public void removeAllEfforts() {
    efforts.clear();
  }

  public void addAllEfforts(List<Effort> effortList) {
    efforts.addAll(effortList);
  }

  /**
   * Returns the number of hours a given person has spent on a task.
   * @param person The person you want hours for.
   */
  public int getPersonEffort(Person person) {
    int totalEffort = 0;
    for (Effort effort : this.efforts) {
      if (effort.getWorker().equals(person)) {
        totalEffort += effort.getSpentEffort();
      }
    }
    return totalEffort;
  }

  /**
   * Return an unmodifiable list of assignedPeople
   * @return people assigned to the task
   */
  public List<Person> getTaskPeople() {
    return Collections.unmodifiableList(assignedPeople);
  }

  public void removeTaskPerson(Person person) {
    assignedPeople.remove(person);
  }

  public void addTaskPerson(Person person) {
    assignedPeople.add(person);
  }

  public void removeAllTaskPeople() {
    assignedPeople.clear();
  }

  public void addAllTaskPeople(List<Person> peopleList) {
    for (Person person : peopleList) {
      addTaskPerson(person);
    }
  }

  public void setStatus(Status status) {
    this.status = status;
    if (status == Status.DONE) {
      this.doneDate = LocalDate.now();
    }
  }

  public Status getStatus() {
    return this.status;
  }

  public LocalDate getDoneDate() {return doneDate;}

  public void setDoneDate(LocalDate date) {
    this.doneDate = date;
  }

  /**
   * Copy values from an existing AgileItem object to the current AgileItem
   *
   * @param agileItem The AgileItem object to copy values from
   */
  public void copyValues(AgileItem agileItem) {
    if (agileItem instanceof Task) {
      Task clone = (Task) agileItem;
      this.label = clone.getLabel();
      this.description = clone.getTaskDescription();
      this.estimation = clone.getTaskEstimation();
      this.impediments = clone.getImpediments();
      this.status = clone.getStatus();
      removeAllTaskPeople();
      for (Person person : clone.getTaskPeople()) {
        this.assignedPeople.add(person);
      }
      this.efforts.clear();
      this.efforts.addAll(clone.getEfforts());
      this.doneDate = clone.getDoneDate();
    }
  }

  /**
   * Overrides the toString method with the Task label.
   *
   * @return Label of task.
   */
  @Override
  public String toString() {
    return this.label;
  }

  /**
   * Equals to check if the fields of two task objects are the same.
   * @param o Object to check against
   * @return Boolean
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Task task = (Task) o;

    if (estimation != task.estimation) {
      return false;
    }
    if (!label.equals(task.label)) {
      return false;
    }
    if (description != null ? !description.equals(task.description) : task.description != null) {
      return false;
    }
    if (status != task.status) {
      return false;
    }
    if (impediments != null ? !impediments.equals(task.impediments) : task.impediments != null) {
      return false;
    }
    if (assignedPeople != null ? !assignedPeople.equals(task.assignedPeople)
                               : task.assignedPeople != null) {
      return false;
    }
    if (doneDate != null ? !doneDate.equals(task.doneDate) : task.doneDate != null) {
      return false;
    }

    return !(efforts != null ? ! efforts.equals(task.efforts) : task.efforts != null);

  }

  @Override
  public int hashCode() {
    int result = label.hashCode();
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + estimation;
    result = 31 * result + (impediments != null ? impediments.hashCode() : 0);
    result = 31 * result + status.hashCode();
    result = 31 * result + (assignedPeople != null ? assignedPeople.hashCode() : 0);
    result = 31 * result + (doneDate != null ? doneDate.hashCode() : 0);
    result = 31 * result + (efforts != null ? efforts.hashCode() : 0);
    return result;
  }

  /**
   * CompareTo method for list sorting
   * @param o Object to compare to
   * @return int befre or after in list
   */
  @Override
  public int compareTo(Task o) {
    return this.label.toLowerCase().compareTo(o.label.toLowerCase());
  }
}
