package seng302.group5.model;

import org.junit.Before;
import org.junit.Test;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static org.junit.Assert.*;

/**
 * @author Zander, Liang
 */
public class StoryTest {

  private String storyLabel;
  private String storyLongName;
  private String storyDescription;
  private Person storyCreator;
  private ObservableList<String> storyAC;

  private Story story;
  private Story story2;
  private Story story3;

  @Before
  public void setUp() throws Exception {
    this.storyLabel = "Story";
    this.storyLongName = "An awesome story";
    this.storyDescription = "Once upon a time...";
    this.storyCreator = new Person("John", "John", "Doe", null);
    this.storyAC = FXCollections.observableArrayList();
    storyAC.add("ac1");
    storyAC.add("ac2");

    this.story = new Story(storyLabel, storyLongName, storyDescription, storyCreator, storyAC);
    this.story2 = new Story("Story2", "Dependent1", "Dependent story for testing", storyCreator,
                            storyAC);
    this.story3 = new Story("Story3", "Dependent2", "Another dependent story for testing",
                            storyCreator, storyAC);
  }

  @Test
  public void testStatusExists() throws  Exception {
    Status status = story.getStatus();
    String statusString = story.getStatusString();

    assertEquals(Status.NOT_STARTED, status);
    assertEquals("Not Started", statusString);
  }

  @Test
  public void testStatusChangeable() throws  Exception {
    Status status = story.getStatus();
    String statusString = story.getStatusString();

    assertEquals(Status.NOT_STARTED, status);
    assertEquals("Not Started", statusString);

    story.setStatus(Status.DONE);

    status = story.getStatus();
    statusString = story.getStatusString();

    assertEquals(Status.DONE, status);
    assertEquals("Done", statusString);
  }

  @Test
  public void testACsExist() throws Exception {
    String firstAC = story.getAcceptanceCriteria().get(0);
    String secondAC = story.getAcceptanceCriteria().get(1);
    assertEquals("ac1", firstAC);
    assertEquals("ac2", secondAC);
  }

  @Test
  public void testToString() throws Exception {
    String result = story.toString();
    assertEquals(storyLabel, result);
  }

  @Test
  public void testCopyValues() throws Exception {
    assertEquals(storyLabel, story.getLabel());
    assertEquals(storyLongName, story.getStoryName());
    assertEquals(storyDescription, story.getDescription());
    assertEquals(storyCreator, story.getCreator());
    assertTrue(!storyAC.isEmpty());
    assertEquals(storyAC, story.getAcceptanceCriteria());

    story.setStoryState(true);
    Story clone = new Story();
    clone.copyValues(story);

    assertEquals(storyLabel, clone.getLabel());
    assertEquals(storyLongName, clone.getStoryName());
    assertEquals(storyDescription, clone.getDescription());
    assertEquals(storyCreator, clone.getCreator());
    assertTrue(!clone.getAcceptanceCriteria().isEmpty());
    assertEquals(storyAC, clone.getAcceptanceCriteria());

    assertTrue(story.getStoryState());
    assertTrue(clone.getStoryState());
  }

  @Test
  public void testAddDependency() throws Exception {
    assertTrue(story.getDependencies().isEmpty());

    story.addDependency(story2);

    assertTrue(!story.getDependencies().isEmpty());
    assertEquals(1, story.getDependencies().size());
    assertEquals(story2, story.getDependencies().get(0));

    story.addDependency(story3);

    assertEquals(2, story.getDependencies().size());
    assertEquals(story2, story.getDependencies().get(0));
    assertEquals(story3, story.getDependencies().get(1));

  }

  @Test
  public void testRemoveDependency() throws Exception {
    assertTrue(story.getDependencies().isEmpty());

    story.addDependency(story2);

    assertTrue(!story.getDependencies().isEmpty());
    assertEquals(1, story.getDependencies().size());
    assertEquals(story2, story.getDependencies().get(0));

    story.addDependency(story3);

    assertEquals(2, story.getDependencies().size());
    assertEquals(story2, story.getDependencies().get(0));
    assertEquals(story3, story.getDependencies().get(1));

    story.removeDependency(story2);

    assertEquals(1, story.getDependencies().size());
    assertEquals(story3, story.getDependencies().get(0));

    story.removeDependency(story3);

    assertTrue(story.getDependencies().isEmpty());
  }

  @Test
  public void testAddAllDependencies() throws Exception {
    assertTrue(story.getDependencies().isEmpty());

    ObservableList<Story> stories = FXCollections.observableArrayList();
    stories.add(story2);
    stories.add(story3);
    story.addAllDependencies(stories);

    assertTrue(!story.getDependencies().isEmpty());
    assertEquals(2, story.getDependencies().size());
    assertEquals(story2, story.getDependencies().get(0));
    assertEquals(story3, story.getDependencies().get(1));
  }

  @Test
  public void testRemoveAllDependencies() throws Exception {
    assertTrue(story.getDependencies().isEmpty());

    ObservableList<Story> stories = FXCollections.observableArrayList();
    stories.add(story2);
    stories.add(story3);
    story.addAllDependencies(stories);

    assertTrue(!story.getDependencies().isEmpty());
    assertEquals(2, story.getDependencies().size());
    assertEquals(story2, story.getDependencies().get(0));
    assertEquals(story3, story.getDependencies().get(1));

    story.removeAllDependencies();

    assertTrue(story.getDependencies().isEmpty());
  }
}
