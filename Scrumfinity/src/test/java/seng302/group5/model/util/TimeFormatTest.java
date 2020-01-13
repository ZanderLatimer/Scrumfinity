package seng302.group5.model.util;

import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.*;

/**
 * Unit tests to ensure the time formatting parser works correctly
 */
public class TimeFormatTest {

  @Test
  public void testDurationEmptyString() {
    String inputString = "";
    int result = TimeFormat.parseMinutes(inputString);
    assertEquals(0, result);
  }

  @Test
  public void testDurationValidHoursMinutesString() {
    String inputString = "1h30m";
    int result = TimeFormat.parseMinutes(inputString);
    assertEquals(90, result);
  }

  @Test
  public void testDurationValidLongHoursMinutesString() {
    String inputString = "100h30m";
    int result = TimeFormat.parseMinutes(inputString);
    assertEquals(6030, result);
  }

  @Test
  public void testDurationValidHoursString() {
    String inputString = "2h";
    int result = TimeFormat.parseMinutes(inputString);
    assertEquals(120, result);
  }

  @Test
  public void testDurationValidLongHoursString() {
    String inputString = "150h";
    int result = TimeFormat.parseMinutes(inputString);
    assertEquals(9000, result);
  }

  @Test
  public void testDurationValidMinutesString() {
    String inputString = "59m";
    int result = TimeFormat.parseMinutes(inputString);
    assertEquals(59, result);
  }

  @Test
  public void testDurationValidLongMinutesString() {
    String inputString = "9001m";
    int result = TimeFormat.parseMinutes(inputString);
    assertEquals(9001, result);
  }

  @Test
  public void testDurationRandomString() {
    String inputString = "Hello World";
    int result = TimeFormat.parseMinutes(inputString);
    assertEquals(-1, result);
  }

  @Test
  public void testDurationSingleCharHString() {
    String inputString = "h";
    int result = TimeFormat.parseMinutes(inputString);
    assertEquals(-1, result);
  }

  @Test
  public void testDurationSingleCharMString() {
    String inputString = "m";
    int result = TimeFormat.parseMinutes(inputString);
    assertEquals(-1, result);
  }

  @Test
  public void testDurationValidHoursMinutesInt() {
    int inputInt = 1000;
    String result = TimeFormat.parseDuration(inputInt);
    assertEquals("16h40m", result);
  }

  @Test
  public void testDurationValidHoursInt() {
    int inputInt = 960;
    String result = TimeFormat.parseDuration(inputInt);
    assertEquals("16h", result);
  }

  @Test
  public void testDurationValidMinutesInt() {
    int inputInt = 38;
    String result = TimeFormat.parseDuration(inputInt);
    assertEquals("38m", result);
  }

  @Test
  public void testDurationZeroMinutesInt() {
    int inputInt = 0;
    String result = TimeFormat.parseDuration(inputInt);
    assertEquals("0m", result);
  }

  @Test
  public void testDurationInvalidMinutesInt() {
    int inputInt = -1;
    String result = TimeFormat.parseDuration(inputInt);
    assertEquals("", result);
  }

  @Test
  public void testTimeEmptyString() {
    String inputString = "";
    int result = TimeFormat.parseMinutes(inputString);
    assertEquals(0, result);
  }

  @Test
  public void testTimeFormatValidAM() {
    String inputString = "1:03";
    LocalTime result = TimeFormat.parseLocalTime(inputString);
    int hours = result.getHour();
    int minutes = result.getMinute();
    assertEquals(1, hours);
    assertEquals(3, minutes);
  }

  @Test
  public void testTimeFormatValidPM() {
    String inputString = "13:45";
    LocalTime result = TimeFormat.parseLocalTime(inputString);
    int hours = result.getHour();
    int minutes = result.getMinute();
    assertEquals(13, hours);
    assertEquals(45, minutes);
  }

  @Test
  public void testTimeFormatRandomString() {
    String inputString = "Hello World";
    LocalTime result = TimeFormat.parseLocalTime(inputString);
    assertNull(result);
  }

  @Test
  public void testTimeFormatHoursOnly() {
    String inputString = "13:";
    LocalTime result = TimeFormat.parseLocalTime(inputString);
    assertNull(result);
  }

  @Test
  public void testTimeFormatMinutesOnly() {
    String inputString = ":24";
    LocalTime result = TimeFormat.parseLocalTime(inputString);
    assertNull(result);
  }

  @Test
  public void testTimeFormatSingleMinute() {
    String inputString = "1:1";
    LocalTime result = TimeFormat.parseLocalTime(inputString);
    assertNull(result);
  }

  @Test
  public void testTimeFormatInvalidHours() {
    String inputString = "24:00";
    LocalTime result = TimeFormat.parseLocalTime(inputString);
    assertNull(result);
  }

  @Test
  public void testTimeFormatNegativeHours() {
    String inputString = "-2:00";
    LocalTime result = TimeFormat.parseLocalTime(inputString);
    assertNull(result);
  }

  @Test
  public void testParseTimeStringAM() {
    LocalTime inputTime = LocalTime.of(1, 3);
    String result = TimeFormat.parseTimeString(inputTime);
    assertEquals("1:03", result);
  }

  @Test
  public void testParseTimeStringPM() {
    LocalTime inputTime = LocalTime.of(13, 45);
    String result = TimeFormat.parseTimeString(inputTime);
    assertEquals("13:45", result);
  }
}
