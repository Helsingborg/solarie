package se.helsingborg.oppna.solarie.prevalence.queries;

import org.prevayler.Query;
import se.helsingborg.oppna.solarie.prevalence.domain.Anvandare;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 04:36
 */
public class GetAnvandareBySignatur implements Query<Root, Anvandare> {

  private String signatur;

  public GetAnvandareBySignatur(String signatur) {
    this.signatur = signatur;
  }

  @Override
  public Anvandare query(Root root, Date executionTime) throws Exception {
    return root.getAnvändareBySignatur().get(signatur);
  }
}
