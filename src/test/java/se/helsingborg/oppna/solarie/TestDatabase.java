package se.helsingborg.oppna.solarie;

import junit.framework.TestCase;
import se.helsingborg.oppna.solarie.prevalence.domain.Diarienummer;

/**
 * @author kalle
 * @since 2014-10-01 23:19
 */
public class TestDatabase extends TestCase {

  public void testDiarienummerFactory() {

    Diarienummer diarienummer = Database.diarienummerFactory("201100167");
    assertEquals("2011", diarienummer.getÅr());
    assertEquals("00167", diarienummer.getLöpnummer());

  }

}
