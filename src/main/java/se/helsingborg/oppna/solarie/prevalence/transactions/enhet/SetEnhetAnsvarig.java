package se.helsingborg.oppna.solarie.prevalence.transactions.enhet;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.prevalence.domain.Anvandare;
import se.helsingborg.oppna.solarie.prevalence.domain.Arende;
import se.helsingborg.oppna.solarie.prevalence.domain.Enhet;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 04:50
 */
public class SetEnhetAnsvarig implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long enhetIdentity;
  private Long ansvarigIdentity;

  public SetEnhetAnsvarig() {
  }

  public SetEnhetAnsvarig(Enhet enhet, Anvandare ansvarig) {
    this.enhetIdentity = enhet.getIdentity();
    this.ansvarigIdentity = ansvarig == null ? null : ansvarig.getIdentity();
  }

  public SetEnhetAnsvarig(Long enhetIdentity, Long ansvarigIdentity) {
    this.enhetIdentity = enhetIdentity;
    this.ansvarigIdentity = ansvarigIdentity;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {

    if (enhetIdentity == null) {
      throw new IllegalArgumentException("Enhet has not been set!");
    }
    Enhet enhet = root.getEnhetByIdentity().get(enhetIdentity);
    if (enhet == null) {
      throw new IllegalArgumentException("No enhet with that identity! " + enhet);
    }

    Anvandare ansvarig;

    if (ansvarigIdentity == null) {
      ansvarig = null;
    } else {
      ansvarig = root.getAnvändareByIdentity().get(ansvarigIdentity);
      if (ansvarig == null) {
        throw new IllegalArgumentException("No användare with that identity! " + ansvarigIdentity);
      }
    }

    if (enhet.getAnsvarig() != null) {
      enhet.getAnsvarig().getEnhetsansvar().remove(enhet);
    }
    enhet.setAnsvarig(ansvarig);
    if (ansvarig != null) {
      ansvarig.getEnhetsansvar().add(enhet);
    }

  }

  public Long getEnhetIdentity() {
    return enhetIdentity;
  }

  public void setEnhetIdentity(Long enhetIdentity) {
    this.enhetIdentity = enhetIdentity;
  }

  public Long getAnsvarigIdentity() {
    return ansvarigIdentity;
  }

  public void setAnsvarigIdentity(Long ansvarigIdentity) {
    this.ansvarigIdentity = ansvarigIdentity;
  }
}
