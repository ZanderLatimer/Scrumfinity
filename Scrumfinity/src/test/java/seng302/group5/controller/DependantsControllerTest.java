package seng302.group5.controller;

import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javafx.stage.Stage;
import seng302.group5.Main;
import seng302.group5.controller.dialogControllers.DependantsDialogController;
import seng302.group5.model.Person;
import seng302.group5.model.Story;
import static org.junit.Assert.*;

/**
 * Created by @author Alex Woo
 */
public class DependantsControllerTest {

  DependantsDialogController ddc;

  Story storya;
  Story storyb;
  Story storyc;
  Story storyd;
  Story storye;
  Story storyf;
  Story storyg;
  Story storyh;
  Map<String, Story> testSyncMap;
  Main mainApp;
  Stage newStage;
  Story newStory;


  /**
   * This sets up the stories with their dependencies in accordance to the following graph:
   *
   *        a
   *      /  \
   *     b    c
   *    / \    \
   *   d   e    g
   *  / \      /
   * f   e    h
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    ddc = new DependantsDialogController();

    storya = new Story("a", "a", "a", new Person());
    storyb = new Story("b", "b", "b", new Person());
    storyc = new Story("c", "c", "c", new Person());
    storyd = new Story("d", "d", "d", new Person());
    storye = new Story("e", "e", "e", new Person());
    storyf = new Story("f", "f", "f", new Person());
    storyg = new Story("g", "g", "g", new Person());
    storyh = new Story("h", "h", "h", new Person());

    List<Story> depsa = new ArrayList<Story>();
    depsa.add(storyb);
    depsa.add(storyc);
    storya.addAllDependencies(depsa);

    List<Story> depsb = new ArrayList<Story>();
    depsb.add(storyd);
    depsb.add(storye);
    storyb.addAllDependencies(depsb);

    List<Story> depsc = new ArrayList<Story>();
    depsc.add(storyg);
    storyc.addAllDependencies(depsc);

    List<Story> depsd = new ArrayList<Story>();
    depsd.add(storyf);
    storyd.addAllDependencies(depsd);

    List<Story> depsg = new ArrayList<Story>();
    depsg.add(storyh);
    storyg.addAllDependencies(depsg);
  }

  /**
   * This is a simple blue skys test with a dependency set up that should pass
   * @throws Exception
   */
  @Test
  public void testPassingCaseForDependencies() throws Exception {
    boolean result;
    result = ddc.checkIsCyclic(storya);
    assertEquals(false, result);
  }

  /**
   * Fail case where a final child node has a cycle between it and its single parent
   * @throws Exception
   */
  @Test
  public void testFailingCaseForDependenciesAtBranchEnd() throws Exception {
    boolean result;

    storyf.addDependency(storyd);

    result = ddc.checkIsCyclic(storyd);
    assertEquals(true, result);
  }

  /**
   * Fail case where the root node has a cycle with one of its children
   * @throws Exception
   */
  @Test
  public void testFailingCaseForDependenciesAtRoot() throws Exception {
    boolean result;

    storyb.addDependency(storya);

    result = ddc.checkIsCyclic(storya);
    assertEquals(true, result);
  }

  /**
   * pass case where two nodes depend on the one same node.
   * @throws Exception
   */
  @Test
  public void testPassingCaseTwoNodesOneDependent() throws Exception {
    boolean result;

    storye.addDependency(storyc);

    result = ddc.checkIsCyclic(storya);
    assertEquals(false, result);
  }

  /**
   * Fail case where the root node has a cycle with one of its childrens children.
   * @throws Exception
   */
  @Test
  public void testFailingCaseForDependenciesWhenOverMultipleNodes() throws Exception {
    boolean result;

    storye.addDependency(storya);

    result = ddc.checkIsCyclic(storya);
    assertEquals(true, result);
  }

  /**
   * Fail case where there are multiple (3) cycles placed throughout the graph
   * @throws Exception
   */
  @Test
  public void testFailingCaseForDependenciesMultipleCycles() throws Exception {
    boolean result;

    storye.addDependency(storya);
    storyh.addDependency(storya);
    storyf.addDependency(storyb);

    result = ddc.checkIsCyclic(storya);
    assertEquals(true, result);
  }
}
