package seng302.group5.model.util;

import java.time.LocalTime;

/**
 * Class for managing the time formatting in Scrumfinity, i.e. [x]h[y]m where x and y are integers.
 *
 * @author Su-Shing Chen, Alex Woo
 */
public class TimeFormat {

  /**
   * Get the number of minutes from a string that follows the Scrumfinity time format. Ensures
   * that string follows the specified format and converts any hours into minutes.
   *
   * @param inputString The time formatted string to parse.
   * @return The time in minutes, -1 if input is invalid.
   */
  public static int parseMinutes(String inputString) {
    String timeRegex = "([0-9]+h)?([0-9]+m)?";
    inputString = inputString.trim();
    int hours = 0;
    int minutes = 0;
    int result = -1;
    if (inputString.matches(timeRegex)) {
      char[] charArray = inputString.toCharArray();
      String accumulator = "";
      for (char c : charArray) {
        if (c == 'h') {
          hours = Integer.parseInt(accumulator);
          accumulator = "";
        } else if (c == 'm') {
          minutes = Integer.parseInt(accumulator);
        } else {
          accumulator += c;
        }
      }
      result = hours * 60 + minutes;
    }

    return result;
  }

  /**
   * Get the time formatted string from an integer number of minutes. For zero it will return
   * 0m and for negative numbers it will be an empty string.
   *
   * @param totalMinutes total minutes to parse into h/m
   * @return Time formatted string.
   */
  public static String parseDuration(int totalMinutes) {
    int hours = totalMinutes / 60;
    int minutes = totalMinutes % 60;
    String result = "";
    if (totalMinutes == 0) {
      result = "0m";
    } else {
      if (hours > 0) {
        result += hours + "h";
      }
      if (minutes > 0) {
        result += minutes + "m";
      }
    }
    return result;
  }

  /**
   * Parse a string in the format h:mm or hh:mm into a LocalTime object.
   *
   * @param inputString Input string representation of time.
   * @return Converted LocalTime object from the string, or null if the input is invalid.
   */
  public static LocalTime parseLocalTime(String inputString) {
    String timeRegex = "[0-9]+:[0-9][0-9]";
    inputString = inputString.trim();
    LocalTime result = null;
    if (inputString.matches(timeRegex)) {
      String segments[] = inputString.split(":");
      int hours = Integer.parseInt(segments[0]);
      int minutes = Integer.parseInt(segments[1]);
      if (hours < 24) {
        result = LocalTime.of(hours, minutes);
      }
    }
    return result;
  }

  /**
   * Parse a LocalTime object into a string in the 24 hour format hh:mm, but h:mm if hours is a
   * single digit.
   * Preconditions: The LocalTime object is valid and non-null.
   *
   * @param localTime LocalTime object to convert.
   * @return String representation of time.
   */
  public static String parseTimeString(LocalTime localTime) {
    int hours = localTime.getHour();
    int minutes = localTime.getMinute();
    return String.format("%d:%02d", hours, minutes);
  }
}
