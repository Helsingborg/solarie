package se.helsingborg.oppna.solarie.prevalence.transactions.atgard;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.domain.Atgard;
import se.helsingborg.oppna.solarie.domain.Enhet;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 04:50
 */
public class SetAtgardEnhet implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long åtgärdIdentity;
  private Long enhetIdentity;

  public SetAtgardEnhet() {
  }

  public SetAtgardEnhet(Atgard åtgärd, Enhet enhet) {
    this.åtgärdIdentity = åtgärd.getIdentity();
    this.enhetIdentity = enhet == null ? null : enhet.getIdentity();
  }

  public SetAtgardEnhet(Long åtgärdIdentity, Long enhetIdentity) {
    this.åtgärdIdentity = åtgärdIdentity;
    this.enhetIdentity = enhetIdentity;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {

    if (åtgärdIdentity == null) {
      throw new IllegalArgumentException("Åtgärd has not been set!");
    }
    Atgard åtgärd = root.getÅtgärdByIdentity().get(åtgärdIdentity);
    if (åtgärd == null) {
      throw new IllegalArgumentException("No åtgärd with that identity! " + åtgärd);
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

    if (åtgärd.getEnhet() != null) {
      åtgärd.getEnhet().getÅtgärder().remove(åtgärd);
    }
    åtgärd.setEnhet(enhet);
    if (enhet != null) {
      enhet.getÅtgärder().add(åtgärd);
    }

  }

  public Long getÅtgärdIdentity() {
    return åtgärdIdentity;
  }

  public void setÅtgärdIdentity(Long åtgärdIdentity) {
    this.åtgärdIdentity = åtgärdIdentity;
  }

  public Long getEnhetIdentity() {
    return enhetIdentity;
  }

  public void setEnhetIdentity(Long enhetIdentity) {
    this.enhetIdentity = enhetIdentity;
  }
}
