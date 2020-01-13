package seng302.group5.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * Unit tests for estimate class (no constructors, getters or setters)
 * Created by Michael on 6/2/2015.
 */
public class EstimateTest {

  public Estimate estimate;
  public List<String> estimateNames;
  public List<String> fiboEsts = Arrays.asList("Not Set", "1", "2", "3", "5", "8", "13", "Epic");
  public Estimate fibonacci = new Estimate("Fibonacci", fiboEsts);

  /**
   * Quick test to make sure the toString works properly in displaying the label. May be unnceessary...
   */
  @Test
  public void testToString() {
    estimateNames = new ArrayList<>();
    estimateNames.add("Duck");
    estimateNames.add("Fish");
    estimate = new Estimate("Fibbo", estimateNames);

    assertEquals("Fibbo", estimate.toString());
  }

  /**
   * Tests the compareTo methods for list sorting. Just creates a few Estimates with labels.
   *
   * @throws Exception
   */
  @Test
  public void testCompareTo() throws Exception {
    Estimate before = new Estimate("aaa",new ArrayList<>());
    Estimate after = new Estimate("z",new ArrayList<>());
    Estimate middle = new Estimate("g",new ArrayList<>());
    Estimate same = new Estimate("g",new ArrayList<>());

    assertTrue(before.compareTo(middle) < 0);
    assertTrue(after.compareTo(middle) > 0);
    assertTrue(middle.compareTo(same) == 0);
  }

  /**
   * Tests add function. Makes sure that the add functions properly sets the map.
   *
   * Blue skies test.
   * @throws Exception
   */
  @Test
  public void testEstimates() throws Exception {
    Person steve = new Person();
    Backlog backlog1 = new Backlog("Backlog", "Backlog1", "The backlog", steve, fibonacci);
    Story story = new Story("story1", "Story1", "First story", steve);
    Story story2 = new Story("story2", "Story2", "Second story", steve);
    Story story3 = new Story("story3", "Story3", "Third story", steve);
    backlog1.addStory(story, 0);
    backlog1.addStory(story2, 7);
    backlog1.addStory(story3, 4);

    for (Map.Entry<Story, Integer> entry : backlog1.getSizes().entrySet())
      assertEquals(fibonacci.getEstimateNames().get(entry.getValue()),
                   backlog1.getEstimate().getEstimateNames().get(entry.getValue()));
  }

  /**
   * Test to make sure that the update method works as expected, takes existing stories in
   * the map and updates them.
   *
   * Blue skies test.
   * @throws Exception
   */
  @Test
  public void testChangeEstimates() throws Exception {
    Person steve = new Person();
    Backlog backlog1 = new Backlog("Backlog", "Backlog1", "The backlog", steve, fibonacci);
    Story story = new Story("story1", "Story1", "First story", steve);
    Story story2 = new Story("story2", "Story2", "Second story", steve);
    Story story3 = new Story("story3", "Story3", "Third story", steve);
    backlog1.addStory(story, 0);
    backlog1.addStory(story2, 7);
    backlog1.addStory(story3, 4);

    for (Map.Entry<Story, Integer> entry : backlog1.getSizes().entrySet())
      assertEquals(fibonacci.getEstimateNames().get(entry.getValue()),
                   backlog1.getEstimate().getEstimateNames().get(entry.getValue()));

    backlog1.updateStory(story, 1);
    backlog1.updateStory(story2, 3);
    backlog1.updateStory(story3, 7);

    for (Map.Entry<Story, Integer> entry : backlog1.getSizes().entrySet())
      assertEquals(fibonacci.getEstimateNames().get(entry.getValue()),
                   backlog1.getEstimate().getEstimateNames().get(entry.getValue()));
  }
}
