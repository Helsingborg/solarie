package se.helsingborg.oppna.solarie.prevalence.transactions.arende;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.prevalence.domain.Anvandare;
import se.helsingborg.oppna.solarie.prevalence.domain.Arende;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 04:50
 */
public class SetArendeHandlaggare implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long ärendeIdentity;
  private Long handläggareIdentity;

  public SetArendeHandlaggare() {
  }

  public SetArendeHandlaggare(Arende ärende, Anvandare handläggare) {
    this.ärendeIdentity = ärende.getIdentity();
    this.handläggareIdentity = handläggare == null ? null : handläggare.getIdentity();
  }

  public SetArendeHandlaggare(Long ärendeIdentity, Long handläggareIdentity) {
    this.ärendeIdentity = ärendeIdentity;
    this.handläggareIdentity = handläggareIdentity;
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

    Anvandare handläggare;

    if (handläggareIdentity == null) {
      handläggare = null;
    } else {
      handläggare = root.getAnvändareByIdentity().get(handläggareIdentity);
      if (handläggare == null) {
        throw new IllegalArgumentException("No användare with that identity! " + handläggareIdentity);
      }
    }

    if (ärende.getHandläggare() != null) {
      ärende.getHandläggare().getHandlagdaÄrenden().remove(ärende);
    }
    ärende.setHandläggare(handläggare);
    if (handläggare != null) {
      handläggare.getHandlagdaÄrenden().add(ärende);
    }

  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public Long getHandläggareIdentity() {
    return handläggareIdentity;
  }

  public void setHandläggareIdentity(Long handläggareIdentity) {
    this.handläggareIdentity = handläggareIdentity;
  }
}
