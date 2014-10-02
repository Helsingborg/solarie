package se.helsingborg.oppna.solarie.prevalence.transactions.atgard;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.prevalence.domain.Anvandare;
import se.helsingborg.oppna.solarie.prevalence.domain.Arende;
import se.helsingborg.oppna.solarie.prevalence.domain.Atgard;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 04:50
 */
public class SetAtgardAgare implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long åtgardIdentity;
  private Long ägareIdentity;

  public SetAtgardAgare() {
  }

  public SetAtgardAgare(Atgard åtgärd, Anvandare ägare) {
    this.åtgardIdentity = åtgärd.getIdentity();
    this.ägareIdentity = ägare == null ? null : ägare.getIdentity();
  }

  public SetAtgardAgare(Long åtgardIdentity, Long ägareIdentity) {
    this.åtgardIdentity = åtgardIdentity;
    this.ägareIdentity = ägareIdentity;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {

    if (åtgardIdentity == null) {
      throw new IllegalArgumentException("Åtgärd has not been set!");
    }
    Atgard åtgärd = root.getÅtgärdByIdentity().get(åtgardIdentity);
    if (åtgärd == null) {
      throw new IllegalArgumentException("No åtgärd with that identity! " + åtgärd);
    }

    Anvandare ägare;

    if (ägareIdentity == null) {
      ägare = null;
    } else {
      ägare = root.getAnvändareByIdentity().get(ägareIdentity);
      if (ägare == null) {
        throw new IllegalArgumentException("No användare with that identity! " + ägareIdentity);
      }
    }

    if (åtgärd.getÄgare() != null) {
      åtgärd.getÄgare().getÄgdaÅtgärder().remove(åtgärd);
    }
    åtgärd.setÄgare(ägare);
    if (ägare != null) {
      ägare.getÄgdaÅtgärder().add(åtgärd);
    }

  }

  public Long getÅtgardIdentity() {
    return åtgardIdentity;
  }

  public void setÅtgardIdentity(Long åtgardIdentity) {
    this.åtgardIdentity = åtgardIdentity;
  }

  public Long getÄgareIdentity() {
    return ägareIdentity;
  }

  public void setÄgareIdentity(Long ägareIdentity) {
    this.ägareIdentity = ägareIdentity;
  }
}
