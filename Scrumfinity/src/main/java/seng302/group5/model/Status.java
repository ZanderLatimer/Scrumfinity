package seng302.group5.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * An enum to represent a status of an AgileItem. The available values in their string
 * representations are Done, Verify, In Progress, and Not Started.
 *
 * @author Alex Woo
 */
public enum Status {
  NOT_STARTED,
  IN_PROGRESS,
  VERIFY,
  DONE;


  private static Map<Status, String> statusStringMap;
  static {
    statusStringMap = new EnumMap<>(Status.class);
    statusStringMap.put(DONE, "Done");
    statusStringMap.put(VERIFY, "Verify");
    statusStringMap.put(IN_PROGRESS, "In Progress");
    statusStringMap.put(NOT_STARTED, "Not Started");
  }

  /**
   * Get the string representation of a Status enum instance.
   *
   * @param status Status enum instance.
   * @return The status's string representation.
   */
  public static String getStatusString(Status status) {
    return statusStringMap.get(status);
  }

  /**
   * Get the Status enum instance from its string representation
   *
   * @param statusStr The string representation.
   * @return The string's Status instance.
   */
  public static Status getStatusEnum(String statusStr) {
    Status result = null;
    for (Map.Entry<Status, String> entry : statusStringMap.entrySet()) {
      if (statusStr.equals(entry.getValue())) {
        result = entry.getKey();
      }
    }
    return result;
  }
}
