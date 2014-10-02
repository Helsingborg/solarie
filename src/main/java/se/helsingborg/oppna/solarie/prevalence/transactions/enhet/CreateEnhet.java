package se.helsingborg.oppna.solarie.prevalence.transactions.enhet;

import org.prevayler.TransactionWithQuery;
import se.helsingborg.oppna.solarie.Solarie;
import se.helsingborg.oppna.solarie.prevalence.domain.Anvandare;
import se.helsingborg.oppna.solarie.prevalence.domain.Enhet;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;
import se.helsingborg.oppna.solarie.prevalence.transactions.IdentityFactory;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 05:13
 */
public class CreateEnhet implements TransactionWithQuery<Root, Enhet> {

  private static final long serialVersionUID = 1l;

  private Long identity;
  private String kod;

  public CreateEnhet() {
  }

  public CreateEnhet(String kod) throws Exception {
    this.identity = Solarie.getInstance().getPrevayler().execute(new IdentityFactory());
    this.kod = kod;
  }

  public CreateEnhet(Long identity, String kod) {
    this.identity = identity;
    this.kod = kod;
  }

  @Override
  public Enhet executeAndQuery(Root root, Date executionTime) throws Exception {
    Enhet enhet = new Enhet();

    if (identity == null) {
      throw new IllegalArgumentException("Identity is not set!");
    }
    if (root.getIdentifiables().containsKey(identity)) {
      throw new IllegalArgumentException("It already exists an identifiable with this identity! " + identity);
    }
    if (kod == null) {
      throw new IllegalArgumentException("Kod is not set!");
    }
    if (root.getAnvändareBySignatur().containsKey(kod)) {
      throw new IllegalArgumentException("It already exists an enhet with this kod! " + kod);
    }

    enhet.setKod(kod);
    enhet.setIdentity(identity);

    root.getIdentifiables().put(identity, enhet);
    root.getEnhetByIdentity().put(identity, enhet);
    root.getEnhetByKod().put(kod, enhet);
    return enhet;

  }

  public Long getIdentity() {
    return identity;
  }

  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  public String getKod() {
    return kod;
  }

  public void setKod(String kod) {
    this.kod = kod;
  }
}
