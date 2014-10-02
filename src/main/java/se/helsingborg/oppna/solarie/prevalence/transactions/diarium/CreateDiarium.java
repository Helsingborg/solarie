package se.helsingborg.oppna.solarie.prevalence.transactions.diarium;

import org.prevayler.TransactionWithQuery;
import se.helsingborg.oppna.solarie.Solarie;
import se.helsingborg.oppna.solarie.prevalence.domain.Diarium;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;
import se.helsingborg.oppna.solarie.prevalence.transactions.IdentityFactory;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 22:55
 */
public class CreateDiarium implements TransactionWithQuery<Root, Diarium> {

  private static final long serialVersionUID = 1l;

  private Long identity;

  private String namn;
  private String jdbcURL;

  public CreateDiarium() {
  }

  public CreateDiarium(String namn, String jdbcURL) throws Exception {
    this.identity = Solarie.getInstance().getPrevayler().execute(new IdentityFactory());
    this.namn = namn;
    this.jdbcURL = jdbcURL;
  }


  public CreateDiarium(Long identity, String namn, String jdbcURL) {
    this.identity = identity;
    this.namn = namn;
    this.jdbcURL = jdbcURL;
  }

  @Override
  public Diarium executeAndQuery(Root root, Date executionTime) throws Exception {

    if (identity == null) {
      throw new IllegalArgumentException("Identity is not set!");
    }
    if (root.getIdentifiables().containsKey(identity)) {
      throw new IllegalArgumentException("It already exists an identifiable with this identity! " + identity);
    }

    Diarium diarium = new Diarium();
    diarium.setIdentity(identity);
    diarium.setNamn(namn);
    diarium.setJdbcURL(jdbcURL);

    root.getDiariumByIdentity().put(identity, diarium);
    root.getIdentifiables().put(identity,diarium);

    return diarium;
  }

  public Long getIdentity() {
    return identity;
  }

  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  public String getNamn() {
    return namn;
  }

  public void setNamn(String namn) {
    this.namn = namn;
  }

  public String getJdbcURL() {
    return jdbcURL;
  }

  public void setJdbcURL(String jdbcURL) {
    this.jdbcURL = jdbcURL;
  }
}
