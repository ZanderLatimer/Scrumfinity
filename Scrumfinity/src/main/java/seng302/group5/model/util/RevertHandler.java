package seng302.group5.model.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import seng302.group5.model.Story;
import seng302.group5.model.Task;
import seng302.group5.model.Team;

/**
 * Created by @author Alex Woo
 */
public class RevertHandler {

  private Main mainApp;

  private ObservableList<Project> projectsLastSaved;
  private ObservableList<Team> teamsLastSaved;
  private ObservableList<Skill> skillsLastSaved;
  private ObservableList<Person> peopleLastSaved;
  private ObservableList<Release> releasesLastSaved;
  private ObservableList<Role> rolesLastSaved;
  private ObservableList<Story> storiesLastSaved;
  private ObservableList<Backlog> backlogsLastSaved;
  private ObservableList<Estimate> estimatesLastSaved;
  private ObservableList<Sprint> sprintsLastSaved;

  /**
   * Constructor. Set the main app to communicate with and initialise lists
   *
   * @param mainApp Main app to communicate with
   */
  public RevertHandler(Main mainApp) {
    this.mainApp = mainApp;
    this.projectsLastSaved = FXCollections.observableArrayList();
    this.teamsLastSaved = FXCollections.observableArrayList();
    this.skillsLastSaved = FXCollections.observableArrayList();
    this.peopleLastSaved = FXCollections.observableArrayList();
    this.releasesLastSaved = FXCollections.observableArrayList();
    this.rolesLastSaved = FXCollections.observableArrayList();
    this.storiesLastSaved = FXCollections.observableArrayList();
    this.backlogsLastSaved = FXCollections.observableArrayList();
    this.estimatesLastSaved = FXCollections.observableArrayList();
    this.sprintsLastSaved = FXCollections.observableArrayList();
  }


  /**
   * Used when the revert button is pushed. It undoes until it is in the previously saved state. IT
   * then clears the undo and redo stacks
   */
  public void revert() {
    mainApp.resetAll();

    for (Project project : projectsLastSaved) {
      mainApp.addProject(new Project(project));
    }

    for (Team team : teamsLastSaved) {
      mainApp.addTeam(new Team(team));
    }

    for (Person person : peopleLastSaved) {
      mainApp.addPerson(new Person(person));
    }

    for (Skill skill : skillsLastSaved) {
      mainApp.addSkill(new Skill(skill));
    }

    for (Release release : releasesLastSaved) {
      mainApp.addRelease(new Release(release));
    }

    for (Role role : rolesLastSaved) {
      mainApp.addRole(new Role(role));
    }

    for (Story story : storiesLastSaved) {
      Story storyClone = new Story(story);
      List<Task> taskClones = new ArrayList<>();
      for (Task task : story.getTasks()) {
        Task taskClone = new Task(task);
        List<Effort> effortClones = new ArrayList<>();
        for (Effort effort : task.getEfforts()) {
          Effort effortClone = new Effort(effort);
          effortClones.add(effortClone);
        }
        taskClone.removeAllEfforts();
        taskClone.addAllEfforts(effortClones);
        taskClones.add(taskClone);
      }
      storyClone.removeAllTasks();
      storyClone.addAllTasks(taskClones);
      mainApp.addStory(storyClone);
    }

    for (Backlog backlog : backlogsLastSaved) {
      mainApp.addBacklog(new Backlog(backlog));
    }

    for (Estimate estimate : estimatesLastSaved) {
      mainApp.addEstimate(new Estimate(estimate));
    }

    for (Sprint sprint : sprintsLastSaved) {
      Sprint sprintClone = new Sprint(sprint);
      List<Task> taskClones = new ArrayList<>();
      for (Task task : sprint.getTasks()) {
        Task taskClone = new Task(task);
        List<Effort> effortClones = new ArrayList<>();
        for (Effort effort : task.getEfforts()) {
          Effort effortClone = new Effort(effort);
          effortClones.add(effortClone);
        }
        taskClone.removeAllEfforts();
        taskClone.addAllEfforts(effortClones);
        taskClones.add(taskClone);
      }
      sprintClone.removeAllTasks();
      sprintClone.addAllTasks(taskClones);
      mainApp.addSprint(sprintClone);
    }


    // Ensure data in main refer to each other
    syncPeopleWithSkills(mainApp.getPeople(), mainApp.getSkills());
    syncTeamsWithPeople(mainApp.getTeams(), mainApp.getPeople(), mainApp.getRoles());
    syncProjectsWithTeams(mainApp.getProjects(), mainApp.getTeams());
    syncReleasesWithProjects(mainApp.getReleases(), mainApp.getProjects());
    syncRolesWithSkills(mainApp.getRoles(), mainApp.getSkills());
    syncStoriesWithPeople(mainApp.getStories(), mainApp.getPeople());
    syncBacklogsWithStories(mainApp.getBacklogs(), mainApp.getStories());
    syncBacklogsWithEstimates(mainApp.getBacklogs(), mainApp.getEstimates());
    syncProjectsWithBacklogs(mainApp.getProjects(), mainApp.getBacklogs());
    syncStoriesWithDependencies(mainApp.getStories());
    syncSprintsWithBacklogs(mainApp.getSprints(), mainApp.getBacklogs());
    syncSprintsWithProjects(mainApp.getSprints(), mainApp.getProjects());
    syncSprintsWithTeams(mainApp.getSprints(), mainApp.getTeams());
    syncSprintsWithReleases(mainApp.getSprints(), mainApp.getReleases());
    syncSprintsWithStories(mainApp.getSprints(), mainApp.getStories());
    syncSprintTasksWithPeople(mainApp.getSprints(), mainApp.getPeople());
    syncStoriesTasksWithPeople(mainApp.getStories(), mainApp.getPeople());


    mainApp.refreshLastSaved();
    mainApp.refreshList(null);
  }

