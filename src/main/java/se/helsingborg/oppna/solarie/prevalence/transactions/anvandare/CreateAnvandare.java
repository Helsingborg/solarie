package se.helsingborg.oppna.solarie.prevalence.transactions.anvandare;

import org.prevayler.TransactionWithQuery;
import se.helsingborg.oppna.solarie.Solarie;
import se.helsingborg.oppna.solarie.prevalence.domain.Anvandare;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;
import se.helsingborg.oppna.solarie.prevalence.transactions.IdentityFactory;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 04:40
 */
public class CreateAnvandare implements TransactionWithQuery<Root, Anvandare> {

  private static final long serialVersionUID = 1l;

  private Long identity;
  private String signatur;

  public CreateAnvandare() {
  }

  public CreateAnvandare(String signatur) throws Exception {
    this.identity = Solarie.getInstance().getPrevayler().execute(new IdentityFactory());
    this.signatur = signatur;
  }

  public CreateAnvandare(Long identity, String signatur) {
    this.identity = identity;
    this.signatur = signatur;
  }

  @Override
  public Anvandare executeAndQuery(Root root, Date executionTime) throws Exception {
    Anvandare användare = new Anvandare();

    if (identity == null) {
      throw new IllegalArgumentException("Identity is not set!");
    }
    if (root.getIdentifiables().containsKey(identity)) {
      throw new IllegalArgumentException("It already exists an identifiable with this identity! " + identity);
    }
    if (signatur == null) {
      throw new IllegalArgumentException("Signatur is not set!");
    }
    if (root.getAnvändareBySignatur().containsKey(signatur)) {
      throw new IllegalArgumentException("It already exists an användare with this signatur! " + signatur);
    }

    användare.setSignatur(signatur);
    användare.setIdentity(identity);

    root.getIdentifiables().put(identity, användare);
    root.getAnvändareByIdentity().put(identity, användare);
    root.getAnvändareBySignatur().put(signatur, användare);
    return användare;

  }

  public Long getIdentity() {
    return identity;
  }

  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  public String getSignatur() {
    return signatur;
  }

  public void setSignatur(String signatur) {
    this.signatur = signatur;
  }
}
