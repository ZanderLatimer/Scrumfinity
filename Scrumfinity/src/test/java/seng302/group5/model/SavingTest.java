package seng302.group5.model;

import org.junit.Test;
import static org.junit.Assert.*;
import seng302.group5.model.util.Settings;

/**
 * @author Michael
 */
public class SavingTest {

  @Test
  public void testDirCreation() {
    Settings.setSysDefault();
    if (!Settings.defaultFilepath.exists()) {
      fail();
    }
  }
}