  /**
   * Makes the last saved lists copy the current state list. used when saving so that a revert maybe
   * done.
   */
  public void setLastSaved() {

    projectsLastSaved.clear();
    for (Project project : mainApp.getProjects()) {
      projectsLastSaved.add(new Project(project));
    }

    teamsLastSaved.clear();
    for (Team team : mainApp.getTeams()) {
      teamsLastSaved.add(new Team(team));
    }

    peopleLastSaved.clear();
    for (Person person : mainApp.getPeople()) {
      peopleLastSaved.add(new Person(person));
    }

    skillsLastSaved.clear();
    for (Skill skill : mainApp.getSkills()) {
      skillsLastSaved.add(new Skill(skill));
    }

    releasesLastSaved.clear();
    for (Release release : mainApp.getReleases()) {
      releasesLastSaved.add(new Release(release));
    }

    rolesLastSaved.clear();
    for (Role role : mainApp.getRoles()) {
      rolesLastSaved.add(new Role(role));
    }

    storiesLastSaved.clear();
    for (Story story : mainApp.getStories()) {
      Story storyClone = new Story(story);
      List<Task> taskClones = new ArrayList<>();
      for (Task task : story.getTasks()) {
        Task taskClone = new Task(task);
        List<Effort> effortClones = new ArrayList<>();
        for (Effort effort : task.getEfforts()) {
          Effort effortClone = new Effort(effort);
          effortClones.add(effortClone);
        }
        taskClone.removeAllEfforts();
        taskClone.addAllEfforts(effortClones);
        taskClones.add(taskClone);
      }
      storyClone.removeAllTasks();
      storyClone.addAllTasks(taskClones);
      storiesLastSaved.add(storyClone);
    }

    backlogsLastSaved.clear();
    for (Backlog backlog : mainApp.getBacklogs()) {
      backlogsLastSaved.add(new Backlog(backlog));
    }

    estimatesLastSaved.clear();
    for (Estimate estimate : mainApp.getEstimates()) {
      estimatesLastSaved.add(new Estimate(estimate));
    }

    sprintsLastSaved.clear();
    for (Sprint sprint : mainApp.getSprints()) {
      Sprint sprintClone = new Sprint(sprint);
      List<Task> taskClones = new ArrayList<>();
      for (Task task : sprint.getTasks()) {
        Task taskClone = new Task(task);
        List<Effort> effortClones = new ArrayList<>();
        for (Effort effort : task.getEfforts()) {
          Effort effortClone = new Effort(effort);
          effortClones.add(effortClone);
        }
        taskClone.removeAllEfforts();
        taskClone.addAllEfforts(effortClones);
        taskClones.add(taskClone);
      }
      sprintClone.removeAllTasks();
      sprintClone.addAllTasks(taskClones);
      sprintsLastSaved.add(sprintClone);
    }

    // Ensure data in the copies refer to each other
    syncPeopleWithSkills(peopleLastSaved, skillsLastSaved);
    syncTeamsWithPeople(teamsLastSaved, peopleLastSaved, rolesLastSaved);
    syncProjectsWithTeams(projectsLastSaved, teamsLastSaved);
    syncReleasesWithProjects(releasesLastSaved, projectsLastSaved);
    syncRolesWithSkills(rolesLastSaved, skillsLastSaved);
    syncStoriesWithPeople(storiesLastSaved, peopleLastSaved);
    syncBacklogsWithStories(backlogsLastSaved, storiesLastSaved);
    syncBacklogsWithEstimates(backlogsLastSaved, estimatesLastSaved);
    syncProjectsWithBacklogs(projectsLastSaved, backlogsLastSaved);
    syncStoriesWithDependencies(storiesLastSaved);
    syncSprintsWithBacklogs(sprintsLastSaved, backlogsLastSaved);
    syncSprintsWithProjects(sprintsLastSaved, projectsLastSaved);
    syncSprintsWithTeams(sprintsLastSaved, teamsLastSaved);
    syncSprintsWithReleases(sprintsLastSaved, releasesLastSaved);
    syncSprintsWithStories(sprintsLastSaved, storiesLastSaved);
    syncSprintTasksWithPeople(sprintsLastSaved, peopleLastSaved);
    syncStoriesTasksWithPeople(storiesLastSaved, peopleLastSaved);
  }

