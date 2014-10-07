package se.helsingborg.oppna.solarie.prevalence.transactions.atgard;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.domain.Atgard;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetAtgardUtgick implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long åtgärdIdentity;
  private Long utgick;

  public SetAtgardUtgick() {
  }

  public SetAtgardUtgick(Atgard åtgärd, Long utgick) {
    this.åtgärdIdentity = åtgärd.getIdentity();
    this.utgick = utgick;
  }

  public SetAtgardUtgick(Long åtgärdIdentity, Long utgick) {
    this.åtgärdIdentity = åtgärdIdentity;
    this.utgick = utgick;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getÅtgärdByIdentity().get(åtgärdIdentity).setUtgick(utgick);
  }

  public Long getÅtgärdIdentity() {
    return åtgärdIdentity;
  }

  public void setÅtgärdIdentity(Long åtgärdIdentity) {
    this.åtgärdIdentity = åtgärdIdentity;
  }

  public Long getUtgick() {
    return utgick;
  }

  public void setUtgick(Long utgick) {
    this.utgick = utgick;
  }
}
