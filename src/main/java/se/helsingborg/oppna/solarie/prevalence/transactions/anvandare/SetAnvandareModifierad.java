package se.helsingborg.oppna.solarie.prevalence.transactions.anvandare;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.domain.Anvandare;
import se.helsingborg.oppna.solarie.domain.Enhet;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetAnvandareModifierad implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long användareIdentity;
  private Long modifierad;

  public SetAnvandareModifierad() {
  }

  public SetAnvandareModifierad(Anvandare användare, Long modifierad) {
    this.användareIdentity = användare.getIdentity();
    this.modifierad = modifierad;
  }

  public SetAnvandareModifierad(Long användareIdentity, Long modifierad) {
    this.användareIdentity = användareIdentity;
    this.modifierad = modifierad;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getAnvändareByIdentity().get(användareIdentity).setModifierad(modifierad);
  }

  public Long getAnvändareIdentity() {
    return användareIdentity;
  }

  public void setAnvändareIdentity(Long användareIdentity) {
    this.användareIdentity = användareIdentity;
  }

  public Long getModifierad() {
    return modifierad;
  }

  public void setModifierad(Long modifierad) {
    this.modifierad = modifierad;
  }
}
