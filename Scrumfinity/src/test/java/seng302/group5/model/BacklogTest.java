package seng302.group5.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static org.junit.Assert.*;

/**
 * Unit tests for the Backlog class
 */
public class BacklogTest {

  private Backlog backlog;
  private String label;
  private String backlogName;
  private String backlogDescription;
  private Person productOwner;
  public List<String> fiboEsts = Arrays.asList("Not Set", "1", "2", "3", "5", "8", "13", "Epic");
  public Estimate fibonacci = new Estimate("Fibonacci", fiboEsts);

  @Before
  public void setUp() throws Exception {
    label = "SENG302 Backlog";
    backlogName = "SENG302 Sample Backlog 2014";
    backlogDescription = "Hello World! This is a description";

    ObservableList<Skill> skills = FXCollections.observableArrayList();
    skills.add(new Skill("Product Owner", "This person can be a product owner"));
    productOwner = new Person("Jason", "Jason", "Smith", skills);

    backlog = new Backlog(label, backlogName, backlogDescription, productOwner, fibonacci);
  }

  @Test
  public void testAddStory() throws Exception {
    assertTrue(backlog.getStories().isEmpty());

    Story story = new Story("the story", "the very story", "description", productOwner, null);

    backlog.addStory(story);

    assertEquals(1, backlog.getStories().size());
    assertEquals(story, backlog.getStories().get(0));
  }

  @Test
  public void testRemoveStory() throws Exception {
    assertTrue(backlog.getStories().isEmpty());

    Story story = new Story("the story", "the very story", "description", productOwner, null);

    backlog.addStory(story);

    assertEquals(1, backlog.getStories().size());
    assertEquals(story, backlog.getStories().get(0));

    backlog.removeStory(story);

    assertTrue(backlog.getStories().isEmpty());
  }

  @Test
  public void testCopyValues() throws Exception {
    Story story = new Story("the story", "the very story", "description", productOwner, null);
    backlog.addStory(story);

    assertEquals(label, backlog.getLabel());
    assertEquals(backlogName, backlog.getBacklogName());
    assertEquals(backlogDescription, backlog.getBacklogDescription());
    assertEquals(productOwner, backlog.getProductOwner());
    assertEquals(1, backlog.getStories().size());
    assertEquals(story, backlog.getStories().get(0));

    Backlog clone = new Backlog();
    clone.copyValues(backlog);

    assertEquals(label, clone.getLabel());
    assertEquals(backlogName, clone.getBacklogName());
    assertEquals(backlogDescription, clone.getBacklogDescription());
    assertEquals(productOwner, clone.getProductOwner());
    assertEquals(1, clone.getStories().size());
    assertEquals(story, clone.getStories().get(0));
  }

  @Test
  public void testSetEstimate() throws Exception {
    List<String> fiboEsts = Arrays.asList("Not Set", "1", "2", "3", "5", "8", "13", "Epic");
    Estimate fibonacci = new Estimate("Fibonacci", fiboEsts);
    backlog = new Backlog();
    backlog.setEstimate(fibonacci);
    assertEquals(backlog.getEstimate(), fibonacci);


  }

  @Test
  public void testToString() throws Exception {
    assertEquals(label, backlog.getLabel());
  }

  @Test
  public void testEquals() throws Exception {
    Story story = new Story("the story", "the very story", "description", productOwner, null);
    backlog.addStory(story);

    Backlog backlog2 = new Backlog(label, backlogName, backlogDescription, productOwner, fibonacci);
    backlog2.addStory(story);

    Backlog backlog3 = new Backlog(backlog);  // Test clone

    assertEquals(backlog, backlog2);
    assertEquals(backlog, backlog3);
  }

  @Test
  public void testHashCode() throws Exception {
    Story story = new Story("the story", "the very story", "description", productOwner, null);
    story.setImpediments("The is a problem!");
    backlog.addStory(story);

    Backlog backlog2 = new Backlog(label, backlogName, backlogDescription, productOwner, fibonacci);
    backlog2.addStory(story);

    Backlog backlog3 = new Backlog(backlog); // Test clone

    assertEquals(backlog.hashCode(), backlog2.hashCode());
    assertEquals(backlog.hashCode(), backlog3.hashCode());
  }

  @Test
  public void testCompareTo() throws Exception {
    Person person = new Person();
    Backlog before = new Backlog("aaa", "", "", person, fibonacci);
    Backlog after = new Backlog("zzz", "", "", person, fibonacci);
    Backlog same = new Backlog(label, "", "", person, fibonacci);
    Backlog clone = new Backlog(backlog);

    assertTrue(before.compareTo(backlog) < 0);
    assertTrue(after.compareTo(backlog) > 0);
    assertTrue(same.compareTo(backlog) == 0);
    assertTrue(clone.compareTo(backlog) == 0);
  }
}
