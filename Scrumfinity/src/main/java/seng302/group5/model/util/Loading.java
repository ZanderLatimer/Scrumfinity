package seng302.group5.model.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import seng302.group5.Main;
import seng302.group5.model.AgileHistory;
import seng302.group5.model.Backlog;
import seng302.group5.model.Effort;
import seng302.group5.model.Estimate;
import seng302.group5.model.Person;
import seng302.group5.model.Project;
import seng302.group5.model.Release;
import seng302.group5.model.Role;
import seng302.group5.model.Skill;
import seng302.group5.model.Sprint;
import seng302.group5.model.Status;
import seng302.group5.model.Story;
import seng302.group5.model.Task;
import seng302.group5.model.Team;

/**
 * Class for loading xml save files
 * Created by Michael on 4/21/2015.
 */
public class Loading {

  private Main main;
  private BufferedReader loadedFile;
  private double saveVersion = 0;

  public Loading(Main main) {
    this.main = main;
  }

  /**
   * Loads all the data from the xml file into main app
   *
   * @param file File to load from
   * @return boolean indicating if file is fully loaded or it broke
   */
  public boolean loadFile(File file) {
    boolean successfulLoad = true;

    // Turns the file into a string
    String filename = file.toString();
    if (!filename.endsWith(".xml")) {
      filename = filename + ".xml";
    }
    try {
      loadedFile = new BufferedReader(new FileReader(filename));
      loadHeader();
      loadProjects();
      loadPeople();
      loadSkills();
      loadTeams();
      loadReleases();
      syncTeamAllocation();
      loadRoles();
      syncRoles();
      if (saveVersion >= 0.2) {
        loadStories();
      }
      if (saveVersion >= 0.3) {
        loadEstimates();
        loadBacklogs();
      } else {
        main.createDefaultEstimates();
      }
      if (saveVersion >= 0.4) {
        loadSprints();
      }
      if (saveVersion >= 0.5) {
        syncTaskPeople();
      }


    } catch (Exception e) {
      successfulLoad = false;

      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Loading error");
      alert.setHeaderText(null);

      String message;

      if(Settings.organizationName.equals("")) {
        message = "This is either not a Scrumfinity file or is an"
                  + " unsupported file version that cannot be loaded.";
      } else if(saveVersion == Settings.progVersion) {
        message = "This file appears to have corrupted and cannot be loaded.";
      } else {
        message = "This file may be corrupted or is from"
                  + " an unsupported version and cannot be loaded.";
      }


      alert.setContentText(message);
      alert.getDialogPane().setPrefHeight(120);
      alert.showAndWait();
//      e.printStackTrace();
    } finally {
      try {
        if (loadedFile != null) {
          loadedFile.close();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return successfulLoad;
  }

  /**
   * Loads the header, if no header available, assumes saveFile is version 0.1
   *
   * @throws Exception Could not read line from file.
   */
  private void loadHeader() throws Exception {
    String headerLine;
    String progVersion;
    String orgName;
    headerLine = loadedFile.readLine();
    if (headerLine.startsWith("<scrumfinity")) {
      progVersion = headerLine.replaceAll("(?i)(.*version=\")(.+?)(\" org.*)", "$2");
      orgName = headerLine.replaceAll("(?i)(.*organization=\")(.+?)(\">)", "$2");
      if (orgName.equals("__undefined__")) {
        orgName = "";
      }
      Settings.organizationName = orgName;
      saveVersion = Double.parseDouble(progVersion);
    }
  }

  /**
   * Loads Projects from xml files into main app
   *
   * @throws Exception Problem with loading
   */
  private void loadProjects() throws Exception {
    String projectLine;
    String projectData;
    AgileHistory teamHistoryItem;
    Team tempTeam;
    LocalDate startDate;
    LocalDate endDate;
    Backlog tempBacklog;

    // Until Project end tag
    while ((!(projectLine = loadedFile.readLine()).equals("</Projects>"))) {
      // On new Person tag
      if (projectLine.matches(".*<Project>")) {
        Project newProject = new Project();

        // Mandatory fields
        projectLine = loadedFile.readLine();
        projectData =
            projectLine.replaceAll("(?i)(.*<projectLabel.*?>)(.+?)(</projectLabel>)", "$2");
        newProject.setLabel(projectData);

        // Non mandatory fields.
        while ((!(projectLine = loadedFile.readLine()).equals("\t</Project>"))) {
          if (projectLine.startsWith("\t\t<projectName>")) {
            projectData = projectLine.replaceAll("(?i)(.*<projectName.*?>)(.+?)(</projectName>)", "$2");
            newProject.setProjectName(projectData);
          }
          if (projectLine.startsWith("\t\t<projectDescription>")) {
            String descBuilder;
            if (!projectLine.endsWith("</projectDescription>")) {
              descBuilder = projectLine
                                .replaceAll("(?i)(.*<projectDescription.*?>)(.+?)", "$2") + "\n";
              while ((!(projectLine = loadedFile.readLine()).endsWith("</projectDescription>"))) {
                descBuilder += projectLine + "\n";
              }
              descBuilder += projectLine.replaceAll("(.+?)(</projectDescription>)", "$1");
            } else {
              descBuilder =
                  projectLine
                      .replaceAll("(?i)(.*<projectDescription.*?>)(.+?)(</projectDescription>)",
                                  "$2");
            }
            newProject.setProjectDescription(descBuilder);
          }
          // Loads list of AgileHistory items
          if (projectLine.startsWith("\t\t<AllocatedTeams>")) {
            while (!(projectLine = loadedFile.readLine()).startsWith("\t\t</AllocatedTeams>")) {
              if (projectLine.startsWith("\t\t\t<allocatedTeam>")) {
                teamHistoryItem = new AgileHistory();
                tempTeam = new Team();

                projectLine = loadedFile.readLine();
                projectData =
                    projectLine.replaceAll("(?i)(.*<agileTeam.*?>)(.+?)(</agileTeam>)", "$2");
                tempTeam.setLabel(projectData);
                teamHistoryItem.setAgileItem(tempTeam);
                projectLine = loadedFile.readLine();
                projectData =
                    projectLine.replaceAll("(?i)(.*<startDate.*?>)(.+?)(</startDate>)", "$2");
                startDate = LocalDate.of(Integer.parseInt(projectData.substring(0, 4)),
                                         Integer.parseInt(projectData.substring(5, 7)),
                                         Integer.parseInt(projectData.substring(8, 10)));
                teamHistoryItem.setStartDate(startDate);
                projectLine = loadedFile.readLine();
                projectData = projectLine.replaceAll("(?i)(.*<endDate.*?>)(.+?)(</endDate>)", "$2");
                if (projectData.equals("null")) {
                  endDate = null;
                } else {
                  endDate = LocalDate.of(Integer.parseInt(projectData.substring(0, 4)),
                                         Integer.parseInt(projectData.substring(5, 7)),
                                         Integer.parseInt(projectData.substring(8, 10)));
                }
                teamHistoryItem.setEndDate(endDate);

                newProject.addTeam(teamHistoryItem);
              }
            }
          }
          // As of version 0.4, load a backlog
          if (projectLine.startsWith("\t\t<projectBacklog>")) {
            projectData =
                projectLine.replaceAll("(?i)(.*<projectBacklog.*?>)(.+?)(</projectBacklog>)", "$2");
            tempBacklog = new Backlog();
            tempBacklog.setLabel(projectData);
            newProject.setBacklog(tempBacklog);
          }
        }
        // Add the loaded project into main
        main.addProject(newProject);
      }
    }
  }

  /**
   * Loads People from xml files into main app
   *
   * @throws Exception Problem with loading
   */
  private void loadPeople() throws Exception {
    String personLine;
    String personData;
    Person newPerson;
    ObservableList<Skill> skills;

    // Untill People end tag
    while ((!(personLine = loadedFile.readLine()).equals("</People>"))) {
      // On new Person tag
      if (personLine.matches(".*<Person>")) {
        // Required initializers
        newPerson = new Person();
        skills = FXCollections.observableArrayList();
        Skill tempSkill;

        // Mandatory data
        personLine = loadedFile.readLine();
        personData = personLine.replaceAll("(?i)(.*<personLabel.*?>)(.+?)(</personLabel>)", "$2");
        newPerson.setLabel(personData);

        // Optional data
        while ((!(personLine = loadedFile.readLine()).equals("\t</Person>"))) {
          if (personLine.startsWith("\t\t<firstName>")) {
            personData = personLine.replaceAll("(?i)(.*<firstName.*?>)(.+?)(</firstName>)", "$2");
            newPerson.setFirstName(personData);
          }
          if (personLine.startsWith("\t\t<lastName>")) {
            personData = personLine.replaceAll("(?i)(.*<lastName.*?>)(.+?)(</lastName>)", "$2");
            newPerson.setLastName(personData);
          }
          if (personLine.startsWith("\t\t<PersonSkills>")) {
            while ((!(personLine = loadedFile.readLine()).equals("\t\t</PersonSkills>"))) {
              if (personLine.startsWith("\t\t\t<PersonSkill>")) {
                tempSkill = new Skill();
                personData =
                    personLine.replaceAll("(?i)(.*<PersonSkill.*?>)(.+?)(</PersonSkill>)", "$2");
                tempSkill.setLabel(personData);
                skills.add(tempSkill);
              }
            }
            if (skills.size() != 0) {
              newPerson.setSkillSet(skills);
            }
          }
        }
        main.addPerson(newPerson);
      }
    }
  }

  /**
   * Loads Skills from xml files into main app and updates skill references inside people objects
   *
   * @throws Exception Problem with loading
   */
  private void loadSkills() throws Exception {
    String skillLine;
    String skillData;

    // Untill skill end tag
    while ((!(skillLine = loadedFile.readLine()).equals("</Skills>"))) {
      // For each new skill
      if (skillLine.matches(".*<Skill>")) {
        Skill newSkill = new Skill();

        // Mandatory data
        skillLine = loadedFile.readLine();
        skillData = skillLine.replaceAll("(?i)(.*<skillLabel.*?>)(.+?)(</skillLabel>)", "$2");
        newSkill.setLabel(skillData);

        // Non mandatory data
        while ((!(skillLine = loadedFile.readLine()).equals("\t</Skill>"))) {
          if (skillLine.startsWith("\t\t<skillDescription>")) {
            String descBuilder;
            if (!skillLine.endsWith("</skillDescription>")) {
              descBuilder = skillLine
                                .replaceAll("(?i)(.*<skillDescription.*?>)(.+?)", "$2") + "\n";
              while ((!(skillLine = loadedFile.readLine()).endsWith("</skillDescription>"))) {
                descBuilder += skillLine + "\n";
              }
              descBuilder += skillLine.replaceAll("(.+?)(</skillDescription>)", "$1");
            } else {
              descBuilder =
                  skillLine
                      .replaceAll("(?i)(.*<skillDescription.*?>)(.+?)(</skillDescription>)", "$2");
            }
            newSkill.setSkillDescription(descBuilder);
          }
        }
        main.addSkill(newSkill);
      }
    }

    // Now sync Skills and people skills
    for (Person person : main.getPeople()) {
      ArrayList<Skill> skillArray = new ArrayList<>();
      // For every skill in that person
      for (Skill personSkill : person.getSkillSet()) {
        // For every skill in main app
        for (Skill mainSkill : main.getSkills()) {
          if (mainSkill.getLabel().equals(personSkill.getLabel())) {
            // Remove loaded skill object
            skillArray.add(mainSkill);
          }
        }
      }
      // To fix Concurrent Modification Exception
      person.getSkillSet().clear();
      for (Skill arraySkill : skillArray) {
        person.getSkillSet().add(arraySkill);
      }
    }
  }

  /**
   * Loads teams from xml files into main app, and updates references inside people objects to the
   * newly loaded teams
   *
   * @throws Exception Problem with loading
   */
  private void loadTeams() throws Exception {
    String teamLine;
    String teamData;
    Team newTeam;

    // Untill Team end tag
    while (!(teamLine = loadedFile.readLine()).startsWith("</Teams>")) {
      // For each new Team
      if (teamLine.matches(".*<Team>")) {
        // New team loading
        newTeam = new Team();

        // Mandatory fields
        teamLine = loadedFile.readLine();
        teamData = teamLine.replaceAll("(?i)(.*<teamLabel.*?>)(.+?)(</teamLabel>)", "$2");
        newTeam.setLabel(teamData);

        // Non mandatory fields
        while (!(teamLine = loadedFile.readLine()).equals("\t</Team>")) {
          if (teamLine.startsWith("\t\t<teamDescription>")) {
            String descBuilder;
            if (!teamLine.endsWith("</teamDescription>")) {
              descBuilder = teamLine
                                .replaceAll("(?i)(.*<teamDescription.*?>)(.+?)", "$2") + "\n";
              while ((!(teamLine = loadedFile.readLine()).endsWith("</teamDescription>"))) {
                descBuilder += teamLine + "\n";
              }
              descBuilder += teamLine.replaceAll("(.+?)(</teamDescription>)", "$1");
            } else {
              descBuilder =
                  teamLine
                      .replaceAll("(?i)(.*<teamDescription.*?>)(.+?)(</teamDescription>)", "$2");
            }
            newTeam.setTeamDescription(descBuilder);
          }
          // Going through teams
          if (teamLine.startsWith("\t\t<TeamPeople>")) {
            loadTeamMembers(newTeam);
          }
        }
        main.addTeam(newTeam);
      }
    }
  }

  /**
   * Loads team members for a team object.
   *
   * @param newTeam Team currently being loaded
   * @throws Exception Problem with loading
   */
  private void loadTeamMembers(Team newTeam) throws Exception {
    // Definers
    String teamLine;
    String teamData;
    Person tempPerson;
    Role tempRole;
    ObservableList<Person> people = FXCollections.observableArrayList();
    HashMap<Person, Role> roles = new HashMap<>();

    // Untill end tag team people
    while (!(teamLine = loadedFile.readLine()).equals("\t\t</TeamPeople>")) {
      // For each new person
      if (teamLine.startsWith("\t\t\t<TeamMember>")) {
        tempPerson = new Person();
        tempRole = null;
        while (!(teamLine = loadedFile.readLine()).equals("\t\t\t</TeamMember>")) {
          if (teamLine.startsWith("\t\t\t\t<teamPersonLabel>")) {
            teamData =
                teamLine.replaceAll("(?i)(.*<teamPersonLabel.*?>)(.+?)(</teamPersonLabel>)", "$2");
            for (Person person : main.getPeople()) {
              if (person.getLabel().equals(teamData)) {
                people.add(person);
                tempPerson = person;
                break;
              }
            }
          }
          if (teamLine.startsWith("\t\t\t\t<personRole>")) {
            tempRole = new Role();
            teamData = teamLine.replaceAll("(?i)(.*<personRole.*?>)(.+?)(</personRole>)", "$2");
            tempRole.setLabel(teamData);
          }
        }
        roles.put(tempPerson, tempRole);
      }
    }
    if (people.size() != 0) {
      newTeam.setTeamMembers(people);
      newTeam.setMembersRole(roles);
      for (Person person : people) {
        person.assignToTeam(newTeam);
      }
    }
  }

  /**
   * Loads releases from xml files into main app
   * Note: As of version 0.6, required fields and their load orders have changed, code within
   * the if branch is for loading older releases, else for newer one. May cause problems with
   * people forgetting to edit both on some important hard-coded additions later.
   *
   * @throws Exception Problem with loading
   */
  private void loadReleases() throws Exception {
    String releaseLine;
    String releaseData;
    Release newRelease;
    LocalDate releaseDate;

    // Untill Releases end tag
    while (!(releaseLine = loadedFile.readLine()).startsWith("</Releases>")) {
      if (saveVersion <= 0.5) {
        // For each new release
        if (releaseLine.matches(".*<Release>")) {
          newRelease = new Release();

          releaseLine = loadedFile.readLine();
          releaseData =
              releaseLine.replaceAll("(?i)(.*<releaseLabel.*?>)(.+?)(</releaseLabel>)", "$2");
          newRelease.setLabel(releaseData);
          releaseLine = loadedFile.readLine();
          String descBuilder;
          if (!releaseLine.endsWith("</releaseDescription>")) {
            descBuilder = releaseLine
                              .replaceAll("(?i)(.*<releaseDescription.*?>)(.+?)", "$2") + "\n";
            while ((!(releaseLine = loadedFile.readLine()).endsWith("</releaseDescription>"))) {
              descBuilder += releaseLine + "\n";
            }
            descBuilder += releaseLine.replaceAll("(.+?)(</releaseDescription>)", "$1");
          } else {
            descBuilder =
                releaseLine
                    .replaceAll("(?i)(.*<releaseDescription.*?>)(.+?)(</releaseDescription>)",
                                "$2");
          }
          newRelease.setReleaseDescription(descBuilder);
          releaseLine = loadedFile.readLine();
          if (!releaseLine.endsWith("</releaseNotes>")) {
            descBuilder = releaseLine
                              .replaceAll("(?i)(.*<releaseNotes.*?>)(.+?)", "$2") + "\n";
            while ((!(releaseLine = loadedFile.readLine()).endsWith("</releaseNotes>"))) {
              descBuilder += releaseLine + "\n";
            }
            descBuilder += releaseLine.replaceAll("(.+?)(</releaseNotes>)", "$1");
          } else {
            descBuilder =
                releaseLine
                    .replaceAll("(?i)(.*<releaseNotes.*?>)(.+?)(</releaseNotes>)", "$2");
          }
          newRelease.setReleaseNotes(descBuilder);
          releaseLine = loadedFile.readLine();
          releaseData = releaseLine.replaceAll("(?i)(.*<releaseProject>)(.+?)(</releaseProject>)",
                                               "$2");
          // Get correct project from main for concurrency
          for (Project project : main.getProjects()) {
            if (project.getLabel().equals(releaseData)) {
              newRelease.setProjectRelease(project);
            }
          }
          releaseLine = loadedFile.readLine();
          releaseData = releaseLine.replaceAll("(?i)(.*<releaseDate>)(.+?)(</releaseDate>)", "$2");
          releaseDate = LocalDate.of(Integer.parseInt(releaseData.substring(0, 4)),
                                     Integer.parseInt(releaseData.substring(5, 7)),
                                     Integer.parseInt(releaseData.substring(8, 10)));
          newRelease.setReleaseDate(releaseDate);

          main.addRelease(newRelease);
        }
      } else {
        // ###############################################################
        // For each new release
        if (releaseLine.matches(".*<Release>")) {
          newRelease = new Release();

          releaseLine = loadedFile.readLine();
          releaseData =
              releaseLine.replaceAll("(?i)(.*<releaseLabel.*?>)(.+?)(</releaseLabel>)", "$2");
          newRelease.setLabel(releaseData);
          releaseLine = loadedFile.readLine();
          releaseData = releaseLine.replaceAll("(?i)(.*<releaseProject>)(.+?)(</releaseProject>)",
                                               "$2");
          // Get correct project from main for concurrency
          for (Project project : main.getProjects()) {
            if (project.getLabel().equals(releaseData)) {
              newRelease.setProjectRelease(project);
            }
          }
          releaseLine = loadedFile.readLine();
          releaseData = releaseLine.replaceAll("(?i)(.*<releaseDate>)(.+?)(</releaseDate>)", "$2");
          releaseDate = LocalDate.of(Integer.parseInt(releaseData.substring(0, 4)),
                                     Integer.parseInt(releaseData.substring(5, 7)),
                                     Integer.parseInt(releaseData.substring(8, 10)));
          newRelease.setReleaseDate(releaseDate);

          //Optional Fields
          while ((!(releaseLine = loadedFile.readLine()).matches(".*</Release>"))) {
            if (releaseLine.startsWith("\t\t<releaseDescription>")) {
              String descBuilder;
              if (!releaseLine.endsWith("</releaseDescription>")) {
                descBuilder = releaseLine
                                  .replaceAll("(?i)(.*<releaseDescription.*?>)(.+?)", "$2") + "\n";
                while ((!(releaseLine = loadedFile.readLine()).endsWith("</releaseDescription>"))) {
                  descBuilder += releaseLine + "\n";
                }
                descBuilder += releaseLine.replaceAll("(.+?)(</releaseDescription>)", "$1");
              } else {
                descBuilder =
                    releaseLine
                        .replaceAll("(?i)(.*<releaseDescription.*?>)(.+?)(</releaseDescription>)",
                                    "$2");
              }
              newRelease.setReleaseDescription(descBuilder);
            }

            if (releaseLine.startsWith("\t\t<releaseNotes>")) {
              String descBuilder;
              if (!releaseLine.endsWith("</releaseNotes>")) {
                descBuilder = releaseLine
                                  .replaceAll("(?i)(.*<releaseNotes.*?>)(.+?)", "$2") + "\n";
                while ((!(releaseLine = loadedFile.readLine()).endsWith("</releaseNotes>"))) {
                  descBuilder += releaseLine + "\n";
                }
                descBuilder += releaseLine.replaceAll("(.+?)(</releaseNotes>)", "$1");
              } else {
                descBuilder =
                    releaseLine
                        .replaceAll("(?i)(.*<releaseNotes.*?>)(.+?)(</releaseNotes>)", "$2");
              }
              newRelease.setReleaseNotes(descBuilder);
            }
          }

          main.addRelease(newRelease);
        }
      }
    }
  }

  /**
   * Loads roles from xml files into main app
   *
   * @throws Exception Problem with loading
   */
  private void loadRoles() throws Exception {
    String roleLine;
    String roleData;
    Role newRole;

    // Untill Role end tag
    while (!(roleLine = loadedFile.readLine()).startsWith("</Roles>")) {
      // For each new role
      if (roleLine.matches(".*<Role>")) {
        newRole = new Role();

        // Mandatory fields
        roleLine = loadedFile.readLine();
        roleData = roleLine.replaceAll("(?i)(.*<roleLabel.*?>)(.+?)(</roleLabel>)", "$2");
        newRole.setLabel(roleData);
        roleLine = loadedFile.readLine();
        roleData = roleLine.replaceAll("(?i)(.*<roleName.*?>)(.+?)(</roleName>)", "$2");
        newRole.setRoleName(roleData);

        // Non Mandatory fields
        while ((!(roleLine = loadedFile.readLine()).matches(".*</Role>"))) {
          if (roleLine.startsWith("\t\t<roleSkill>")) {
            roleData = roleLine.replaceAll("(?i)(.*<roleSkill.*?>)(.+?)(</roleSkill>)", "$2");
            for (Skill skill : main.getSkills()) {
              if (skill.getLabel().equals(roleData)) {
                newRole.setRequiredSkill(skill);
                main.getNonRemovable().add(skill); // Skill is non-removable if in use by role
              }
            }
          }
          if (roleLine.startsWith("\t\t<memberLimit>")) {
            roleData = roleLine.replaceAll("(?i)(.*<memberLimit.*?>)(.+?)(</memberLimit>)", "$2");
            newRole.setMemberLimit(Integer.parseInt(roleData));
          }
        }
        main.addRole(newRole);
      }
    }
  }

  /**
   * Loads backlogs from xml files into main app and sync with stories.
   *
   * @throws Exception Problem with loading
   */
  private void loadBacklogs() throws Exception {
    String backlogLine;
    String backlogData;
    String storySize;
    Backlog newBacklog;

    // Until Backlog end tag
    while ((!(backlogLine = loadedFile.readLine()).equals("</Backlogs>"))) {
      // On new Backlog tag
      if (backlogLine.matches(".*<Backlog>")) {
        // Required initializers
        newBacklog = new Backlog();

        // Mandatory data
        backlogLine = loadedFile.readLine();
        backlogData = backlogLine.replaceAll("(?i)(.*<backlogLabel.*?>)(.+?)(</backlogLabel>)",
                                             "$2");
        newBacklog.setLabel(backlogData);
        backlogLine = loadedFile.readLine();
        backlogData =
            backlogLine.replaceAll("(?i)(.*<productOwner.*?>)(.+?)(</productOwner>)", "$2");
        // Syncs with current people objects
        for (Person person : main.getPeople()) {
          if (person.getLabel().equals(backlogData)) {
            newBacklog.setProductOwner(person);
            break;
          }
        }

        // Optional data
        while ((!(backlogLine = loadedFile.readLine()).equals("\t</Backlog>"))) {
          if (backlogLine.startsWith("\t\t<backlogName>")) {
            backlogData =
                backlogLine.replaceAll("(?i)(.*<backlogName.*?>)(.+?)(</backlogName>)", "$2");
            newBacklog.setBacklogName(backlogData);
          }
          if (backlogLine.startsWith("\t\t<backlogDescription>")) {
            String descBuilder;
            if (!backlogLine.endsWith("</backlogDescription>")) {
              descBuilder = backlogLine
                                .replaceAll("(?i)(.*<backlogDescription.*?>)(.+?)", "$2") + "\n";
              while ((!(backlogLine = loadedFile.readLine()).endsWith("</backlogDescription>"))) {
                descBuilder += backlogLine + "\n";
              }
              descBuilder += backlogLine.replaceAll("(.+?)(</backlogDescription>)", "$1");
            } else {
              descBuilder =
                  backlogLine
                      .replaceAll("(?i)(.*<backlogDescription.*?>)(.+?)(</backlogDescription>)",
                                  "$2");
            }
            newBacklog.setBacklogDescription(descBuilder);
          }
          // Loads in backlog stories
          if (backlogLine.startsWith("\t\t<BacklogStories>")) {
            while ((!(backlogLine = loadedFile.readLine()).equals("\t\t</BacklogStories>"))) {
              if (backlogLine.startsWith("\t\t\t<backlogStory>")) {
                backlogData =
                    backlogLine.replaceAll("(?i)(.*<backlogStory.*?>)(.+?)(</backlogStory>)", "$2");
                storySize =
                    loadedFile.readLine()
                        .replaceAll("(?i)(.*<storySize.*?>)(.+?)(</storySize>)", "$2");
                // Sync stories and backlog
                for (Story story : main.getStories()) {
                  if (story.getLabel().equals(backlogData)) {
                    newBacklog.addStory(story, Integer.parseInt(storySize));
                    break;
                  }
                }
              }
            }
          }

          if (backlogLine.startsWith("\t\t<backlogEstimate>")) {
            backlogData =
                backlogLine
                    .replaceAll("(?i)(.*<backlogEstimate.*?>)(.+?)(</backlogEstimate>)", "$2");
            for (Estimate estimate : main.getEstimates()) {
              if (backlogData.equals(estimate.getLabel())) {
                newBacklog.setEstimate(estimate);
                break;
              }
            }
          }
        }
        main.addBacklog(newBacklog);
      }
    }
    syncProjectsAndBacklogs();
  }

  /**
   * Syncs temporary teams inside agile history items in projects with the real team objects
   */
  private void syncTeamAllocation() {
    for (Project project : main.getProjects()) {
      // For every AgileHistory in the project
      for (AgileHistory teamHistory : project.getAllocatedTeams()) {
        // For every Team that is in Main App
        for (Team team : main.getTeams()) {
          Team historyTeam = (Team) teamHistory.getAgileItem();
          if (team.getLabel().equals(historyTeam.getLabel())) {
            teamHistory.setAgileItem(team);
          }
        }
      }
    }
  }

  /**
   * Syncs the map of Person,Role in teams with roles from main app
   */
  private void syncRoles() {
    Role tempRole;
    for (Team team : main.getTeams()) {
      for (Person person : team.getTeamMembers()) {
        tempRole = team.getMembersRole().get(person);
        if (tempRole != null) {
          for (Role role : main.getRoles()) {
            if (tempRole.getLabel().equals(role.getLabel())) {
              team.getMembersRole().put(person, role);
              break;
            }
          }
        }
      }
    }
  }

  /**
   * Loads stories from xml into main app
   *
   * @throws Exception things went wrong in loading. Badly formatted
   */
  private void loadStories() throws Exception {
    Story newStory;
    String storyLine;
    String storyData;
    Map<String, Person> taskPersonMap = new HashMap<>();
    for (Person person : main.getPeople()) {
      taskPersonMap.put(person.getLabel(), person);
    }

    // Until stories end tag
    while (!(storyLine = loadedFile.readLine()).startsWith("</Stories>")) {
      // For each new story
      if (storyLine.matches(".*<Story>")) {
        newStory = new Story();
        newStory.setStatus(Status.NOT_STARTED);

        // Mandatory fields
        storyLine = loadedFile.readLine();
        storyData = storyLine.replaceAll("(?i)(.*<storyLabel.*?>)(.+?)(</storyLabel>)", "$2");
        newStory.setLabel(storyData);
        storyLine = loadedFile.readLine();
        storyData = storyLine.replaceAll("(?i)(.*<creator.*?>)(.+?)(</creator>)", "$2");
        // Syncs with current people objects
        for (Person person : main.getPeople()) {
          if (person.getLabel().equals(storyData)) {
            newStory.setCreator(person);
            break;
          }
        }
        if (saveVersion >= 0.4) {
          storyLine = loadedFile.readLine();
          storyData = storyLine.replaceAll("(?i)(.*<readiness.*?>)(.+?)(</readiness>)", "$2");
          if (storyData.equals("true")) {
            newStory.setStoryState(true);
          } else {
            newStory.setStoryState(false);
          }
        }
        if (saveVersion >= 0.5) {
          storyLine = loadedFile.readLine();
          storyData = storyLine.replaceAll("(?i)(.*<status.*?>)(.+?)(</status>)", "$2");
          newStory.setStatus(Status.getStatusEnum(storyData));
        }

        // Non-mandatory fields
        while ((!(storyLine = loadedFile.readLine()).matches(".*</Story>"))) {
          if (saveVersion >= 0.5) {
            if (storyLine.startsWith("\t\t<impediments>")) {
              storyData =
                  storyLine.replaceAll("(?i)(.*<impediments.*?>)(.+?)(</impediments>)", "$2");
              newStory.setImpediments(storyData);
            }
          }
          if (saveVersion >= 0.5) {
            if (storyLine.startsWith("\t\t<Tasks>")) {
              while ((!(storyLine = loadedFile.readLine()).equals("\t\t</Tasks>"))) {
                Task storyTask;
                if (storyLine.startsWith("\t\t\t<Task>")) {
                  storyTask = loadATask(loadedFile, taskPersonMap);
                  if (storyTask != null) {
                    newStory.addTask(storyTask);
                  }
                }
              }
            }
          }
          if (storyLine.startsWith("\t\t<longName>")) {
            storyData = storyLine.replaceAll("(?i)(.*<longName.*?>)(.+?)(</longName>)", "$2");
            newStory.setStoryName(storyData);
          }
          if (storyLine.startsWith("\t\t<description>")) {
            String descBuilder;
            if (!storyLine.endsWith("</description>")) {
              descBuilder = storyLine
                                .replaceAll("(?i)(.*<description.*?>)(.+?)", "$2") + "\n";
              while ((!(storyLine = loadedFile.readLine()).endsWith("</description>"))) {
                descBuilder += storyLine + "\n";
              }
              descBuilder += storyLine.replaceAll("(.+?)(</description>)", "$1");
            } else {
              descBuilder =
                  storyLine
                      .replaceAll("(?i)(.*<description.*?>)(.+?)(</description>)", "$2");
            }
            newStory.setDescription(descBuilder);
          }
          if (storyLine.startsWith("\t\t<AcceptanceCriteria>")) {
            ObservableList<String> newACs = FXCollections.observableArrayList();
            while ((!(storyLine = loadedFile.readLine()).equals("\t\t</AcceptanceCriteria>"))) {
              if (storyLine.startsWith("\t\t\t<criteria>")) {
                String acceptanceBuilder;
                if (!storyLine.endsWith("</criteria>")) {
                  acceptanceBuilder = storyLine
                                          .replaceAll("(?i)(.*<criteria.*?>)(.+?)", "$2") + "\n";
                  while ((!(storyLine = loadedFile.readLine()).endsWith("</criteria>"))) {
                    acceptanceBuilder += storyLine + "\n";
                  }
                  acceptanceBuilder += storyLine.replaceAll("(.+?)(</criteria>)", "$1");
                } else {
                  acceptanceBuilder =
                      storyLine
                          .replaceAll("(?i)(.*<criteria.*?>)(.+?)(</criteria>)", "$2");
                }
                newACs.add(acceptanceBuilder);
              }
            }
            newStory.setAcceptanceCriteria(newACs);
          }
          if (saveVersion >= 0.4) {
            if (storyLine.startsWith("\t\t<Dependencies>")) {
              while ((!(storyLine = loadedFile.readLine()).equals("\t\t</Dependencies>"))) {
                if (storyLine.startsWith("\t\t\t<dependent>")) {
                  storyData =
                      storyLine.replaceAll("(?i)(.*<dependent.*?>)(.+?)(</dependent>)", "$2");
                  Story tempS = new Story();
                  tempS.setLabel(storyData);
                  newStory.addDependency(tempS);
                }
              }
            }
          }
        }
        main.addStory(newStory);
      }
    }
    syncStoriesDependencies();
  }

  /**
   * Loads estimates from xml into app. Loads the label and the estimate scale. format of: Estimates
   * Estimate estimateLabel Label of estimate /estimateLabel EstimateNames size-0 Not Set /size-0
   * size-1 Start of actual scale /size-1 etc..... /EstimateNames /Estimate /Estimates
   *
   * @throws Exception Something went wrong with reader
   */
  private void loadEstimates() throws Exception {
    Estimate newEstimate;
    String estimateLine;
    String estimateData;
    List<String> estimateNames;

    while (!(estimateLine = loadedFile.readLine()).startsWith("</Estimates>")) {
      if (estimateLine.matches(".*<Estimate>")) {
        newEstimate = new Estimate();

        // Mandatory fields
        estimateLine = loadedFile.readLine();
        estimateData =
            estimateLine.replaceAll("(?i)(.*<estimateLabel.*?>)(.+?)(</estimateLabel>)", "$2");
        newEstimate.setLabel(estimateData);
        estimateNames = new ArrayList<>();
        loadedFile.readLine();
        while (!(estimateLine = loadedFile.readLine()).endsWith("</EstimateNames>")) {
          estimateData =
              estimateLine.replaceAll("(?i)(.*<size-\\d*.*?>)(.+?)(</size-\\d*.*?>)", "$2");
          estimateNames.add(estimateData);
        }
        //If the version is less than 0.5 then add Epic Dino to the Dino scale
        //and 21 to the Fibonacci scale.
        if (saveVersion < 0.5) {
          if (newEstimate.getLabel().equals("Dinos")) {
            estimateNames.add("Epic Dino");
          }
          if (newEstimate.getLabel().equals("Fibonacci")) {
            estimateNames.add(estimateNames.size() - 1, "21");
          }
        }
        newEstimate.setEstimateNames(estimateNames);
        main.addEstimate(newEstimate);
      }
    }
  }

  private void loadSprints() throws Exception {
    Sprint sprint;
    String sprintLine;
    String sprintData;
    LocalDate start;
    LocalDate end;
    Map<String, Person> taskPersonMap = new HashMap<>();
    for (Person person : main.getPeople()) {
      taskPersonMap.put(person.getLabel(), person);
    }

    while (!(sprintLine = loadedFile.readLine()).startsWith("</Sprints>")) {
      if (sprintLine.matches(".*<Sprint>")) {
        sprint = new Sprint();

        //Load label/goal
        sprintLine = loadedFile.readLine();
        sprintData = sprintLine.replaceAll("(?i)(.*<sprintLabel.*?>)(.+?)(</sprintLabel>)", "$2");
        sprint.setLabel(sprintData); //set label
        sprintLine = loadedFile.readLine();
        sprintData = sprintLine.replaceAll("(?i)(.*<sprintTeam.*?>)(.+?)(</sprintTeam>)", "$2");

        for (Team team : main.getTeams()) {
          if (team.getLabel().equals(sprintData)) {
            sprint.setSprintTeam(team); //set team
            break;
          }
        }
        sprintLine = loadedFile.readLine();
        sprintData =
            sprintLine.replaceAll("(?i)(.*<sprintBacklog.*?>)(.+?)(</sprintBacklog>)", "$2");
        for (Backlog backlog : main.getBacklogs()) {
          if (backlog.getLabel().equals(sprintData)) {
            sprint.setSprintBacklog(backlog); //set backlog
            break;
          }
        }
        sprintLine = loadedFile.readLine();
        sprintData =
            sprintLine.replaceAll("(?i)(.*<sprintProject.*?>)(.+?)(</sprintProject>)", "$2");

        for (Project project : main.getProjects()) {
          if (project.getLabel().equals(sprintData)) {
            sprint.setSprintProject(project); //set project
            break;
          }
        }
        sprintLine = loadedFile.readLine();
        sprintData =
            sprintLine.replaceAll("(?i)(.*<sprintRelease.*?>)(.+?)(</sprintRelease>)", "$2");

        for (Release release : main.getReleases()) {
          if (release.getLabel().equals(sprintData)) {
            sprint.setSprintRelease(release); //set release
            break;
          }
        }
        sprintLine = loadedFile.readLine();
        sprintData = sprintLine.replaceAll("(?i)(.*<sprintStart.*?>)(.+?)(</sprintStart>)", "$2");

        start = LocalDate.parse(sprintData);
        sprint.setSprintStart(start); // set start date

        sprintLine = loadedFile.readLine();
        sprintData = sprintLine.replaceAll("(?i)(.*<sprintEnd.*?>)(.+?)(</sprintEnd>)", "$2");
        end = LocalDate.parse(sprintData);

        sprint.setSprintEnd(end); //set end date

        while ((!(sprintLine = loadedFile.readLine()).matches(".*</Sprint>"))) {
          if (sprintLine.startsWith("\t\t<sprintName>")) {
            sprintData = sprintLine.replaceAll("(?i)(.*<sprintName.*?>)(.+?)(</sprintName>)", "$2");

            sprint.setSprintFullName(sprintData);
          }
          if (saveVersion >= 0.5) {
            if (sprintLine.startsWith("\t\t<sprintImpediments>")) {
              sprintData = sprintLine.replaceAll("(?i)(.*<sprintImpediments.*?>)(.+?)"
                                                 + "(</sprintImpediments>)", "$2");
              sprint.setSprintImpediments(sprintData);
            }
          }
          if (saveVersion >= 0.5) {
            if (sprintLine.startsWith("\t\t<Tasks>")) {
              while ((!(sprintLine = loadedFile.readLine()).equals("\t\t</Tasks>"))) {
                Task sprintTask;
                if (sprintLine.startsWith("\t\t\t<Task>")) {
                  sprintTask = loadATask(loadedFile, taskPersonMap);
                  if (sprintTask != null) {
                    sprint.addTask(sprintTask);
                  }
                }
              }
            }
          }
          if (sprintLine.startsWith("\t\t<sprintDescription>")) {
            String descBuilder;
            if (!sprintLine.endsWith("</sprintDescription>")) {
              descBuilder = sprintLine
                                .replaceAll("(?i)(.*<sprintDescription.*?>)(.+?)", "$2") + "\n";
              while ((!(sprintLine = loadedFile.readLine()).endsWith("</sprintDescription>"))) {
                descBuilder += sprintLine + "\n";
              }
              descBuilder += sprintLine.replaceAll("(.+?)(</sprintDescription>)", "$1");
            } else {
              descBuilder =
                  sprintLine
                      .replaceAll("(?i)(.*<sprintDescription.*?>)(.+?)(</sprintDescription>)",
                                  "$2");
            }

            sprint.setSprintDescription(descBuilder);
          }

          if (sprintLine.startsWith("\t\t<sprintStories>")) {
            while ((!(sprintLine = loadedFile.readLine()).equals("\t\t</sprintStories>"))) {
              sprintData = sprintLine.replaceAll("(?i)(.*<Story.*?>)(.+?)(</Story>)", "$2");
              // Sync stories and sprint

              for (Story story : main.getStories()) {
                if (story.getLabel().equals(sprintData)) {
                  sprint.addStory(story);
                  break;

                }
              }
            }
          }
        }

        main.addSprint(sprint);

      }
    }
  }

  /**
   * Loads a single task from the save file. Must be called when BufferedReader object
   * is on the Task tag
   * @param loadedFile The BufferedReader
   * @param taskPersonMap A map of Label to Person for people in mainApp
   * @return a loaded Task
   * @throws Exception Something went wrong
   */
  public Task loadATask(BufferedReader loadedFile, Map<String, Person> taskPersonMap) throws Exception {
    Task storyTask;
    String storyLine;
    String storyData;
    storyTask = new Task();
    storyLine = loadedFile.readLine();
    storyData =
        storyLine.replaceAll("(?i)(.*<taskLabel.*?>)(.+?)(</taskLabel>)", "$2");
    storyTask.setLabel(storyData);
    storyLine = loadedFile.readLine();
    storyData = storyLine.replaceAll(
        "(?i)(.*<taskStatus.*?>)(.+?)(</taskStatus>)", "$2");
    storyTask.setStatus(Status.getStatusEnum(storyData));
    while (!(storyLine = loadedFile.readLine()).startsWith("\t\t\t</Task>")) {
      // Description builder
      if (storyLine.startsWith("\t\t\t\t<taskDescription>")) {
        String descBuilder;
        if (!storyLine.endsWith("</taskDescription>")) {
          descBuilder = storyLine
                            .replaceAll("(?i)(.*<taskDescription.*?>)(.+?)", "$2") + "\n";
          while ((!(storyLine = loadedFile.readLine()).endsWith("</taskDescription>"))) {
            descBuilder += storyLine + "\n";
          }
          descBuilder += storyLine.replaceAll("(.+?)(</taskDescription>)", "$1");
        } else {
          descBuilder =
              storyLine
                  .replaceAll("(?i)(.*<taskDescription.*?>)(.+?)(</taskDescription>)", "$2");
        }
        storyTask.setTaskDescription(descBuilder);
      }
      if (storyLine.startsWith("\t\t\t\t<taskEstimate>")) {
        storyData =
            storyLine.replaceAll("(?i)(.*<taskEstimate.*?>)(.+?)(</taskEstimate>)", "$2");
        storyTask.setTaskEstimation(Integer.parseInt(storyData));
      }
      if (storyLine.startsWith("\t\t\t\t<taskImpediments>")) {
        storyData =
            storyLine.replaceAll("(?i)(.*<taskImpediments.*?>)(.+?)(</taskImpediments>)", "$2");
        storyTask.setImpediments(storyData);
      }
      if (storyLine.startsWith("\t\t\t\t<TaskPeople>")) {
        while ((!(storyLine = loadedFile.readLine()).endsWith("</TaskPeople>"))) {
          storyData = storyLine.replaceAll("(?i)(.*<person.*?>)(.+?)(</person>)", "$2");
          storyTask.addTaskPerson(taskPersonMap.get(storyData));
        }
      }
      //This loads the original "barebones" logging effort that was put in place for a short time
      if (saveVersion == 0.5) {
        if (storyLine.startsWith("\t\t\t\t<TaskEffort>")) {
          String effortData;
          while ((!(storyLine = loadedFile.readLine()).endsWith("</TaskEffort>"))) {
            storyData = storyLine.replaceAll("(?i)(.*<person.*?>)(.+?)(</person>)", "$2");
            storyLine = loadedFile.readLine();
            effortData = storyLine.replaceAll("(?i)(.*<effort.*?>)(.+?)(</effort>)",
                                              "$2");

            Effort tempE = new Effort(taskPersonMap.get(storyData), Integer.parseInt(effortData),
                                      "out of date effort", LocalDateTime.now());

            storyTask.addEffort(tempE);
          }
        }
      } else if (saveVersion >= 0.6) { //This loads the real proper effort that gets logged from V0.6 onwards
        if (storyLine.startsWith("\t\t\t\t<Effort>")) {
          while ((!(storyLine = loadedFile.readLine()).endsWith("</Effort>"))) {
            String effortWorker = storyLine.replaceAll(
                "(?i)(.*<effortWorker.*?>)(.+?)(</effortWorker>)",
                "$2");

            Person worker = null;
            for (Person man : main.getPeople()) {
              if (man.getLabel().equals(effortWorker)) {
                worker = man;
              }
            }

            storyLine = loadedFile.readLine();
            int effortSpent = Integer.parseInt(
                storyLine.replaceAll("(?i)(.*<spentEffort.*?>)(.+?)(</spentEffort>)",
                                     "$2"));

            storyLine = loadedFile.readLine();
            String comment = storyLine.replaceAll("(?i)(.*<comments.*?>)(.+?)(</comments>)",
                                                  "$2");
            storyLine = loadedFile.readLine();

            String dateTime = storyLine.replaceAll("(?i)(.*<dateTime.*?>)(.+?)(</dateTime>)",
                                                   "$2");

            LocalDateTime dt = LocalDateTime.parse(dateTime);

            if (worker != null) {
              Effort effortItem = new Effort(worker, effortSpent, comment, dt);
              storyTask.addEffort(effortItem);
            }
          }
        }
        if (storyLine.startsWith("\t\t\t\t<DoneDate>")) {
          String doneDate = storyLine.replaceAll(
              "(?i)(.*<DoneDate.*?>)(.+?)(</DoneDate>)",
              "$2");
          storyTask.setDoneDate(LocalDate.parse(doneDate));
        }
      }
    }
    return storyTask;
  }


  /**
   * A function to sync the temp backlog and real backlogs
   */
  private void syncProjectsAndBacklogs() {
    Backlog tempBacklog;

    Map<String, Backlog> backlogMap = new HashMap<>();
    for (Backlog backlog : main.getBacklogs()) {
      backlogMap.put(backlog.getLabel(), backlog);
    }

    for (Project project : main.getProjects()) {
      tempBacklog = project.getBacklog();
      if (tempBacklog != null) {
        project.setBacklog(backlogMap.get(tempBacklog.getLabel()));
      }
    }
  }


  /**
   * A function to sync the loaded story and its dependencies in the program
   */
  private void syncStoriesDependencies() {

    Story tempStory;
    Map<String, Story> storyMap = new HashMap<>();
    List<Story> dependents = new ArrayList<>();

    for (Story story : main.getStories()) {
      storyMap.put(story.getLabel(), story);
    }

    for (Story story : main.getStories()) {
      dependents.clear();
      for (Story dependent : story.getDependencies()) {
        tempStory = storyMap.get(dependent.getLabel());
        dependents.add(tempStory);
      }
      story.removeAllDependencies();
      story.addAllDependencies(dependents);
    }
  }

  /**
   * Sync the task people and main app people.
   */
  private void syncTaskPeople() {
    Map<String, Person> personMap = new HashMap<>();
    for (Person person : main.getPeople()) {
      personMap.put(person.getLabel(), person);
    }

    for (Story story : main.getStories()) {
      if (!story.getTasks().isEmpty()) {
        for (Task task : story.getTasks()) {
          if (!task.getTaskPeople().isEmpty()) {
            List<Person> newPersList = new ArrayList<>();
            for (Person person : task.getTaskPeople()) {
              newPersList.add(personMap.get(person.getLabel()));
            }
            task.removeAllTaskPeople();
            task.addAllTaskPeople(newPersList);
          }
        }
      }
    }

    for (Sprint sprint : main.getSprints()) {
      if (!sprint.getTasks().isEmpty()) {
        for (Task task : sprint.getTasks()) {
          if (!task.getTaskPeople().isEmpty()) {
            List<Person> newPersList = new ArrayList<>();
            for (Person person : task.getTaskPeople()) {
              newPersList.add(personMap.get(person.getLabel()));
            }
            task.removeAllTaskPeople();
            task.addAllTaskPeople(newPersList);
          }
        }
      }
    }
  }
}
