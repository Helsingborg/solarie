package se.helsingborg.oppna.solarie.prevalence.transactions.arende;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.domain.Arende;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetArendeBeslutad implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long ärendeIdentity;
  private Long beslutad;

  public SetArendeBeslutad() {
  }

  public SetArendeBeslutad(Arende ärende, Long beslutad) {
    this.ärendeIdentity = ärende.getIdentity();
    this.beslutad = beslutad;
  }

  public SetArendeBeslutad(Long ärendeIdentity, Long beslutad) {
    this.ärendeIdentity = ärendeIdentity;
    this.beslutad = beslutad;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getÄrendeByIdentity().get(ärendeIdentity).setBeslutad(beslutad);
  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public Long getBeslutad() {
    return beslutad;
  }

  public void setBeslutad(Long beslutad) {
    this.beslutad = beslutad;
  }
}