  /**
   * Creates proper object reference between people and skills.
   *
   * @param people Reference Person objects to link together
   * @param skills Reference Skill objects to link together
   */
  private void syncPeopleWithSkills(List<Person> people, List<Skill> skills) {
    Map<String, Skill> skillMap = new HashMap<>();

    for (Skill mainSkill : skills) {
      skillMap.put(mainSkill.getLabel(), mainSkill);
    }

    // For every available person
    for (Person person : people) {
      List<Skill> skillArray = new ArrayList<>();
      // For every skill in that person
      for (Skill personSkill : person.getSkillSet()) {
        // For every skill in main app
        skillArray.add(skillMap.get(personSkill.getLabel()));
      }
      person.getSkillSet().setAll(skillArray);
    }
  }

  /**
   * Creates concurrency between people in main app and people in teams.
   *
   * @param teams  Reference Team objects to link together
   * @param people Reference Person objects to link together
   * @param roles  Reference Role objects to link together
   */
  private void syncTeamsWithPeople(List<Team> teams, List<Person> people, List<Role> roles) {
    Map<String, Person> personMap = new HashMap<>();
    Map<String, Role> roleMap = new HashMap<>();

    for (Person mainPerson : people) {
      personMap.put(mainPerson.getLabel(), mainPerson);
    }
    for (Role mainRole : roles) {
      roleMap.put(mainRole.getLabel(), mainRole);
    }

    // For every available team
    for (Team team : teams) {
      List<Person> personArray = new ArrayList<>();
      Map<Person, Role> personRoleMap = new HashMap<>();
      // For every person in that team
      for (Person teamPerson : team.getTeamMembers()) {
        // For every person that is in Main App
        Person mainPerson = personMap.get(teamPerson.getLabel());
        Role mainRole = null;
        if (team.getMembersRole().get(teamPerson) != null) {
          mainRole = roleMap.get(team.getMembersRole().get(teamPerson).getLabel());
        }
        personArray.add(mainPerson);
        personRoleMap.put(mainPerson, mainRole);
      }
      team.getTeamMembers().clear();
      team.getMembersRole().clear();
      for (Person person : personArray) {
        person.assignToTeam(team);
        team.addTeamMember(person, personRoleMap.get(person));
      }
    }
  }

