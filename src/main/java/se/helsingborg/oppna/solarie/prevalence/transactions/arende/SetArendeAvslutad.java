package se.helsingborg.oppna.solarie.prevalence.transactions.arende;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.domain.Arende;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetArendeAvslutad implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long ärendeIdentity;
  private Long avslutad;

  public SetArendeAvslutad() {
  }

  public SetArendeAvslutad(Arende ärende, Long avslutad) {
    this.ärendeIdentity = ärende.getIdentity();
    this.avslutad = avslutad;
  }

  public SetArendeAvslutad(Long ärendeIdentity, Long avslutad) {
    this.ärendeIdentity = ärendeIdentity;
    this.avslutad = avslutad;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getÄrendeByIdentity().get(ärendeIdentity).setAvslutad(avslutad);
  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public Long getAvslutad() {
    return avslutad;
  }

  public void setAvslutad(Long avslutad) {
    this.avslutad = avslutad;
  }
}
