package seng302.group5.model;

import java.time.LocalDate;

/**
 * Release class that sets the model for a release.
 * Created by Craig Barnard on 07/04/2015
 */
public class Release implements AgileItem, Comparable<Release> {

  private String label;
  private String releaseDescription;
  private LocalDate releaseDate;
  private String releaseNotes;
  private Project projectRelease = null;

  public Release() {
    this.label = "";
    this.releaseDescription = "";
    this.releaseDate = null;
    this.releaseNotes = "";
  }

  /**
   * Constructor for Release object.
   *
   * @param label              name of release, None-Null, Can't be greater than 8 characters.
   * @param releaseDescription description of release
   * @param releaseDate        date of release
   * @param releaseNotes       release notes
   * @param projectRelease     project allocated to
   */
  public Release(String label, String releaseDescription, String releaseNotes,
                 LocalDate releaseDate, Project projectRelease) {
    this.label = label;
    this.releaseDescription = releaseDescription;
    this.releaseDate = releaseDate;
    this.releaseNotes = releaseNotes;
    this.projectRelease = projectRelease;
  }

  public Release(Release clone) {
    this.label = clone.getLabel();
    this.releaseDescription = clone.getReleaseDescription();
    this.releaseDate = clone.getReleaseDate();
    this.releaseNotes = clone.getReleaseNotes();
    this.projectRelease = clone.getProjectRelease();
  }

  @Override
  public String getLabel() {
    return this.label;
  }

  @Override
  public void setLabel(String label) {
    this.label = label;
  }

  public String getReleaseDescription() {
    return this.releaseDescription;
  }

  public void setReleaseDescription(String releaseDescription) {
    this.releaseDescription = releaseDescription;
  }

  public LocalDate getReleaseDate() {
    return this.releaseDate;
  }

  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }

  public String getReleaseNotes() {
    return this.releaseNotes;
  }

  public void setReleaseNotes(String releaseNotes) {
    this.releaseNotes = releaseNotes;
  }

  public Project getProjectRelease() {
    return this.projectRelease;
  }

  public void setProjectRelease(Project projectRelease) {
    this.projectRelease = projectRelease;
  }

  /**
   * Copies the release input fields into current object.
   *
   * @param agileItem Person who's fields are to be copied
   */
  @Override
  public void copyValues(AgileItem agileItem) {
    if (agileItem instanceof Release) {
      Release clone = (Release) agileItem;
      this.label = clone.getLabel();
      this.releaseDescription = clone.getReleaseDescription();
      this.releaseDate = clone.getReleaseDate();
      this.releaseNotes = clone.getReleaseNotes();
      this.projectRelease = clone.getProjectRelease();
    }
  }

  /**
   * toString override
   *
   * @return Release's name
   */
  @Override
  public String toString() {
    return this.label;
  }

  /**
   * Check if two releases' ids are equal
   *
   * @param obj Object to compare to.
   * @return Whether releases' ids are equal
   */
  @Override
  public boolean equals(Object obj) {
    boolean result = false;
    if (obj instanceof Release) {
      Release release = (Release) obj;
      result = this.label.equals(release.getLabel());
    }
    return result;
  }

  /**
   * Compare the release label to o's label
   * @param o the release you wish to compare to.
   * @return whether its greater or lesser than o.
   */
  @Override
  public int compareTo(Release o) {
    return this.label.toLowerCase().compareTo(o.label.toLowerCase());
  }
}
