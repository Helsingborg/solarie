package se.helsingborg.oppna.solarie.prevalence.transactions.arende;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.domain.Anvandare;
import se.helsingborg.oppna.solarie.domain.Arende;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 04:50
 */
public class SetArendeAgare implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long ärendeIdentity;
  private Long ägareIdentity;

  public SetArendeAgare() {
  }

  public SetArendeAgare(Arende ärende, Anvandare ägare) {
    this.ärendeIdentity = ärende.getIdentity();
    this.ägareIdentity = ägare == null ? null : ägare.getIdentity();
  }

  public SetArendeAgare(Long ärendeIdentity, Long ägareIdentity) {
    this.ärendeIdentity = ärendeIdentity;
    this.ägareIdentity = ägareIdentity;
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

    Anvandare ägare;

    if (ägareIdentity == null) {
      ägare = null;
    } else {
      ägare = root.getAnvändareByIdentity().get(ägareIdentity);
      if (ägare == null) {
        throw new IllegalArgumentException("No användare with that identity! " + ägareIdentity);
      }
    }

    if (ärende.getÄgare() != null) {
      ärende.getÄgare().getÄgdaÄrenden().remove(ärende);
    }
    ärende.setÄgare(ägare);
    if (ägare != null) {
      ägare.getÄgdaÄrenden().add(ärende);
    }

  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public Long getÄgareIdentity() {
    return ägareIdentity;
  }

  public void setÄgareIdentity(Long ägareIdentity) {
    this.ägareIdentity = ägareIdentity;
  }
}