  /**
   * Creates proper object reference between projects and skills.
   *
   * @param projects Reference Project objects to link together
   * @param teams    Reference Team objects to link together
   */
  private void syncProjectsWithTeams(List<Project> projects, List<Team> teams) {
    Map<String, Team> teamMap = new HashMap<>();

    for (Team mainTeam : teams) {
      teamMap.put(mainTeam.getLabel(), mainTeam);
    }

    // For every available project
    for (Project project : projects) {
      List<AgileHistory> agileHistoryArray = new ArrayList<>();
      // For every team in that project
      for (AgileHistory projectAH : project.getAllocatedTeams()) {
        // For every team that is in Main App
        Team projectTeam = (Team) projectAH.getAgileItem();
        Team mainTeam = teamMap.get(projectTeam.getLabel());

        agileHistoryArray.add(new AgileHistory(mainTeam,
                                               projectAH.getStartDate(),
                                               projectAH.getEndDate()));

      }
      project.getAllocatedTeams().setAll(agileHistoryArray);
    }
  }

  /**
   * Creates proper object reference between releases and projects.
   *
   * @param releases Reference Release objects to link together
   * @param projects Reference Project objects to link together
   */
  private void syncReleasesWithProjects(List<Release> releases, List<Project> projects) {
    Map<String, Project> projectMap = new HashMap<>();

    for (Project mainProject : projects) {
      projectMap.put(mainProject.getLabel(), mainProject);
    }

    // For every available release
    for (Release release : releases) {
      Project mainProject = projectMap.get(release.getProjectRelease().getLabel());
      release.setProjectRelease(mainProject);
    }
  }

  /**
   * Creates proper object reference between roles and required skills.
   *
   * @param roles  Reference Role objects to link together
   * @param skills Reference Skill objects to link together
   */
  private void syncRolesWithSkills(List<Role> roles, List<Skill> skills) {
    Map<String, Skill> skillMap = new HashMap<>();

    for (Skill mainSkill : skills) {
      skillMap.put(mainSkill.getLabel(), mainSkill);
    }

    // Update required skill in existing role objects
    for (Role mainRole : roles) {
      if (mainRole.getRequiredSkill() != null) {
        Skill mainSkill = skillMap.get(mainRole.getRequiredSkill().getLabel());
        mainRole.setRequiredSkill(mainSkill);
        mainApp.getNonRemovable().add(mainSkill); // Skill non-removable if in use by a role
      }
    }
  }

  /**
   * Creates the proper reference to the Person object (Creator) related to the story.
   *
   * @param stories Reference story objects to link to the person
   * @param people  Reference person object to link to the story
   */
  private void syncStoriesWithPeople(List<Story> stories, List<Person> people) {
    Map<String, Person> personMap = new HashMap<>();

    for (Person mainPerson : people) {
      personMap.put(mainPerson.getLabel(), mainPerson);
    }

    for (Story mainStory : stories) {
      if (mainStory.getCreator() != null) {
        Person mainPerson = personMap.get(mainStory.getCreator().getLabel());
        mainStory.setCreator(mainPerson);
      }
    }
  }

  /**
   * Syncs story tasks and people so that references are accurate on revert.
   * @param stories The list of stories that will be synced.
   * @param people The list of people that will be synced with tasks.
   */
  private void syncStoriesTasksWithPeople(List<Story> stories, List<Person> people) {
    Map<String, Person> personMap = new HashMap<>();

    for (Person mainPerson : people) {
      personMap.put(mainPerson.getLabel(), mainPerson);
    }

    for (Story story : stories) {
      for (Task task : story.getTasks()) {
        ArrayList<Person> peopleList = new ArrayList<>();
        for (Person person : task.getTaskPeople()) {
          Person mainPerson = personMap.get(person.getLabel());
          peopleList.add(mainPerson);
        }
        // sync the efforts
        for (Effort effort : task.getEfforts()) {
          Person mainPerson = personMap.get(effort.getWorker().getLabel());
          effort.setWorker(mainPerson);
        }
        task.removeAllTaskPeople();
        task.addAllTaskPeople(peopleList);
      }
    }
  }

