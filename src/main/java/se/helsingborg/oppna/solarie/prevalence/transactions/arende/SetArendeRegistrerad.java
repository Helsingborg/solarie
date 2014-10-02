package se.helsingborg.oppna.solarie.prevalence.transactions.arende;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.prevalence.domain.Arende;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetArendeRegistrerad implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long ärendeIdentity;
  private Long registrerad;

  public SetArendeRegistrerad() {
  }

  public SetArendeRegistrerad(Arende ärende, Long registrerad) {
    this.ärendeIdentity = ärende.getIdentity();
    this.registrerad = registrerad;
  }

  public SetArendeRegistrerad(Long ärendeIdentity, Long registrerad) {
    this.ärendeIdentity = ärendeIdentity;
    this.registrerad = registrerad;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getÄrendeByIdentity().get(ärendeIdentity).setRegistrerad(registrerad);
  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public Long getRegistrerad() {
    return registrerad;
  }

  public void setRegistrerad(Long registrerad) {
    this.registrerad = registrerad;
  }
}
