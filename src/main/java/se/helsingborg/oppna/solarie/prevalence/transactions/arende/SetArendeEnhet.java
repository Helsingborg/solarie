package se.helsingborg.oppna.solarie.prevalence.transactions.arende;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.domain.Arende;
import se.helsingborg.oppna.solarie.domain.Enhet;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 04:50
 */
public class SetArendeEnhet implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long ärendeIdentity;
  private Long enhetIdentity;

  public SetArendeEnhet() {
  }

  public SetArendeEnhet(Arende ärende, Enhet enhet) {
    this.ärendeIdentity = ärende.getIdentity();
    this.enhetIdentity = enhet == null ? null : enhet.getIdentity();
  }

  public SetArendeEnhet(Long ärendeIdentity, Long enhetIdentity) {
    this.ärendeIdentity = ärendeIdentity;
    this.enhetIdentity = enhetIdentity;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {

    if (ärendeIdentity == null) {
      throw new IllegalArgumentException("Ärende has not been set!");
    }
    Arende ärende = root.getÄrendeByIdentity().get(ärendeIdentity);
    if (ärende == null) {
      throw new IllegalArgumentException("No ärende with that identity! " + ärende);
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

    if (ärende.getEnhet() != null) {
      ärende.getEnhet().getÄrenden().remove(ärende);
    }
    ärende.setEnhet(enhet);
    if (enhet != null) {
      enhet.getÄrenden().add(ärende);
    }

  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public Long getEnhetIdentity() {
    return enhetIdentity;
  }

  public void setEnhetIdentity(Long enhetIdentity) {
    this.enhetIdentity = enhetIdentity;
  }
}
