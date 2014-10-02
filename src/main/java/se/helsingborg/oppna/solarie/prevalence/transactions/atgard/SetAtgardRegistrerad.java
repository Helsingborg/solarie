package se.helsingborg.oppna.solarie.prevalence.transactions.atgard;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.prevalence.domain.Arende;
import se.helsingborg.oppna.solarie.prevalence.domain.Atgard;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetAtgardRegistrerad implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long åtgärdIdentity;
  private Long registrerad;

  public SetAtgardRegistrerad() {
  }

  public SetAtgardRegistrerad(Atgard åtgärd, Long registrerad) {
    this.åtgärdIdentity = åtgärd.getIdentity();
    this.registrerad = registrerad;
  }

  public SetAtgardRegistrerad(Long åtgärdIdentity, Long registrerad) {
    this.åtgärdIdentity = åtgärdIdentity;
    this.registrerad = registrerad;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getÅtgärdByIdentity().get(åtgärdIdentity).setRegistrerad(registrerad);
  }

  public Long getÅtgärdIdentity() {
    return åtgärdIdentity;
  }

  public void setÅtgärdIdentity(Long åtgärdIdentity) {
    this.åtgärdIdentity = åtgärdIdentity;
  }

  public Long getRegistrerad() {
    return registrerad;
  }

  public void setRegistrerad(Long registrerad) {
    this.registrerad = registrerad;
  }
}