  /**
   * Syncs Sprint tasks and people so that references are accurate on revert.
   * @param sprints The list of Sprints that will be synced.
   * @param people The list of people that will be synced with tasks.
   */
  private void syncSprintTasksWithPeople(List<Sprint> sprints, List<Person> people) {
    Map<String, Person> personMap = new HashMap<>();

    for (Person mainPerson : people) {
      personMap.put(mainPerson.getLabel(), mainPerson);
    }

    for (Sprint sprint : sprints) {
      for (Task task : sprint.getTasks()) {
        ArrayList<Person> peopleList = new ArrayList<>();
        for (Person person : task.getTaskPeople()) {
          Person mainPerson = personMap.get(person.getLabel());
          peopleList.add(mainPerson);
        }
        // sync the efforts
        for (Effort effort : task.getEfforts()) {
          Person mainPerson = personMap.get(effort.getWorker().getLabel());
          effort.setWorker(mainPerson);
        }
        task.removeAllTaskPeople();
        task.addAllTaskPeople(peopleList);
      }
    }
  }

  /**
   * Creates the proper reference to the Story objects related to the backlog
   *
   * @param backlogs Reference backlog objects to link the story
   * @param stories  Reference story object to link the backlog
   */
  private void syncBacklogsWithStories(List<Backlog> backlogs, List<Story> stories) {
    Map<String, Story> storyMap = new HashMap<>();

    for (Story mainStory : stories) {
      storyMap.put(mainStory.getLabel(), mainStory);
    }

    // For every available backlog
    for (Backlog backlog : backlogs) {
      List<Story> storyList = new ArrayList<>();
      Map<Story, Integer> storyEstimateMap = new IdentityHashMap<>();
      // For every story in that backlog
      for (Story backlogStory : backlog.getStories()) {
        Story story = storyMap.get(backlogStory.getLabel());
        storyList.add(story);   // important to keep same order to maintain priority
        // get values from old map in backlog
        storyEstimateMap.put(story, backlog.getSizes().get(backlogStory));
      }
      backlog.removeAllStories();
      for (Story story : storyList) {
        backlog.addStory(story, storyEstimateMap.get(story));
      }
    }
  }

  /**
   * Creates the proper reference to the Estimate objects in the Backlog
   *
   * @param backlogs  Reference backlog objects
   * @param estimates Reference estimate objects
   */
  private void syncBacklogsWithEstimates(List<Backlog> backlogs, List<Estimate> estimates) {
    Map<String, Estimate> estimateMap = new HashMap<>();

    for (Estimate mainEstimate : estimates) {
      estimateMap.put(mainEstimate.getLabel(), mainEstimate);
    }

    String estimateLabel;
    for (Backlog backlog : backlogs) {
      estimateLabel = backlog.getEstimate().getLabel();
      backlog.setEstimate(estimateMap.get(estimateLabel));
    }
  }

  /**
   * Creates the proper reference to the Backlog objects in the Projects
   *
   * @param projects Reference project objects
   * @param backlogs Reference backlog objects
   */
  private void syncProjectsWithBacklogs(List<Project> projects, List<Backlog> backlogs) {
    Map<String, Backlog> backlogMap = new HashMap<>();

    for (Backlog mainBacklog : backlogs) {
      backlogMap.put(mainBacklog.getLabel(), mainBacklog);
    }

    String backlogLabel;
    for (Project project : projects) {
      if (project.getBacklog() != null) {
        backlogLabel = project.getBacklog().getLabel();
        project.setBacklog(backlogMap.get(backlogLabel));
      }
    }
  }

