package seng302.group5.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.IdentityHashMap;

/**
 * Class modelling a Backlog. Backlogs will contain same basic descriptive information as found
 * in other agile item classes, and a way to store a map of story to size estimates. This
 * information is stored here as story size is backlog dependant, and cannot be estimated untill
 * it is inside a backlog.
 */
public class Backlog implements AgileItem, Comparable<Backlog> {

  private String label;
  private String backlogName;
  private String backlogDescription;
  private Person productOwner;
  private List<Story> stories;
  private Estimate estimate;
  private Map<Story, Integer> sizes;

  /**
   * Default constructor. Initializes stories list and sizes map, and sets the label,
   * backlogName, and backlogDescription to empty strings.
   */
  public Backlog() {
    this.label = "";
    this.backlogName = "";
    this.backlogDescription = "";
    this.productOwner = null;
    this.estimate = null;
    this.stories = new ArrayList<>();
    this.sizes = new IdentityHashMap<>();
  }

  /**
   * Constructor for backlogs. Initializes stories list and sizes map.
   *
   * @param label Label of backlog
   * @param backlogName Full name of backlog
   * @param backlogDescription Description of backlog
   * @param productOwner Product owner of backlog
   * @param estimate The estimation scale to be used.
   */
  public Backlog(String label, String backlogName, String backlogDescription, Person productOwner, Estimate estimate) {
    this.label = label;
    this.backlogName = backlogName;
    this.backlogDescription = backlogDescription;
    this.productOwner = productOwner;
    this.estimate = estimate;
    this.stories = new ArrayList<>();
    this.sizes = new IdentityHashMap<>();
  }

  /**
   * Constructor to create a clone of an existing backlog
   *
   * @param clone Backlog to clone
   */
  public Backlog(Backlog clone) {
    this.label = clone.getLabel();
    this.backlogName = clone.getBacklogName();
    this.backlogDescription = clone.getBacklogDescription();
    this.productOwner = clone.getProductOwner();
    this.estimate = clone.getEstimate();
    this.stories = new ArrayList<>();
    this.stories.addAll(clone.getStories());
    this.estimate = clone.getEstimate();
    this.sizes = new IdentityHashMap<>();
    if (!clone.getStories().isEmpty()) {
      sizes.putAll(clone.getSizes());
    }
  }


  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public void setLabel(String label) {
    this.label = label;
  }

  public String getBacklogName() {
    return backlogName;
  }

  public void setBacklogName(String backlogName) {
    this.backlogName = backlogName;
  }

  public String getBacklogDescription() {
    return backlogDescription;
  }

  public void setBacklogDescription(String backlogDescription) {
    this.backlogDescription = backlogDescription;
  }

  public Person getProductOwner() {
    return productOwner;
  }

  public void setProductOwner(Person productOwner) {
    this.productOwner = productOwner;
  }

  /**
   * Sets estimate scale for the backlog.
   * @param estimate Estimate scale
   */
  public void setEstimate(Estimate estimate) {
    this.estimate = estimate;
  }

  /**
   * Returns estimate scale for this backstory
   * @return estimate scale
   */
  public Estimate getEstimate() {
    return this.estimate;
  }

  public List<Story> getStories() {
    return Collections.unmodifiableList(stories);
  }

  /**
   * Add all stories in a collection to the backlog with the default estimate.
   * Clears previous sizes map, and sets all stories' value in map to 0.
   *
   * @param storyCollection Collection of stories
   */
  public void addAllStories(Collection<Story> storyCollection) {
    stories.addAll(storyCollection);
    for (Story story : storyCollection) {
      sizes.put(story, 0);
    }
  }

  /**
   * Add a story to the backlog with the default estimate.
   *
   * @param story Story to add
   */
  public void addStory(Story story) {
    stories.add(story);
    if (!sizes.containsKey(story)) {
      sizes.put(story, 0);
    }
  }

