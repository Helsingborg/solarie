package se.helsingborg.oppna.solarie.prevalence.transactions.arende;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.prevalence.domain.Arende;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetArendeMening implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long ärendeIdentity;
  private String mening;

  public SetArendeMening() {
  }

  public SetArendeMening(Arende ärende, String mening) {
    this.ärendeIdentity = ärende.getIdentity();
    this.mening = mening;
  }

  public SetArendeMening(Long ärendeIdentity, String mening) {
    this.ärendeIdentity = ärendeIdentity;
    this.mening = mening;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getÄrendeByIdentity().get(ärendeIdentity).setMening(mening);
  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public String getMening() {
    return mening;
  }

  public void setMening(String mening) {
    this.mening = mening;
  }
}
