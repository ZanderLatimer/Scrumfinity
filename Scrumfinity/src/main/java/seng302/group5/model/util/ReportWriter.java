package seng302.group5.model.util;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import seng302.group5.Main;
import seng302.group5.model.AgileHistory;
import seng302.group5.model.AgileItem;
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
 * Created by Michael + Craig on 5/5/2015. A class that reads the data from the application and
 * saves it in a human readable format.
 */
public class ReportWriter {

  private Document report;
  private Element rootElement;
  Element projElement;
  Element releasesElement;
  Element teamElement;
  Element membersElement;
  Element skillElement;
  Element orphanTeam;
  Element orphanPeople;
  Element unusedSkills;
  Element allStories;
  Element allBacklogs;
  Element allEstimations;
  Element allSprints;
  LocalDate date;
  String dateFormat = "dd/MM/yyyy";

  ObservableList<Team> orphanTeamsList = FXCollections.observableArrayList();
  ObservableList<Skill> unassignedSkills = FXCollections.observableArrayList();
  ObservableList<Story> unassignedStories = FXCollections.observableArrayList();

  Main mainApp;
  /**
   * Creates a report based on data currently stored in the main application memory. Uses XML
   * format, no pretty print.
   *
   * @param mainApp      The currently opened main application
   * @param saveLocation Where the report is saved to
   */
  public void writeReport(Main mainApp, File saveLocation) {
    this.mainApp = mainApp;
    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      unassignedSkills.setAll(mainApp.getSkills());
      unassignedStories.setAll(mainApp.getStories());
      report = docBuilder.newDocument();
      date = LocalDate.now();
      String datesString = date.format(
          DateTimeFormatter.ofPattern(dateFormat));

      //header = report.createTextNode(headerText);
      rootElement = report.createElement("Header");
      String orgName;
      if (!Settings.organizationName.isEmpty()) {
        orgName = Settings.organizationName;
      } else {
        orgName = "__undefined__";
      }
      rootElement.setAttribute("Label", "Report created on " + datesString + " for " +
                                        orgName);
      report.appendChild(rootElement);

      projElement = report.createElement("Projects");
      rootElement.appendChild(projElement);
      orphanTeamsList.setAll(mainApp.getTeams());
      for (Project project : mainApp.getProjects()) {

        createProject(project, "Project");
      }

      orphanTeam = report.createElement("UnassignedTeams");
      rootElement.appendChild(orphanTeam);
      for (Team team : orphanTeamsList) {
        createOrphanTeam(team, orphanTeam, "OrphanTeam");
      }

      orphanPeople = report.createElement("UnassignedPeople");
      rootElement.appendChild(orphanPeople);
      for (Person person : mainApp.getPeople()) {
        if (!person.isInTeam()) {
          createPerson(person, orphanPeople, "Orphan");
        }
      }

      unusedSkills = report.createElement("UnassignedSkills");
      rootElement.appendChild(unusedSkills);
      for (Skill skill : unassignedSkills) {
        createSkill(skill, unusedSkills);
      }

      allBacklogs = report.createElement("Backlogs");
      rootElement.appendChild(allBacklogs);
      for (Backlog backlog : mainApp.getBacklogs()) {
        createBacklog(backlog, allBacklogs, "Backlog");
      }

      allStories = report.createElement("OrphanStories");
      rootElement.appendChild(allStories);
      for (Story story : unassignedStories) {
        createStory(story, allStories, "OrphanStories");
      }

      allEstimations = report.createElement("Estimations");
      rootElement.appendChild(allEstimations);
      for (Estimate estimate : mainApp.getEstimates()) {
        createEstimate(estimate, allEstimations, "EstimateScale");
      }

      allSprints = report.createElement("Sprints");
      rootElement.appendChild(allSprints);
      for (Sprint sprint : mainApp.getSprints()) {
        createSprint(sprint, allSprints, "Sprint");
      }

      String filename = saveLocation.toString();
      if (!filename.endsWith(".xml")) {
        filename = filename + ".xml";
      }
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(report);
      StreamResult result = new StreamResult(filename);

      transformer.transform(source, result);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates a custom report of reportItems at the level passed.
   * @param mainApp The main application with all data
   * @param saveLocation Place to save the report
   * @param reportItems Items that the report contains
   * @param level The level of report, e.g. Projects or Teams.
   */
  public void writeCustomReport(Main mainApp, File saveLocation, List<AgileItem> reportItems, String level) {
    try {
      this.mainApp = mainApp;
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      report = docBuilder.newDocument();
      date = LocalDate.now();
      String datesString = date.format(
          DateTimeFormatter.ofPattern(dateFormat));

      rootElement = report.createElement("Header");
      String orgName;
      if (!Settings.organizationName.isEmpty()) {
        orgName = Settings.organizationName;
      } else {
        orgName = "__undefined__";
      }
      rootElement.setAttribute("Label", "Report created on " + datesString + " for " +
                                        orgName);
      report.appendChild(rootElement);

      projElement = report.createElement(level);
      rootElement.appendChild(projElement);
      switch (level) {
        case ("Projects"):
          for (AgileItem agileProject : reportItems) {
            createProject((Project) agileProject, "Project");
          }
          break;
        case ("Teams"):
          for (AgileItem agileTeam : reportItems) {
            createOrphanTeam((Team) agileTeam, projElement, "Team");
          }
          break;
        case ("People"):
          for (AgileItem agilePerson : reportItems) {
            createPerson((Person) agilePerson, projElement, "People");
          }
          break;
        case ("Skills"):
          for (AgileItem agileSkill : reportItems) {
            createSkill((Skill) agileSkill, projElement);
          }
          break;
        case ("Releases"):
          for (AgileItem agileRelease : reportItems) {
            createRelease((Release) agileRelease, projElement);
          }
          break;
        case ("Stories"):
          for (AgileItem agileStory : reportItems) {
            createStory((Story) agileStory, projElement, "Story");
          }
          break;
        case ("Backlogs"):
          for (AgileItem agileBacklog : reportItems) {
            createBacklog((Backlog) agileBacklog, projElement, "Backlog");
          }
          break;
        case ("Estimates"):
          for (AgileItem agileEstimate : reportItems) {
            createEstimate((Estimate) agileEstimate, projElement, "Estimate");
          }
          break;
        case ("Sprints"):
          for (AgileItem agileSprint : reportItems) {
            createSprint((Sprint) agileSprint, projElement, "Sprint");
          }
          break;
      }

      String filename = saveLocation.toString();
      if (!filename.endsWith(".xml")) {
        filename = filename + ".xml";
      }
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(report);
      StreamResult result = new StreamResult(filename);

      transformer.transform(source, result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create a Project element that contains the project's information.
   *
   * @param project Project to be reported
   * @param name What to name the element
   */
  private void createProject(Project project, String name) {
    Element projElem = report.createElement(name);
    projElement.appendChild(projElem);
    projElem.setAttribute("label", project.getLabel());

    Element projName = report.createElement("Name");
    if (project.getProjectName() != null && !project.getProjectName().isEmpty()) {
      projName.appendChild(report.createTextNode(project.getProjectName()));
    }
    projElem.appendChild(projName);

    Element projDesc = report.createElement("Description");
    if (project.getProjectDescription() != null && !project.getProjectDescription().isEmpty()) {
      projDesc.appendChild(report.createTextNode(project.getProjectDescription()));
    }
    projElem.appendChild(projDesc);

    Element projBacklog = report.createElement("Backlog");
    if (project.getBacklog() != null) {
      projBacklog.appendChild(report.createTextNode(project.getBacklog().getLabel()));
    }
    projElem.appendChild(projBacklog);

    releasesElement = report.createElement("Releases");
    projElem.appendChild(releasesElement);

    for (Release release : mainApp.getReleases()) {
      if (release.getProjectRelease().getLabel().equals(project.getLabel())) {
        createRelease(release, releasesElement);
      }
    }
    teamElement = report.createElement("Teams");
    projElem.appendChild(teamElement);
    for (AgileHistory team : project.getAllocatedTeams()) {
      if (orphanTeamsList.contains(team.getAgileItem())) {
        orphanTeamsList.remove(team.getAgileItem());
      }
      createTeam(team, teamElement, "Team");
    }


  }

  /**
   * Create the release child element that contains the releases information, formats it under
   * relevant tags as a child of the releaseElement.
   *
   * @param release         the release that information will be displayed for
   * @param releasesElement the parent of the release, i.e. displayed as a child of project or of
   *                        all releases.
   */
  private void createRelease(Release release, Element releasesElement) {
    Element releaseElem = report.createElement("Release");
    releasesElement.appendChild(releaseElem);
    releaseElem.setAttribute("label", release.getLabel());

    Element releaseDesc = report.createElement("Description");
    if (release.getReleaseDescription() != null && !release.getReleaseDescription().isEmpty()) {
      releaseDesc.appendChild(report.createTextNode(release.getReleaseDescription()));
    }
    releaseElem.appendChild(releaseDesc);

    Element releaseNotes = report.createElement("Notes");
    if (release.getReleaseNotes() != null && !release.getReleaseNotes().isEmpty()) {
      releaseNotes.appendChild(report.createTextNode(release.getReleaseNotes()));
    }
    releaseElem.appendChild(releaseNotes);

    String releaseDateString = release.getReleaseDate().format(
        DateTimeFormatter.ofPattern(dateFormat));
    Element releaseDate = report.createElement("ReleaseDate");
    releaseDate.appendChild(report.createTextNode(releaseDateString));
    releaseElem.appendChild(releaseDate);
  }


  /**
   * Create a team child element that contains the teams information
   *
   * @param team the team who's information is to be displayed.
   * @param teamElement element for team info to be attached to
   * @param name what to name the team element.
   */
  private void createTeam(AgileHistory team, Element teamElement, String name) {

    Element teamElem = report.createElement(name);
    teamElement.appendChild(teamElem);
    teamElem.setAttribute("label", team.getAgileItem().getLabel());

    String theString = team.getStartDate().format(
        DateTimeFormatter.ofPattern(dateFormat));
    Element teamStartDate = report.createElement("StartDate");
    teamStartDate.appendChild(report.createTextNode(theString));
    teamElem.appendChild(teamStartDate);

    String endDate;
    if (team.getEndDate() != null) {
      endDate = team.getEndDate().format(
          DateTimeFormatter.ofPattern(dateFormat));
    } else {
      endDate = "No end Date";
    }

    Element teamEndDate = report.createElement("EndDate");
    teamEndDate.appendChild(report.createTextNode(endDate));
    teamElem.appendChild(teamEndDate);

    membersElement = report.createElement("Members");
    teamElem.appendChild(membersElement);
    for (Team listTeam : mainApp.getTeams()) {
      if (team.getAgileItem().getLabel().equals(listTeam.getLabel())) {
        for (Person person : listTeam.getTeamMembers()) {
          createPerson(person, membersElement, "TeamMember");
        }
      }
    }
  }

  /**
   * Create a orphan team element i.e. a team that is not assigned to a project. Will be displayed
   * under the unnasigned teams tag.
   *
   * @param team The team which will be used to collect the list of members to get there info.
   * @param orphanTeam element to be attached to
   * @param name what the team element needs to be named
   */
  private void createOrphanTeam(Team team, Element orphanTeam, String name) {

    Element orphanTeamElem = report.createElement(name);
    orphanTeamElem.setAttribute("label", team.getLabel());
    orphanTeam.appendChild(orphanTeamElem);

    for (Person person : team.getTeamMembers()) {
      Element memberElem = report.createElement("Member");
      orphanTeamElem.appendChild(memberElem);
      memberElem.setAttribute("label", person.getLabel());

      Element teamMemberName = report.createElement("FirstName");
      teamMemberName.appendChild(report.createTextNode(person.getFirstName()));
      memberElem.appendChild(teamMemberName);

      Element teamMemberLastName = report.createElement("LastName");
      teamMemberLastName.appendChild(report.createTextNode(person.getLastName()));
      memberElem.appendChild(teamMemberLastName);

      Role role = team.getMembersRole().get(person);
      Element teamMemberRole = report.createElement("Role");
      if (role != null) {
        teamMemberRole.appendChild(report.createTextNode(role.toString()));
      } else {
        teamMemberRole.appendChild(report.createTextNode("No role"));
      }
      memberElem.appendChild(teamMemberRole);

      skillElement = report.createElement("Skills");
      memberElem.appendChild(skillElement);
      for (Skill skill : person.getSkillSet()) {
        Element skillElem = report.createElement("Skill");
        skillElement.appendChild(skillElem);
        skillElem.setAttribute("label", skill.getLabel());

        Element skillDescription = report.createElement("Description");
        skillDescription.appendChild(report.createTextNode(skill.getSkillDescription()));
        skillElem.appendChild(skillDescription);
      }
    }
  }

  /**
   * Create a skill element that displays the information for a skill, the skill element can be
   * displayed under a person or under the list of all skills tag.
   *
   * @param skill The skill who's information will be displayed.
   * @param allSkills The element to which the data will be attached to
   */
  private void createSkill(Skill skill, Element allSkills) {
    Element skillElem = report.createElement("Skill");
    allSkills.appendChild(skillElem);
    skillElem.setAttribute("label", skill.getLabel());

    Element skillDescription = report.createElement("Description");
    skillDescription.appendChild(report.createTextNode(skill.getSkillDescription()));
    skillElem.appendChild(skillDescription);

  }

  /**
   * Creates a backlog element and displays the backlog information under backlogs tag.
   * This includes the label, name, description, produc owner (Person), Estimate used, and
   * stories contained (Story)
   *
   * @param backlog all the backlogs saved in main
   * @param allBacklogs element to which backlog info is attached to
   * @param name name of the created element
   */
  private void createBacklog(Backlog backlog, Element allBacklogs, String name) {
    Element backlogElem = report.createElement(name);
    backlogElem.setAttribute("label", backlog.getLabel());
    allBacklogs.appendChild(backlogElem);

    Element backlogName = report.createElement("Name");
    backlogName.appendChild(report.createTextNode(backlog.getBacklogName()));
    backlogElem.appendChild(backlogName);

    Element backlogDescription = report.createElement("Description");
    backlogDescription.appendChild(report.createTextNode(backlog.getBacklogDescription()));
    backlogElem.appendChild(backlogDescription);

    createPerson(backlog.getProductOwner(), backlogElem, "ProductOwner");

    Element backlogEstimate = report.createElement("Estimate");
    if (backlog.getEstimate() == null) {
      backlogEstimate.appendChild(report.createTextNode("Not assigned"));
    } else {
      backlogEstimate.appendChild(report.createTextNode(backlog.getEstimate().toString()));
    }
    backlogElem.appendChild(backlogEstimate);

    Element backlogStories = report.createElement("Stories");
    for (Story story : backlog.getStories()) {
      if (unassignedStories.contains(story)) {
        unassignedStories.remove(story);
      }
      createStory(story, backlogStories, "Story");
    }
    backlogElem.appendChild(backlogStories);
  }

  /**
   * Gets all person fields and puts them into the backlogOwner element for creating backlogs
   *
   * @param person       The porduct owner of the backlog
   * @param backlogOwner The element "Product Owner" in the backlog
   * @param typeOfPerson name of the created person element
   */
  private void createPerson(Person person, Element backlogOwner, String typeOfPerson) {
    Element personOwner = report.createElement(typeOfPerson);
    personOwner.setAttribute("label", person.getLabel());
    backlogOwner.appendChild(personOwner);

    Element teamMemberName = report.createElement("FirstName");
    teamMemberName.appendChild(report.createTextNode(person.getFirstName()));
    personOwner.appendChild(teamMemberName);

    Element teamMemberLastName = report.createElement("LastName");
    teamMemberLastName.appendChild(report.createTextNode(person.getLastName()));
    personOwner.appendChild(teamMemberLastName);

    skillElement = report.createElement("Skills");
    personOwner.appendChild(skillElement);
    for (Skill skill : person.getSkillSet()) {
      if (unassignedSkills.contains(skill)) {
        unassignedSkills.remove(skill);
      }
      Element skillElem = report.createElement("Skill");
      skillElement.appendChild(skillElem);
      skillElem.setAttribute("label", skill.getLabel());

      Element skillDescription = report.createElement("Description");
      skillDescription.appendChild(report.createTextNode(skill.getSkillDescription()));
      skillElem.appendChild(skillDescription);
    }
  }

  /**
   * Creates the story elements for the report. Shows the name, description and the creator.
   *
   * @param story        Story to be written
   * @param backlogStory Element for the story element to be attached to
   * @param typeOfStory  String name for story type (BacklogStory, UnassignedStory etc.
   */
  private void createStory(Story story, Element backlogStory, String typeOfStory) {
    Element storyElem = report.createElement(typeOfStory);
    storyElem.setAttribute("label", story.getLabel());
    backlogStory.appendChild(storyElem);

    Element storyName = report.createElement("Name");
    storyName.appendChild(report.createTextNode(story.getStoryName()));
    storyElem.appendChild(storyName);

    Element storyDescription = report.createElement("Description");
    storyDescription.appendChild(report.createTextNode(story.getDescription()));
    storyElem.appendChild(storyDescription);

    Element storyState = report.createElement("StoryState");
    if (story.getStoryState()) {
      storyState.appendChild(report.createTextNode("This story is ready."));
    } else {
      storyState.appendChild(report.createTextNode("This story is not ready."));
    }
    storyElem.appendChild(storyState);

    Element storyCreator = report.createElement("Creator");
    storyCreator.appendChild(report.createTextNode(story.getCreator().getLabel()));
    storyElem.appendChild(storyCreator);

    Element storyImpediments = report.createElement("Impediments");
    storyImpediments.appendChild(report.createTextNode(story.getImpediments()));
    storyElem.appendChild(storyImpediments);

    Element acElements = report.createElement("AcceptanceCriteria");
    storyElem.appendChild(acElements);
    for (String ac : story.getAcceptanceCriteria()) {
      Element acElem = report.createElement("criteria");
      acElem.appendChild(report.createTextNode(ac));
      acElements.appendChild(acElem);
    }

    Element estElement = report.createElement("Estimate-Value");
    String size = "0";
    List<String> estimateNames;
    for (Backlog backlogs : mainApp.getBacklogs()) {
      for (Story backStory : backlogs.getStories()) {
        if (story.equals(backStory)) {
          Map sizes = backlogs.getSizes();
          size = sizes.get(story).toString();
          estimateNames = backlogs.getEstimate().getEstimateNames();
          if (!size.equals("0") || !backlogs.getEstimate().equals("Fibonacci")) {
            size = size + " - " + estimateNames.get(Integer.parseInt(size));
          } else {
            size = estimateNames.get(Integer.parseInt(size));
          }
          break;
        }
      }
    }
    estElement.appendChild(report.createTextNode(size));
    storyElem.appendChild(estElement);

    Element dependenciesElement = report.createElement("Dependencies");
    for (Story dependency : story.getDependencies()) {
      Element depElement = report.createElement("dependency");
      depElement.appendChild(report.createTextNode(dependency.getLabel()));
      dependenciesElement.appendChild(depElement);
    }
    storyElem.appendChild(dependenciesElement);


    //shows the tasks in the story and displays their details
    Element tasksElement = report.createElement("Tasks");
    for (Task task : story.getTasks()){
      Element taskElement = report.createElement("task");
      Element nameEle = report.createElement("name");
      taskElement.appendChild(nameEle);
      nameEle.appendChild(report.createTextNode(task.getLabel()));
      Element descEle = report.createElement("description");
      taskElement.appendChild(descEle);
      descEle.appendChild(report.createTextNode(task.getTaskDescription()));
      Element estiEle = report.createElement("estimation");
      taskElement.appendChild(estiEle);
      estiEle.appendChild(report.createTextNode(task.getTaskEstimation().toString()));
      Element impedEle = report.createElement("impediments");
      taskElement.appendChild(impedEle);
      impedEle.appendChild(report.createTextNode(task.getImpediments()));
      Element statusEle = report.createElement("status");
      taskElement.appendChild(statusEle);
      statusEle.appendChild(report.createTextNode(Status.getStatusString(task.getStatus())));

      Element peopleElem = report.createElement("assigned-people");
      taskElement.appendChild(peopleElem);
      for (Person person : task.getTaskPeople()) {
        Element personElem = report.createElement("assigned-person");
        peopleElem.appendChild(personElem);
        personElem.appendChild(report.createTextNode(person.getLabel()));
      }

      Element spentEffortElem = report.createElement("spent-effort");
      taskElement.appendChild(spentEffortElem);
      for (Effort effort : task.getEfforts()) {
        Element effortElem = report.createElement("effort");
        Element personElem = report.createElement("person");
        personElem.appendChild(report.createTextNode(effort.getWorker().getLabel()));
        effortElem.appendChild(personElem);
        Element timeElem = report.createElement("logged-time");
        timeElem.appendChild(report.createTextNode(TimeFormat.parseDuration(effort.getSpentEffort())));
        effortElem.appendChild(timeElem);
        Element commentElem = report.createElement("comments");
        commentElem.appendChild(report.createTextNode(effort.getComments()));
        effortElem.appendChild(commentElem);
        Element endTimeElem = report.createElement("time-stamp");
        endTimeElem.appendChild(report.createTextNode(effort.getDateTime().toString()));
        effortElem.appendChild(endTimeElem);
        spentEffortElem.appendChild(effortElem);
      }
      tasksElement.appendChild(taskElement);
    }
    storyElem.appendChild(tasksElement);
  }

  /**
   * Creates an estimate element. Reports the label and scales.
   *
   * @param estimate The estimate object to be reported
   * @param estimateElement The element to attach estimate report to
   * @param typeOfEstimate Name of the estimate report tag
   */
  private void createEstimate(Estimate estimate, Element estimateElement, String typeOfEstimate) {
    Element estimateElem = report.createElement(typeOfEstimate);
    estimateElem.setAttribute("label", estimate.getLabel());
    estimateElement.appendChild(estimateElem);

    List<String> estimateKeys = estimate.getEstimateNames();
    for (int i = 0; i < estimateKeys.size(); i++) {
      Element estimateSize = report.createElement("Scale-" + String.valueOf(i));
      estimateSize.appendChild(report.createTextNode(estimateKeys.get(i)));
      estimateElem.appendChild(estimateSize);
    }
  }

  /**
   * Creates a sprint element, with information on all fields.
   *
   * @param sprint Sprint object to be reported
   * @param sprintElement Element to attach sprint report to
   * @param typeOfSprint Name of the sprint report tag
   */
  private void createSprint(Sprint sprint, Element sprintElement, String typeOfSprint) {
    Element sprintElem = report.createElement(typeOfSprint);
    sprintElem.setAttribute("goal", sprint.getLabel());
    sprintElement.appendChild(sprintElem);

    Element sprintDesc = report.createElement("Description");
    sprintDesc.appendChild(report.createTextNode(sprint.getSprintDescription()));
    sprintElem.appendChild(sprintDesc);

    Element sprintFullName = report.createElement("FullName");
    sprintFullName.appendChild(report.createTextNode(sprint.getSprintFullName()));
    sprintElem.appendChild(sprintFullName);

    Element sprintBacklog = report.createElement("Backlog");
    sprintBacklog.appendChild(report.createTextNode(sprint.getSprintBacklog().getLabel()));
    sprintElem.appendChild(sprintBacklog);

    Element sprintProject = report.createElement("Project");
    sprintProject.appendChild(report.createTextNode(sprint.getSprintProject().getLabel()));
    sprintElem.appendChild(sprintProject);

    Element sprintStories = report.createElement("Stories");
    for (Story story : sprint.getSprintStories()) {
      createStory(story, sprintStories, "Story");
    }
    sprintElem.appendChild(sprintStories);

    Team sprintTeam = sprint.getSprintTeam();
    for (AgileHistory hist : sprint.getSprintProject().getAllocatedTeams()) {
      Team histTeam = (Team)hist.getAgileItem();
      if (histTeam.equals(sprintTeam)) {
        createTeam(hist, sprintElem, "SprintTeam");
        break;
      }
    }

    createRelease(sprint.getSprintRelease(), sprintElem);

    Element sprintImpediments = report.createElement("Impediments");
    sprintImpediments.appendChild(report.createTextNode(sprint.getSprintImpediments()));
    sprintElem.appendChild(sprintImpediments);

    Element sprintStart = report.createElement("StartDate");
    String formattedDate = sprint.getSprintStart().format(DateTimeFormatter.ofPattern(dateFormat));
    sprintStart.appendChild(report.createTextNode(formattedDate));
    sprintElem.appendChild(sprintStart);

    Element sprintEnd = report.createElement("StartDate");
    formattedDate = sprint.getSprintEnd().format(DateTimeFormatter.ofPattern(dateFormat));
    sprintEnd.appendChild(report.createTextNode(formattedDate));
    sprintElem.appendChild(sprintEnd);

    //Shows the tasks in the sprint and its details.
    Element tasksElement = report.createElement("Tasks");
    for (Task task : sprint.getTasks()){
      Element taskElement = report.createElement("task");
      Element nameEle = report.createElement("name");
      taskElement.appendChild(nameEle);
      nameEle.appendChild(report.createTextNode(task.getLabel()));
      Element descEle = report.createElement("description");
      taskElement.appendChild(descEle);
      descEle.appendChild(report.createTextNode(task.getTaskDescription()));
      Element estiEle = report.createElement("estimation");
      taskElement.appendChild(estiEle);
      estiEle.appendChild(report.createTextNode(task.getTaskEstimation().toString()));
      Element impedEle = report.createElement("impediments");
      taskElement.appendChild(impedEle);
      impedEle.appendChild(report.createTextNode(task.getImpediments()));
      Element statusEle = report.createElement("status");
      taskElement.appendChild(statusEle);
      statusEle.appendChild(report.createTextNode(Status.getStatusString(task.getStatus())));

      Element peopleElem = report.createElement("assigned-people");
      taskElement.appendChild(peopleElem);
      for (Person person : task.getTaskPeople()) {
        Element personElem = report.createElement("assigned-person");
        peopleElem.appendChild(personElem);
        personElem.appendChild(report.createTextNode(person.getLabel()));
      }

      Element spentEffortElem = report.createElement("spent-effort");
      taskElement.appendChild(spentEffortElem);
      for (Effort effort : task.getEfforts()) {
        Element effortElem = report.createElement("effort");
        Element personElem = report.createElement("person");
        personElem.appendChild(report.createTextNode(effort.getWorker().getLabel()));
        effortElem.appendChild(personElem);
        Element timeElem = report.createElement("logged-time");
        timeElem.appendChild(report.createTextNode(TimeFormat.parseDuration(effort.getSpentEffort())));
        effortElem.appendChild(timeElem);
        Element commentElem = report.createElement("comments");
        commentElem.appendChild(report.createTextNode(effort.getComments()));
        effortElem.appendChild(commentElem);
        Element endTimeElem = report.createElement("time-stamp");
        endTimeElem.appendChild(report.createTextNode(effort.getDateTime().toString()));
        effortElem.appendChild(endTimeElem);
        spentEffortElem.appendChild(effortElem);
      }
      tasksElement.appendChild(taskElement);
    }
    sprintElem.appendChild(tasksElement);
  }
}

