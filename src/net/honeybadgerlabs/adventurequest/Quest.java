package net.honeybadgerlabs.adventurequest;

public class Quest {
  public static final int STATUS_NONE     = 0;
  public static final int STATUS_FAILED   = 1;
  public static final int STATUS_COMPLETE = 2;
  public static final int STATUS_PROGRESS = 3;
  public static final int STATUS_ABANDON  = 4;

  public String description = "";
  public int status = STATUS_NONE;

  public Quest(String description, int status) {
    this.description = description;
    this.status = status;
  }
}
