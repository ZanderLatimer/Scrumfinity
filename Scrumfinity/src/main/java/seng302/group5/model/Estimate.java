package seng302.group5.model;

import java.util.List;

/**
 * The estimate class. Has two fields, a String label, and a List of strings which are the
 * names of the estimates. The order of the Strings in the list dictates their relative size, index
 * 1 being the smallest, and 0 signifying not yet estimated.
 * Created by Michael on 6/2/2015.
 */
public class Estimate implements AgileItem, Comparable<Estimate> {

  private String label;
  private List<String> estimateNames;

  /**
   * Default constructor for Estimate
   */
  public Estimate() {}

  /**
   * Constructor for Estimate with a label and list of estimate names. The first index in
   * the list should always signify not set.
   * @param label String name of estimate scale
   * @param estimateNames String list of items in the sacle
   */
  public Estimate(String label, List<String> estimateNames) {
    this.label = label;
    this.estimateNames = estimateNames;
  }

  /**
   * Cloning function used to undo/redo. Copies parameter st
   * @param clone The Estimate object who's fields are to be cloned
   */
  public Estimate(Estimate clone) {
    this.label = clone.label;
    this.estimateNames = clone.estimateNames;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Copies the inputted AgileItem's field into the current Estimate object,
   * if AgileItem is an instance of Estimate.
   *
   * @param agileItem The AgileItem object to copy values from
   */
  @Override
  public void copyValues(AgileItem agileItem) {
    if (agileItem instanceof Estimate) {
      Estimate clone = (Estimate) agileItem;
      this.label = clone.getLabel();
      this.estimateNames = clone.getEstimateNames();
    }
  }

  public List<String> getEstimateNames() {
    return estimateNames;
  }

  public void setEstimateNames(List<String> estimateNames) {
    this.estimateNames = estimateNames;
  }

  /**
   * Overrides to toString method with a return of the label of estimate.
   *
   * @return Unique label of Estimate.
   */
  @Override
  public String toString() {
    return label;
  }

  /**
   * Compares the story labels (lowercase) for alphabetical sorting in lists.
   *
   * @param o the story you wish to compare to.
   * @return return whether its greater or lesser.
   */
  @Override
  public int compareTo(Estimate o) {
    return this.label.toLowerCase().compareTo(o.label.toLowerCase());
  }
}
