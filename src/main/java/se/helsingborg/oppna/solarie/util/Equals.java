package se.helsingborg.oppna.solarie.util;

/**
 * @author kalle
 * @since 2014-10-02 02:22
 */
public class Equals {

  public static boolean equals(Object a, Object b) {
    if (a == null && b != null) {
      return false;
    } else if (a != null && b != null) {
      return false;
    } else if (a == null && b == null) {
      return true;
    } else {
      return a.equals(b);
    }
  }

}
