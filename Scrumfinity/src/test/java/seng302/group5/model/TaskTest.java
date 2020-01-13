package seng302.group5.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by Michael on 11/8/2015.
 */
public class TaskTest {

  private String label;
  private String description;
  private int estimation;
  private Status status;
  private List<Person> assignedPeople;

  private Task task1;
  private Task task2;
  private Task task3;
  private Task task4;

  private Person person1;
  private Person person2;
  private Person person3;

  @Before
  public void setUp() throws Exception {
    label = "Task";
    description = "A description";
    estimation = 300;
    status = Status.NOT_STARTED;
    createVanillaPeople();
    assignedPeople = new ArrayList<>();
    assignedPeople.addAll(Arrays.asList(person1, person2, person3));

    task1 = new Task(label, description, estimation, status, assignedPeople);
//    task1.updateSpentEffort(person1, 50);
    task2 = new Task(task1);
    task3 =
        new Task("AnotherTask", "DESCRIPSTIONING", 400, Status.DONE, Arrays.asList(person3, person2));

  }

  public void createVanillaPeople() {
    person1 = new Person();
    person1.setLabel("Person1");
    person1.setFirstName("Only first name");
    person2 = new Person();
    person2.setLabel("Person2");
    person2.setFirstName("Both first");
    person2.setLastName("And last");
    person3 = new Person();
    person3.setLabel("Person3");
    person3.setLastName("Only last name");
  }

  @Test
  public void testToString() {
    assertEquals(task1.toString(), task1.getLabel());
  }

  @Test
  public void testEquals() {
    assertEquals(task1, task1);
    assertEquals(task1, task2);
//    task1.updateSpentEffort(person2, 10);
//    task2.updateSpentEffort(person2, 10);
//    assertEquals(task1, task2);
  }

  @Test
  public void testClearing() {
    task1.removeAllTaskPeople();
//    assertTrue(task1.getSpentEffort().isEmpty());
    assertTrue(task1.getTaskPeople().isEmpty());
  }

  @Test
  public void testAddingPeople() {
    task1.removeAllTaskPeople();
    task1.addTaskPerson(person1);
//    task1.updateSpentEffort(person1, 20);
    assertTrue(task1.getTaskPeople().contains(person1));
//    assertTrue(task1.getSpentEffort().get(person1) == 20);
  }

  @Test
  public void testRemovingPeople() {
    task1.removeTaskPerson(person2);
    assertFalse(task1.getTaskPeople().contains(person2));
//    assertFalse(task1.getSpentEffort().containsKey(person2));
  }
}
