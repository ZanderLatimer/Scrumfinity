package seng302.group5.model.util;

import java.io.File;
import java.nio.channels.spi.SelectorProvider;

import seng302.group5.model.AgileItem;
import seng302.group5.model.Backlog;
import seng302.group5.model.Person;
import seng302.group5.model.Project;
import seng302.group5.model.Release;
import seng302.group5.model.Skill;
import seng302.group5.model.Sprint;
import seng302.group5.model.Story;
import seng302.group5.model.Team;

/**
 * Default settings for program, contains static variables for checking states of elements in program.
 * Created by Michael on 3/19/2015.
 */
public class Settings {

  public static File defaultFilepath;
  public static File currentFile;
  public static String currentListType;
  public static String organizationName = "";
  public static double progVersion = 0.6;


  /**
   * Sets default scrumfinity filepath for open/load.
   */
  public static void setSysDefault() {
    File scrumHome;
    String directory;

    directory = System.getProperty("user.home");
    directory = directory + File.separator + "Scrumfinity";
    scrumHome = new File(directory);

    if (!scrumHome.exists()) {
      scrumHome.mkdir();
    }
    if (scrumHome.exists()) {
      defaultFilepath = scrumHome;
    }
  }


  /**
   * Used to check if the list being displayed contaisn the objects of same type as the
   * one being created.
   *
   * @param agileItem agileItem being created
   * @return Bool if displayed list being viewed contains object type.
   */
  public static boolean correctList(AgileItem agileItem) {
    String listType = Settings.currentListType;
    switch (listType) {
      case "Projects":
        return agileItem instanceof Project;
      case "People":
        return agileItem instanceof Person;
      case "Skills":
        return agileItem instanceof Skill;
      case "Teams":
        return agileItem instanceof Team;
      case "Releases":
        return agileItem instanceof Release;
      case "Stories":
        return agileItem instanceof Story;
      case "Backlogs":
        return agileItem instanceof Backlog;
      case "Sprints":
        return agileItem instanceof Sprint;
    }
    return false;
  }
}
