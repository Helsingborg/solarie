package se.helsingborg.oppna.solarie.prevalence.transactions.enhet;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.domain.Enhet;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetEnhetModifierad implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long enhetIdentity;
  private Long modifierad;

  public SetEnhetModifierad() {
  }

  public SetEnhetModifierad(Enhet enhet, Long modifierad) {
    this.enhetIdentity = enhet.getIdentity();
    this.modifierad = modifierad;
  }

  public SetEnhetModifierad(Long enhetIdentity, Long modifierad) {
    this.enhetIdentity = enhetIdentity;
    this.modifierad = modifierad;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getEnhetByIdentity().get(enhetIdentity).setModifierad(modifierad);
  }

  public Long getEnhetIdentity() {
    return enhetIdentity;
  }

  public void setEnhetIdentity(Long enhetIdentity) {
    this.enhetIdentity = enhetIdentity;
  }

  public Long getModifierad() {
    return modifierad;
  }

  public void setModifierad(Long modifierad) {
    this.modifierad = modifierad;
  }
}
