package se.helsingborg.oppna.solarie.prevalence.transactions.arende;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.domain.Arende;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetArendeMakulerad implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long ärendeIdentity;
  private Long makulerad;

  public SetArendeMakulerad() {
  }

  public SetArendeMakulerad(Arende ärende, Long makulerad) {
    this.ärendeIdentity = ärende.getIdentity();
    this.makulerad = makulerad;
  }

  public SetArendeMakulerad(Long ärendeIdentity, Long makulerad) {
    this.ärendeIdentity = ärendeIdentity;
    this.makulerad = makulerad;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getÄrendeByIdentity().get(ärendeIdentity).setMakulerad(makulerad);
  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public Long getMakulerad() {
    return makulerad;
  }

  public void setMakulerad(Long makulerad) {
    this.makulerad = makulerad;
  }
}
