package se.helsingborg.oppna.solarie.prevalence.transactions.arende;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.domain.Arende;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetArendeModifierad implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long ärendeIdentity;
  private Long modifierad;

  public SetArendeModifierad() {
  }

  public SetArendeModifierad(Arende ärende, Long modifierad) {
    this.ärendeIdentity = ärende.getIdentity();
    this.modifierad = modifierad;
  }

  public SetArendeModifierad(Long ärendeIdentity, Long modifierad) {
    this.ärendeIdentity = ärendeIdentity;
    this.modifierad = modifierad;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getÄrendeByIdentity().get(ärendeIdentity).setModifierad(modifierad);
  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public Long getModifierad() {
    return modifierad;
  }

  public void setModifierad(Long modifierad) {
    this.modifierad = modifierad;
  }
}
