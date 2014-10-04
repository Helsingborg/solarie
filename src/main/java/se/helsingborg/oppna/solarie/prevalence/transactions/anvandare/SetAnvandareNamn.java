package se.helsingborg.oppna.solarie.prevalence.transactions.anvandare;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.domain.Anvandare;
import se.helsingborg.oppna.solarie.domain.Enhet;
import se.helsingborg.oppna.solarie.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetAnvandareNamn implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long användareIdentity;
  private String namn;

  public SetAnvandareNamn() {
  }

  public SetAnvandareNamn(Anvandare användare, String namn) {
    this.användareIdentity = användare.getIdentity();
    this.namn = namn;
  }

  public SetAnvandareNamn(Long användareIdentity, String namn) {
    this.användareIdentity = användareIdentity;
    this.namn = namn;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getAnvändareByIdentity().get(användareIdentity).setNamn(namn);
  }

  public Long getAnvändareIdentity() {
    return användareIdentity;
  }

  public void setAnvändareIdentity(Long användareIdentity) {
    this.användareIdentity = användareIdentity;
  }

  public String getNamn() {
    return namn;
  }

  public void setNamn(String namn) {
    this.namn = namn;
  }
}