  /**
   * Creates proper references between the dependencies stories to the newly reverted stories.
   *
   * @param stories Reference story objects
   */
  private void syncStoriesWithDependencies(List<Story> stories) {
    Map<String, Story> storyMap = new IdentityHashMap<>();

    for (Story mainStory : stories) {
      storyMap.put(mainStory.getLabel(), mainStory);
    }

    List<Story> tempList;
    for (Story story : stories) {
      tempList = new ArrayList<>();
      for (Story depStory : story.getDependencies()) {
        tempList.add(storyMap.get(depStory.getLabel()));
      }
      story.removeAllDependencies();
      story.addAllDependencies(tempList);
    }
  }

  /**
   * Creates proper references between the Backlog inside the Sprint object and mainApp backlog
   *
   * @param sprints List of mainApp sprints
   * @param backlogs List of mainApp backlogs (correct references)
   */
  public void syncSprintsWithBacklogs(List<Sprint> sprints, List<Backlog> backlogs) {
    Map<String, Backlog> backlogMap = new IdentityHashMap<>();

    for (Backlog mainBacklog : backlogs) {
      backlogMap.put(mainBacklog.getLabel(), mainBacklog);
    }

    String backlogLabel;
    for (Sprint sprint : sprints) {
      backlogLabel = sprint.getSprintBacklog().getLabel();
      sprint.setSprintBacklog(backlogMap.get(backlogLabel));
    }
  }


  /**
   * Creates proper references between the Project inside the Sprint object and mainApp Project
   *
   * @param sprints List of mainApp sprints
   * @param projects List of mainApp projects (correct references)
   */
  public void syncSprintsWithProjects(List<Sprint> sprints, List<Project> projects) {
    Map<String, Project> projectMap = new IdentityHashMap<>();

    for (Project mainProject : projects) {
      projectMap.put(mainProject.getLabel(), mainProject);
    }

    String projectLabel;
    for (Sprint sprint : sprints) {
      projectLabel = sprint.getSprintProject().getLabel();
      sprint.setSprintProject(projectMap.get(projectLabel));
    }
  }

  /**
   * Creates proper references between the Team inside the Sprint object and mainApp Team
   *
   * @param sprints List of mainApp sprints
   * @param teams List of mainApp teams (correct references)
   */
  public void syncSprintsWithTeams(List<Sprint> sprints, List<Team> teams) {
    Map<String, Team> teamMap = new IdentityHashMap<>();

    for (Team mainTeam : teams) {
      teamMap.put(mainTeam.getLabel(), mainTeam);
    }

    String teamLabel;
    for (Sprint sprint : sprints) {
      teamLabel = sprint.getSprintTeam().getLabel();
      sprint.setSprintTeam(teamMap.get(teamLabel));
    }
  }

  /**
   * Creates proper references between the Release inside the Sprint object and mainApp Release
   *
   * @param sprints List of mainApp sprints
   * @param releases List of mainApp releases (correct references)
   */
  public void syncSprintsWithReleases(List<Sprint> sprints, List<Release> releases) {
    Map<String, Release> releaseMap = new IdentityHashMap<>();

    for (Release mainRelease : releases) {
      releaseMap.put(mainRelease.getLabel(), mainRelease);
    }

    String releaseLabel;
    for (Sprint sprint : sprints) {
      releaseLabel = sprint.getSprintRelease().getLabel();
      sprint.setSprintRelease(releaseMap.get(releaseLabel));
    }
  }

  /**
   * Creates proper references between the Stories inside the Sprint object and mainApp Stories
   *
   * @param sprints List of mainApp sprints
   * @param stories List of mainApp releases (correct references)
   */
  public void syncSprintsWithStories(List<Sprint> sprints, List<Story> stories) {
    Map<String, Story> storyMap = new IdentityHashMap<>();

    for (Story mainStory : stories) {
      storyMap.put(mainStory.getLabel(), mainStory);
    }

    List<Story> tempList;
    for (Sprint sprint : sprints) {
      tempList = new ArrayList<>();
      for (Story depStory : sprint.getSprintStories()) {
        tempList.add(storyMap.get(depStory.getLabel()));
      }
      sprint.removeAllStories();
      sprint.addAllStories(tempList);
    }
  }
}
