package se.helsingborg.oppna.solarie.prevalence.transactions.arende;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.prevalence.domain.Arende;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetArendeAnkommen implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long ärendeIdentity;
  private Long ankommen;

  public SetArendeAnkommen() {
  }

  public SetArendeAnkommen(Arende ärende, Long ankommen) {
    this.ärendeIdentity = ärende.getIdentity();
    this.ankommen = ankommen;
  }

  public SetArendeAnkommen(Long ärendeIdentity, Long ankommen) {
    this.ärendeIdentity = ärendeIdentity;
    this.ankommen = ankommen;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getÄrendeByIdentity().get(ärendeIdentity).setAnkommen(ankommen);
  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public Long getAnkommen() {
    return ankommen;
  }

  public void setAnkommen(Long ankommen) {
    this.ankommen = ankommen;
  }
}
