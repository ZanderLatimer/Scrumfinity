package seng302.group5.model;


/**
 * Created by Michael on 3/17/2015.
 *
 * An idea for being able to show different observable lists, may scrap all together
 */
public interface AgileItem {

  /**
   * Get the label.
   *
   * @return The label
   */
  String getLabel();

  /**
   * Set the label.
   * @param label label of agile item
   */
  void setLabel(String label);

  /**
   * Copy values from an existing AgileItem object to the current AgileItem
   *
   * @param agileItem The AgileItem object to copy values from
   */
  void copyValues(AgileItem agileItem);

  /**
   * What will display in the list.
   *
   * @return String representation of item.
   */
  String toString();

  /**
   * The function which will be used to compare AgileItems. Assumes labels are unique and non null.
   *
   * @param obj Object to compare to.
   * @return Whether labels are equal or not.
   */
  boolean equals(Object obj);

}
