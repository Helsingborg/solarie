package se.helsingborg.oppna.solarie.prevalence.queries;

import org.prevayler.Query;
import se.helsingborg.oppna.solarie.prevalence.domain.Arende;
import se.helsingborg.oppna.solarie.prevalence.domain.Diarienummer;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-01 23:14
 */
public class GetArendeByDiarienummer implements Query<Root, Arende> {

  private Diarienummer diarienummer;

  public GetArendeByDiarienummer(Diarienummer diarienummer) {
    this.diarienummer = diarienummer;
  }

  @Override
  public Arende query(Root root, Date executionTime) throws Exception {
    return root.getÄrendeByDiarienummer().get(diarienummer);
  }
}
