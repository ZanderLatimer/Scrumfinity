package seng302.group5.model;

/**
 * Store a role that a team member can have.
 *
 * @author Alex Woo
 */
public class Role implements Comparable<Role> {

  private String label;
  private String roleName;
  private Skill requiredSkill;
  private int memberLimit;


  /**
   * Simple Constructor.
   *
   * @param label    Unique, non-null label for the role.
   * @param roleName Name of the role.
   */
  public Role(String label, String roleName) {
    this.label = label;
    this.roleName = roleName;
    this.requiredSkill = null;
    this.memberLimit = Integer.MAX_VALUE; // Infinity
  }

  /**
   * Constructor.
   *
   * @param label       Unique, non-null label for role.
   * @param roleName    Name of the role.
   * @param skill       The skill required to be assigned this role.
   * @param memberLimit The number of members allowed to have this role.
   */
  public Role(String label, String roleName, Skill skill, int memberLimit) {
    this.label = label;
    this.roleName = roleName;
    this.requiredSkill = skill;
    this.memberLimit = memberLimit;
  }

  /**
   * Default Constructor.
   */
  public Role() {
    this.label = "";
    this.roleName = "";
    this.requiredSkill = null;
    this.memberLimit = Integer.MAX_VALUE;
  }

  /**
   * Cloning constructor
   *
   * @param clone Role object to clone
   */
  public Role(Role clone) {
    this.label = clone.getLabel();
    this.roleName = clone.getRoleName();
    this.requiredSkill = clone.getRequiredSkill();
    this.memberLimit = clone.getMemberLimit();
  }


  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public Skill getRequiredSkill() {
    return requiredSkill;
  }

  public void setRequiredSkill(Skill requiredSkill) {
    this.requiredSkill = requiredSkill;
  }

  public int getMemberLimit() {
    return memberLimit;
  }

  public void setMemberLimit(int memberLimit) {
    this.memberLimit = memberLimit;
  }

  /**
   * toString override.
   *
   * @return Role's roleName
   */
  @Override
  public String toString() {
    return roleName;
  }

  /**
   * Compares the labels of this role and the role o
   * @param o the role you wish to compare with.
   * @return comparable int
   */
  @Override
  public int compareTo(Role o) {
    return this.label.toLowerCase().compareTo(o.label.toLowerCase());
  }
}