  /**
   * Add a story to the backlog and assign an estimate size. Should only be used when
   * adding a new story. Use updateStory to change the size keys.
   *
   * @param story Story to add
   * @param size Size of sctory in scale (ints)
   */
  public void addStory(Story story, int size) {
    this.stories.add(story);
    this.sizes.put(story, size);
  }

  /**
   * Adds a story with estimate to a specific index (priority) to the backlog.
   * Preconditions: The index is within the length of the list of stories.
   * Postconditions: It wont break.
   * @param priority The index that the story should be placed into.
   * @param story The story to be added
   * @param size  The estimate of the story being added.
   */
  public void addStory(int priority, Story story, int size) {
    this.stories.add(priority, story);
    this.sizes.put(story, size);
  }

  /**
   * Updates the size the story is mapped to on the Map. Use this if story exists but you need
   * to update the size
   *
   * @param story The story to be updates
   * @param size The new value of the story in the Map
   */
  public void updateStory(Story story, int size) {
    this.sizes.put(story, size);
  }

  /**
   * Remove all stories in the backlog. Clears stories list and sizes map.
   */
  public void removeAllStories() {
    stories.clear();
    sizes.clear();
  }

  /**
   * Remove a story from the backlog. Removes from both the stories list and sizes map.
   *
   * @param story Story to remove
   */
  public void removeStory(Story story) {
    stories.remove(story);
    sizes.remove(story);
  }

  /**
   * Gets an unmodifiable sizes map.
   * @return Map of story to size
   */
  public Map<Story, Integer> getSizes() {
    return Collections.unmodifiableMap(sizes);
  }

  /**
   * Copies the input backlog's fields into current object.
   *
   * @param agileItem The AgileItem object to copy values from
   */
  @Override
  public void copyValues(AgileItem agileItem) {
    if (agileItem instanceof Backlog) {
      Backlog clone = (Backlog) agileItem;
      this.label = clone.getLabel();
      this.backlogName = clone.getBacklogName();
      this.backlogDescription = clone.getBacklogDescription();
      this.productOwner = clone.getProductOwner();
      this.stories.clear();
      this.stories.addAll(clone.getStories());
      this.estimate = clone.getEstimate();
      this.sizes.clear();
      if (!clone.getSizes().isEmpty()) {
        this.sizes.putAll(clone.getSizes());
      }
    }
  }

  /**
   * Overrides to toString method with the label of backlog.
   *
   * @return Unique label of team.
   */
  @Override
  public String toString() {
    return label;
  }

  /**
   * Check if two backlog objects are equal by checking all fields
   *
   * @param obj Backlog to compare to
   * @return Whether backlogs are equal
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    Backlog backlog = (Backlog) obj;

    if (!label.equals(backlog.label)) {
      return false;
    }
    if (!backlogName.equals(backlog.backlogName)) {
      return false;
    }
    if (!backlogDescription.equals(backlog.backlogDescription)) {
      return false;
    }
    if (productOwner != null ? !productOwner.equals(backlog.productOwner)
                             : backlog.productOwner != null) {
      return false;
    }
    if (!stories.equals(backlog.stories)) return false;

    return sizes.equals(backlog.sizes);
  }

  /**
   * Return the hashcode for the backlog. Hashcode made using all fields.
   *
   * @return The hashcode
   */
  @Override
  public int hashCode() {
    int result = label.hashCode();
    result = 31 * result + backlogName.hashCode();
    result = 31 * result + backlogDescription.hashCode();
    result = 31 * result + (productOwner != null ? productOwner.hashCode() : 0);
    result = 31 * result + stories.hashCode();
    result = 31 * result + sizes.hashCode();
    return result;
  }

  /**
   * Compares the backlog labels.
   *
   * @param o the backlog you wish to compare to.
   * @return return whether its greater or lesser.
   */
  @Override
  public int compareTo(Backlog o) {
    return this.label.toLowerCase().compareTo(o.label.toLowerCase());
  }
}
