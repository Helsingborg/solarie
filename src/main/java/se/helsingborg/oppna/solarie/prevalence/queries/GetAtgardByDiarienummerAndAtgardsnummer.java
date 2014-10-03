package se.helsingborg.oppna.solarie.prevalence.queries;

import org.prevayler.Query;
import se.helsingborg.oppna.solarie.domain.Atgard;
import se.helsingborg.oppna.solarie.domain.Diarienummer;
import se.helsingborg.oppna.solarie.domain.Diarium;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 06:14
 */
public class GetAtgardByDiarienummerAndAtgardsnummer implements Query<Root, Atgard> {

  private Long diariumIdentity;
  private Diarienummer diarienummer;
  private short åtgärdsnummer;

  public GetAtgardByDiarienummerAndAtgardsnummer(Diarium diarium, Diarienummer diarienummer, short åtgärdsnummer) {
    this.diariumIdentity = diarium.getIdentity();
    this.diarienummer = diarienummer;
    this.åtgärdsnummer = åtgärdsnummer;
  }

  public GetAtgardByDiarienummerAndAtgardsnummer(Long diariumIdentity, Diarienummer diarienummer, short åtgärdsnummer) {
    this.diariumIdentity = diariumIdentity;
    this.diarienummer = diarienummer;
    this.åtgärdsnummer = åtgärdsnummer;
  }

  @Override
  public Atgard query(Root root, Date executionTime) throws Exception {
    return root.getDiariumByIdentity().get(diariumIdentity).getÄrendeByDiarienummer().get(diarienummer).getÅtgärderByNummer().get(åtgärdsnummer);
  }
}
