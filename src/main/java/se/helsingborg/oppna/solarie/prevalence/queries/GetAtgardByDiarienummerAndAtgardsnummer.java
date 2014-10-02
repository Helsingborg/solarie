package se.helsingborg.oppna.solarie.prevalence.queries;

import org.prevayler.Query;
import se.helsingborg.oppna.solarie.prevalence.domain.Atgard;
import se.helsingborg.oppna.solarie.prevalence.domain.Diarienummer;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 06:14
 */
public class GetAtgardByDiarienummerAndAtgardsnummer implements Query<Root, Atgard> {

  private Diarienummer diarienummer;
  private short åtgärdsnummer;

  public GetAtgardByDiarienummerAndAtgardsnummer(Diarienummer diarienummer, short åtgärdsnummer) {
    this.diarienummer = diarienummer;
    this.åtgärdsnummer = åtgärdsnummer;
  }

  @Override
  public Atgard query(Root root, Date executionTime) throws Exception {
    return root.getÄrendeByDiarienummer().get(diarienummer).getÅtgärderByNummer().get(åtgärdsnummer);
  }
}
