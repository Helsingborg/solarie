package se.helsingborg.oppna.solarie.prevalence.transactions.enhet;

import org.prevayler.TransactionWithQuery;
import se.helsingborg.oppna.solarie.Solarie;
import se.helsingborg.oppna.solarie.domain.Diarium;
import se.helsingborg.oppna.solarie.domain.Enhet;
import se.helsingborg.oppna.solarie.domain.Root;
import se.helsingborg.oppna.solarie.prevalence.transactions.IdentityFactory;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 05:13
 */
public class CreateEnhet implements TransactionWithQuery<Root, Enhet> {

  private static final long serialVersionUID = 1l;

  private Long diariumIdentity;
  private Long identity;
  private String kod;

  public CreateEnhet() {
  }

  public CreateEnhet(Diarium diarium, String kod) throws Exception {
    this.diariumIdentity = diarium.getIdentity();
    this.identity = Solarie.getInstance().getPrevayler().execute(new IdentityFactory());
    this.kod = kod;
  }

  public CreateEnhet(Long diariumIdentity, Long identity, String kod) {
    this.diariumIdentity = diariumIdentity;
    this.identity = identity;
    this.kod = kod;
  }

  @Override
  public Enhet executeAndQuery(Root root, Date executionTime) throws Exception {

    if (diariumIdentity == null) {
      throw new IllegalArgumentException("Diarium identity is not set!");
    }

    Diarium diarium = root.getDiariumByIdentity().get(diariumIdentity);

    if (diarium == null) {
      throw new IllegalArgumentException("No diarium with that identity! " + diariumIdentity);
    }

    if (identity == null) {
      throw new IllegalArgumentException("Identity is not set!");
    }
    if (root.getIdentifiables().containsKey(identity)) {
      throw new IllegalArgumentException("It already exists an identifiable with this identity! " + identity);
    }
    if (kod == null) {
      throw new IllegalArgumentException("Kod is not set!");
    }
    if (diarium.getAnvändareBySignatur().containsKey(kod)) {
      throw new IllegalArgumentException("It already exists an enhet with this kod! " + kod);
    }

    Enhet enhet = new Enhet();

    enhet.setKod(kod);
    enhet.setIdentity(identity);

    root.getIdentifiables().put(identity, enhet);
    root.getEnhetByIdentity().put(identity, enhet);
    diarium.getEnhetByKod().put(kod, enhet);
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
