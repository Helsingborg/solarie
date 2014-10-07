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
public class SetArendeSenasteModifierare implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long ärendeIdentity;
  private Long senasteModifierareIdentity;

  public SetArendeSenasteModifierare() {
  }

  public SetArendeSenasteModifierare(Arende ärende, Anvandare senasteModifierare) {
    this.ärendeIdentity = ärende.getIdentity();
    this.senasteModifierareIdentity = senasteModifierare == null ? null : senasteModifierare.getIdentity();
  }

  public SetArendeSenasteModifierare(Long ärendeIdentity, Long senasteModifierareIdentity) {
    this.ärendeIdentity = ärendeIdentity;
    this.senasteModifierareIdentity = senasteModifierareIdentity;
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

    Anvandare senasteModifierare;

    if (senasteModifierareIdentity == null) {
      senasteModifierare = null;
    } else {
      senasteModifierare = root.getAnvändareByIdentity().get(senasteModifierareIdentity);
      if (senasteModifierare == null) {
        throw new IllegalArgumentException("No användare with that identity! " + senasteModifierareIdentity);
      }
    }

    if (ärende.getSenasteModifierare() != null) {
      ärende.getSenasteModifierare().getModifieradeÄrenden().remove(ärende);
    }
    ärende.setSenasteModifierare(senasteModifierare);
    if (senasteModifierare != null) {
      senasteModifierare.getModifieradeÄrenden().add(ärende);
    }

  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public Long getSenasteModifierareIdentity() {
    return senasteModifierareIdentity;
  }

  public void setSenasteModifierareIdentity(Long senasteModifierareIdentity) {
    this.senasteModifierareIdentity = senasteModifierareIdentity;
  }
}
