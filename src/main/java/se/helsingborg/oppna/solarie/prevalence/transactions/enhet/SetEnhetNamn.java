package se.helsingborg.oppna.solarie.prevalence.transactions.enhet;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.prevalence.domain.Atgard;
import se.helsingborg.oppna.solarie.prevalence.domain.Enhet;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetEnhetNamn implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long enhetIdentity;
  private String namn;

  public SetEnhetNamn() {
  }

  public SetEnhetNamn(Enhet enhet, String namn) {
    this.enhetIdentity = enhet.getIdentity();
    this.namn = namn;
  }

  public SetEnhetNamn(Long enhetIdentity, String namn) {
    this.enhetIdentity = enhetIdentity;
    this.namn = namn;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getEnhetByIdentity().get(enhetIdentity).setNamn(namn);
  }

  public Long getEnhetIdentity() {
    return enhetIdentity;
  }

  public void setEnhetIdentity(Long enhetIdentity) {
    this.enhetIdentity = enhetIdentity;
  }

  public String getNamn() {
    return namn;
  }

  public void setNamn(String namn) {
    this.namn = namn;
  }
}
