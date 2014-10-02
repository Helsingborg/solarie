package se.helsingborg.oppna.solarie.prevalence.queries;

import org.prevayler.Query;
import se.helsingborg.oppna.solarie.prevalence.domain.Anvandare;
import se.helsingborg.oppna.solarie.prevalence.domain.Diarium;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 04:36
 */
public class GetAnvandareBySignatur implements Query<Root, Anvandare> {

  private Long diariumIdentity;
  private String signatur;

  public GetAnvandareBySignatur(Diarium diarium, String signatur) {
    this.diariumIdentity = diarium.getIdentity();
    this.signatur = signatur;
  }

  public GetAnvandareBySignatur(Long diariumIdentity, String signatur) {
    this.diariumIdentity = diariumIdentity;
    this.signatur = signatur;
  }

  @Override
  public Anvandare query(Root root, Date executionTime) throws Exception {
    return root.getDiariumByIdentity().get(diariumIdentity).getAnvändareBySignatur().get(signatur);
  }


}
