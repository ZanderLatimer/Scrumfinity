package seng302.group5.model.undoredo;

import java.util.ArrayList;

import seng302.group5.model.AgileItem;

/**
 * A class used to represent one single undo/redo action and contain the required data to do so.
 *
 * @author Su-Shing Chen
 */
public class UndoRedoObject implements UndoRedo {

  private Action action;
  private AgileItem agileItem;
  private ArrayList<AgileItem> data;

  public UndoRedoObject() {
    this.action = Action.UNDEFINED;
    this.data = new ArrayList<>();
  }

  @Override
  public Action getAction() {
    return action;
  }

  @Override
  public void setAction(Action action) {
    this.action = action;
  }

  @Override
  public AgileItem getAgileItem() {
    return agileItem;
  }

  @Override
  public void setAgileItem(AgileItem agileItem) {
    this.agileItem = agileItem;
  }

  @Override
  public ArrayList<AgileItem> getData() {
    return data;
  }

  @Override
  public void addDatum(AgileItem datum) {
    this.data.add(datum);
  }

  /**
   * Return a string detailing the type of action the UndoRedo was created for.
   */
  @Override
  public String getActionString() {
    return Action.getActionString(action);
  }
}
