package seng302.group5.model.util;

import com.sun.media.jfxmediaimpl.platform.osx.OSXPlatform;

import org.junit.Test;

import java.io.File;

import javax.crypto.Mac;

import static org.junit.Assert.*;

/**
 * Created by Michael on 3/31/2015.
 */
public class SettingsTest {

  @Test
  public void testDefaultDirectory() {
    String sysName = System.getProperty("os.name");
    if (sysName.startsWith("Windows")) {
      Settings.setSysDefault();
      File windowsDir = new File(System.getProperty("user.home")+"\\Scrumfinity");
      assertEquals(Settings.defaultFilepath, windowsDir);
    }
    //TODO find out how to simulate OSX and Linux environments t test
  }
}
