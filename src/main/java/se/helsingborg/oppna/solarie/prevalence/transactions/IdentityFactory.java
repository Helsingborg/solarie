package se.helsingborg.oppna.solarie.prevalence.transactions;

import org.prevayler.TransactionWithQuery;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 01:55
 */
public class IdentityFactory implements TransactionWithQuery<Root, Long> {

  private static final long serialVersionUID = 1l;

  @Override
  public Long executeAndQuery(Root root, Date executionTime) throws Exception {
    return root.getIdentityFactory().incrementAndGet();
  }

}
