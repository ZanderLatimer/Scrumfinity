package seng302.group5.model;

/**
 * @author Liang Ma
 */
public class Skill implements AgileItem, Comparable<Skill> {

  private String label;
  private String skillDescription;

  /**
   * Default constructor for skill
   */
  public Skill() {
    this.label = "";
    this.skillDescription = "";
  }

  /**
   * Skill constructor with skill skillName only.
   *
   * @param label short name of a skill
   */
  public Skill(String label) {
    this.label = label;
  }

  /**
   * Skill constructor with both skill skillName and skill skillDescription.
   *
   * @param label            short name of a skill
   * @param skillDescription Description of the skill
   */
  public Skill(String label, String skillDescription) {
    this.label = label;
    this.skillDescription = skillDescription;
  }

  /**
   * Constructor to create a clone of an existing skill
   *
   * @param clone Skill to clone
   */
  public Skill(Skill clone) {
    this.label = clone.getLabel();
    this.skillDescription = clone.getSkillDescription();
  }

  /**
   * Get skillName of a skill.
   *
   * @return skillName of the skill
   */
  @Override
  public String getLabel() {
    return label;
  }

  /**
   * Set skillName for a skill.
   *
   * @param label skillName of the skill
   */
  @Override
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Get the description of the skill.
   *
   * @return description of the skill
   */
  public String getSkillDescription() {
    return this.skillDescription;
  }

  /**
   * Set the skillDescription of a skill.
   *
   * @param skillDescription skillDescription of the skill
   */
  public void setSkillDescription(String skillDescription) {
    this.skillDescription = skillDescription;
  }

  @Override
  public void copyValues(AgileItem agileItem) {
    if (agileItem instanceof Skill) {
      Skill clone = (Skill) agileItem;
      this.label = clone.getLabel();
      this.skillDescription = clone.getSkillDescription();
    }
  }

  /**
   * Return a formatted skill name along with its description.
   *
   * @return skill name and description
   */
  @Override
  public String toString() {
    return this.getLabel();
  }

  /**
   * Check if two skill's ids are equal
   *
   * @param obj Object to compare to.
   * @return Whether skill's ids are equal
   */
  @Override
  public boolean equals(Object obj) {
    boolean result = false;
    if (obj instanceof Skill) {
      Skill skill = (Skill) obj;
      result = this.label.equals(skill.getLabel());
    }
    return result;
  }

  /**
   * Compare the skill label to o label
   * @param o the skill you wish to compare to
   * @return whether it is greater or lesser
   */
  @Override
  public int compareTo(Skill o) {
    return this.label.toLowerCase().compareTo(o.label.toLowerCase());
  }
}
