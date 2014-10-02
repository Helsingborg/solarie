package se.helsingborg.oppna.solarie.prevalence.transactions.arende;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.prevalence.domain.Arende;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetArendeBevakning implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long ärendeIdentity;
  private Long bevakning;

  public SetArendeBevakning() {
  }

  public SetArendeBevakning(Arende ärende, Long bevakning) {
    this.ärendeIdentity = ärende.getIdentity();
    this.bevakning = bevakning;
  }

  public SetArendeBevakning(Long ärendeIdentity, Long bevakning) {
    this.ärendeIdentity = ärendeIdentity;
    this.bevakning = bevakning;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getÄrendeByIdentity().get(ärendeIdentity).setBevakning(bevakning);
  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public Long getBevakning() {
    return bevakning;
  }

  public void setBevakning(Long bevakning) {
    this.bevakning = bevakning;
  }
}
