package se.helsingborg.oppna.solarie.prevalence.transactions.anvandare;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.domain.Anvandare;
import se.helsingborg.oppna.solarie.domain.Atgard;
import se.helsingborg.oppna.solarie.domain.Enhet;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 04:50
 */
public class SetAnvandareEnhet implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long användareIdentity;
  private Long enhetIdentity;

  public SetAnvandareEnhet() {
  }

  public SetAnvandareEnhet(Anvandare användare, Enhet enhet) {
    this.användareIdentity = användare.getIdentity();
    this.enhetIdentity = enhet == null ? null : enhet.getIdentity();
  }

  public SetAnvandareEnhet(Long användareIdentity, Long enhetIdentity) {
    this.användareIdentity = användareIdentity;
    this.enhetIdentity = enhetIdentity;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {

    if (användareIdentity == null) {
      throw new IllegalArgumentException("Användare has not been set!");
    }
    Anvandare användare = root.getAnvändareByIdentity().get(användareIdentity);
    if (användare == null) {
      throw new IllegalArgumentException("No användare with that identity! " + användare);
    }

    Enhet enhet;

    if (enhetIdentity == null) {
      enhet = null;
    } else {
      enhet = root.getEnhetByIdentity().get(enhetIdentity);
      if (enhet == null) {
        throw new IllegalArgumentException("No enhet with that identity! " + enhetIdentity);
      }
    }

    if (användare.getEnhet() != null) {
      användare.getEnhet().getAnvändare().remove(användare);
    }
    användare.setEnhet(enhet);
    if (enhet != null) {
      enhet.getAnvändare().add(användare);
    }

  }

  public Long getAnvändareIdentity() {
    return användareIdentity;
  }

  public void setAnvändareIdentity(Long användareIdentity) {
    this.användareIdentity = användareIdentity;
  }

  public Long getEnhetIdentity() {
    return enhetIdentity;
  }

  public void setEnhetIdentity(Long enhetIdentity) {
    this.enhetIdentity = enhetIdentity;
  }
}
