package seng302.group5.model.undoredo;

import java.util.ArrayList;
import java.util.List;

import seng302.group5.model.AgileItem;

/**
 * A class used to represent multiple undo/redo actions and contain the required data to do so.
 *
 * @author Su-Shing Chen
 */
public class CompositeUndoRedo implements UndoRedo {

  private Action action;
  private AgileItem agileItem;
  private ArrayList<AgileItem> data;
  private List<UndoRedo> undoRedos;
  private String actionString;

  /**
   * Constructor
   *
   * @param actionString The action of what to take
   */
  public CompositeUndoRedo(String actionString) {
    this.action = Action.COMPOSITE;
    this.data = new ArrayList<>();
    this.undoRedos = new ArrayList<>();
    this.actionString = actionString;
  }

  /**
   * Get the type of action the object is representing.
   *
   * @return Value from the Action enum representing the action type
   */
  @Override
  public Action getAction() {
    return action;
  }

  /**
   * Set the type of action the object is representing
   *
   * @param action Value from the Action enum representing the action type
   */
  @Override
  public void setAction(Action action) {
    this.action = action;
  }

  /**
   * Get the agileItem reference the UndoRedo object is concerned with
   *
   * @return AgileItem which the UndoRedo object will change on undo or redo
   */
  @Override
  public AgileItem getAgileItem() {
    return agileItem;
  }

  /**
   * Set the agileItem reference the UndoRedo object is concerned with
   *
   * @param agileItem AgileItem which the UndoRedo object will change on undo or redo
   */
  @Override
  public void setAgileItem(AgileItem agileItem) {
    this.agileItem = agileItem;
  }

  /**
   * Get the additional data required to properly perform an undo or redo. This varies depending on
   * the action specified.
   *
   * @return List of AgileItems as additional required data
   */
  @Override
  public ArrayList<AgileItem> getData() {
    return data;
  }

  /**
   * Add one instance of an AgileItem to the UndoRedo object as additional data.
   *
   * @param datum An AgileItem required for undo/redo
   */
  @Override
  public void addDatum(AgileItem datum) {
    this.data.add(datum);
  }

  /**
   * Return a string detailing the type of action the UndoRedo was created for.
   */
  @Override
  public String getActionString() {
    return actionString;
  }

  /**
   * Get the list of UndoRedos
   *
   * @return The list of UndoRedos
   */
  public List<UndoRedo> getUndoRedos() {
    return undoRedos;
  }

  /**
   * Add an UndoRedo instance to the list
   *
   * @param undoRedo An UndoRedo instance
   */
  public void addUndoRedo(UndoRedo undoRedo) {
    undoRedos.add(undoRedo);
  }
}
