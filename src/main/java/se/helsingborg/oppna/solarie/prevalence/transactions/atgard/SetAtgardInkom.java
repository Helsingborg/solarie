package se.helsingborg.oppna.solarie.prevalence.transactions.atgard;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.domain.Atgard;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetAtgardInkom implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long åtgärdIdentity;
  private Long inkom;

  public SetAtgardInkom() {
  }

  public SetAtgardInkom(Atgard åtgärd, Long inkom) {
    this.åtgärdIdentity = åtgärd.getIdentity();
    this.inkom = inkom;
  }

  public SetAtgardInkom(Long åtgärdIdentity, Long inkom) {
    this.åtgärdIdentity = åtgärdIdentity;
    this.inkom = inkom;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getÅtgärdByIdentity().get(åtgärdIdentity).setInkom(inkom);
  }

  public Long getÅtgärdIdentity() {
    return åtgärdIdentity;
  }

  public void setÅtgärdIdentity(Long åtgärdIdentity) {
    this.åtgärdIdentity = åtgärdIdentity;
  }

  public Long getInkom() {
    return inkom;
  }

  public void setInkom(Long inkom) {
    this.inkom = inkom;
  }
}
