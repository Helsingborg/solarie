package se.helsingborg.oppna.solarie.prevalence.transactions.arende;

import org.prevayler.TransactionWithQuery;
import se.helsingborg.oppna.solarie.Solarie;
import se.helsingborg.oppna.solarie.domain.Arende;
import se.helsingborg.oppna.solarie.domain.Diarienummer;
import se.helsingborg.oppna.solarie.domain.Diarium;
import se.helsingborg.oppna.solarie.domain.Root;
import se.helsingborg.oppna.solarie.prevalence.transactions.IdentityFactory;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 01:54
 */
public class CreateArende implements TransactionWithQuery<Root, Arende> {

  private static final long serialVersionUID = 1l;

  private Long diariumIdentity;
  private Long identity;
  private Diarienummer diarienummer;

  public CreateArende() {
  }

  public CreateArende(Diarium diarium, Diarienummer diarienummer) throws Exception {
    this.diariumIdentity = diarium.getIdentity();
    this.identity = Solarie.getInstance().getPrevayler().execute(new IdentityFactory());
    this.diarienummer = diarienummer;
  }

  public CreateArende(Long diariumIdentity, Long identity, Diarienummer diarienummer) {
    this.diariumIdentity = diariumIdentity;
    this.identity = identity;
    this.diarienummer = diarienummer;
  }

  @Override
  public Arende executeAndQuery(Root root, Date executionTime) throws Exception {

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
    if (diarienummer == null) {
      throw new IllegalArgumentException("Diarienummer is not set!");
    }
    if (diarium.getÄrendeByDiarienummer().containsKey(diarienummer)) {
      throw new IllegalArgumentException("It already exists an ärende with this diarienummer! " + diarienummer);
    }


    Arende ärende = new Arende();

    ärende.setDiarienummer(diarienummer);
    ärende.setIdentity(identity);
    ärende.setDiarium(diarium);

    root.getIdentifiables().put(identity, ärende);
    root.getÄrendeByIdentity().put(identity, ärende);
    diarium.getÄrendeByDiarienummer().put(diarienummer, ärende);
    return ärende;
  }

  public Long getIdentity() {
    return identity;
  }

  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  public Diarienummer getDiarienummer() {
    return diarienummer;
  }

  public void setDiarienummer(Diarienummer diarienummer) {
    this.diarienummer = diarienummer;
  }
}
