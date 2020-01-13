package seng302.group5.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Effort implements AgileItem, Comparable<Effort> {

  private String effortLabel;
  private Person worker;
  private int spentEffort;
  private String comments;
  private LocalDateTime dateTime;

  /**
   * Empty constructor for an Effort object.
   */
  public Effort() {
    this.effortLabel = "";
    this.worker = null;
    this.spentEffort = 0;
    this.comments = "";
    this.dateTime = null;
  }

  /**
   * Default constructor for an Effort object including all fields.
   *
   * @param worker The Person logging the effort.
   * @param spentEffort The spentEffort they are logging (as a double).
   * @param comments Any comments about the logged Effort.
   * @param dateTime The spentEffort the effort was logged.
   */
  public Effort(Person worker, int spentEffort, String comments, LocalDateTime dateTime) {
    this.effortLabel = generateLabel(worker, spentEffort, comments, dateTime);
    this.worker = worker;
    this.spentEffort = spentEffort;
    this.comments = comments;
    this.dateTime = dateTime;
  }

  /**
   * A constructor to create a clone of an existing Effort object.
   * @param clone The Effort item for cloning.
   */
  public Effort(Effort clone) {
    this.effortLabel = clone.getLabel();
    this.worker = clone.getWorker();
    this.spentEffort = clone.getSpentEffort();
    this.comments = clone.getComments();
    this.dateTime = clone.getDateTime();
  }

  private String generateLabel(Person worker, int spentEffort,
                               String comments, LocalDateTime dateTime) {
    int result = worker.hashCode() + Integer.hashCode(spentEffort) + comments.hashCode() +
                 dateTime.hashCode() + LocalTime.now().hashCode();
    return dateTime.toString() + String.valueOf(result);
  }

  public String getLabel() {
    return effortLabel;
  }

  public void setLabel(String effortLabel) {
    this.effortLabel = effortLabel;
  }

  public Person getWorker() {
    return worker;
  }

  public void setWorker(Person worker) {
    this.worker = worker;
  }

  public int getSpentEffort() {
    return spentEffort;
  }

  public void setSpentEffort(int spentEffort) {
    this.spentEffort = spentEffort;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  /**
   * Copies the values from an Effort AgileItem into a new Effort object.
   * @param agileItem The AgileItem object to copy values from.
   */
  @Override
  public void copyValues(AgileItem agileItem) {
    if (agileItem instanceof Effort) {
      Effort clone = (Effort) agileItem;
      this.effortLabel = clone.getLabel();
      this.worker = clone.getWorker();
      this.spentEffort = clone.getSpentEffort();
      this.comments = clone.getComments();
      this.dateTime = clone.getDateTime();
    }
  }

  /**
   * Return the string representation of the effort.
   *
   * @return The effort's string representation
   */
  @Override
  public String toString() {
    // todo adjust for list
    return "Effort{" +
           "worker=" + worker +
           ", spentEffort=" + spentEffort +
           ", comments='" + comments + '\'' +
           ", dateTime=" + dateTime +
           '}';
  }

  /**
   * Compares this Effort with another for equality.
   * @param o The Effort objcet to compare with this one.
   * @return Whether or not this Effort and the other Effort are equal.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Effort effort = (Effort) o;

    if (!effortLabel.equals(effort.getLabel())) {
      return false;
    }
    if (!comments.equals(effort.getComments())) {
      return false;
    }
    if (spentEffort != effort.getSpentEffort()) {
      return false;
    }
    if (!worker.equals(effort.getWorker())) {
      return false;
    }
    return dateTime.equals(effort.getDateTime());
  }

  /**
   * Generates the hashcode for an Effort object.
   * @return The hashcode generated from the Effort object.
   */
  @Override
  public int hashCode() {
    int result = effortLabel.hashCode();
    result = 31 * result + worker.hashCode();
    result = 31 * result + Integer.hashCode(spentEffort);
    result = 31 * result + comments.hashCode();
    result = 31 * result + dateTime.hashCode();
    return result;
  }

  /**
   * Compares the the endDate of this Effort with another.
   * @param o The Effort to compare to.
   * @return Whether this Efforts endDate is greater or lesser than the other.
   */
  @Override
  public int compareTo(Effort o) {
    return this.dateTime.compareTo(o.getDateTime());
  }
}
