package se.helsingborg.oppna.solarie.prevalence.transactions.atgard;

import org.prevayler.TransactionWithQuery;
import se.helsingborg.oppna.solarie.Solarie;
import se.helsingborg.oppna.solarie.domain.Arende;
import se.helsingborg.oppna.solarie.domain.Atgard;
import se.helsingborg.oppna.solarie.domain.Root;
import se.helsingborg.oppna.solarie.prevalence.transactions.IdentityFactory;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 05:13
 */
public class CreateAtgard implements TransactionWithQuery<Root, Atgard> {

  private static final long serialVersionUID = 1l;

  private Long identity;
  private Long ärendeIdentity;
  private Short åtgärdsnummer;

  public CreateAtgard() {
  }

  public CreateAtgard(Arende ärende, Short åtgärdsnummer) throws Exception {
    this(ärende.getIdentity(), åtgärdsnummer);
  }

  public CreateAtgard(Long identity, Arende ärende, Short åtgärdsnummer) {
    this(identity, ärende.getIdentity(), åtgärdsnummer);
  }

  public CreateAtgard(Long ärendeIdentity, Short åtgärdsnummer) throws Exception {
    this(Solarie.getInstance().getPrevayler().execute(new IdentityFactory()), ärendeIdentity, åtgärdsnummer);
  }

  public CreateAtgard(Long identity, Long ärendeIdentity, Short åtgärdsnummer) {
    this.identity = identity;
    this.åtgärdsnummer = åtgärdsnummer;
    this.ärendeIdentity = ärendeIdentity;
  }

  @Override
  public Atgard executeAndQuery(Root root, Date executionTime) throws Exception {

    if (identity == null) {
      throw new IllegalArgumentException("Identity is not set!");
    }
    if (root.getIdentifiables().containsKey(identity)) {
      throw new IllegalArgumentException("It already exists an identifiable with this identity! " + identity);
    }

    if (ärendeIdentity == null) {
      throw new IllegalArgumentException("Ärendeidentity is not set!");
    }

    Arende ärende = root.getÄrendeByIdentity().get(ärendeIdentity);
    if (ärende == null) {
      throw new IllegalArgumentException("No ärende with that identity! " + ärendeIdentity);
    }

    if (åtgärdsnummer == null) {
      throw new IllegalArgumentException("Åtgärdsnummer is not set!");
    }
    if (ärende.getÅtgärderByNummer().containsKey(åtgärdsnummer)) {
      throw new IllegalArgumentException("It ärende with identity "+ärende.getIdentity()+" already contains an åtgärd with this nummer! " + åtgärdsnummer);
    }

    Atgard åtgärd = new Atgard();
    åtgärd.setNummer(åtgärdsnummer);
    åtgärd.setIdentity(identity);
    åtgärd.setÄrende(ärende);
    ärende.getÅtgärderByNummer().put(åtgärdsnummer, åtgärd);

    root.getIdentifiables().put(identity, åtgärd);
    root.getÅtgärdByIdentity().put(identity, åtgärd);

    return åtgärd;

  }

  public Long getIdentity() {
    return identity;
  }

  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public Short getÅtgärdsnummer() {
    return åtgärdsnummer;
  }

  public void setÅtgärdsnummer(Short åtgärdsnummer) {
    this.åtgärdsnummer = åtgärdsnummer;
  }
}
