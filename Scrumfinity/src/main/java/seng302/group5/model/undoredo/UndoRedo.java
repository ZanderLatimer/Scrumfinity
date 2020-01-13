package seng302.group5.model.undoredo;

import java.util.ArrayList;

import seng302.group5.model.AgileItem;

/**
 * Interface for separating the concern with singular undo/redo and composite undo/redo
 *
 * @author Su-Shing Chen
 */
public interface UndoRedo {

  /**
   * Get the type of action the object is representing.
   *
   * @return Value from the Action enum representing the action type
   */
  Action getAction();

  /**
   * Set the type of action the object is representing
   *
   * @param action Value from the Action enum representing the action type
   */
  void setAction(Action action);

  /**
   * Get the agileItem reference the UndoRedo object is concerned with
   *
   * @return AgileItem which the UndoRedo object will change on undo or redo
   */
  AgileItem getAgileItem();

  /**
   * Set the agileItem reference the UndoRedo object is concerned with
   *
   * @param agileItem AgileItem which the UndoRedo object will change on undo or redo
   */
  void setAgileItem(AgileItem agileItem);

  /**
   * Get the additional data required to properly perform an undo or redo. This varies
   * depending on the action specified.
   *
   * @return List of AgileItems as additional required data
   */
  ArrayList<AgileItem> getData();

  /**
   * Add one instance of an AgileItem to the UndoRedo object as additional data.
   *
   * @param datum An AgileItem required for undo/redo
   */
  void addDatum(AgileItem datum);

  /**
   * Return a string detailing the type of action the UndoRedo was created for.
   *
   * @return The string of what action to take.
   */
  String getActionString();
}
