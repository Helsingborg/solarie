package se.helsingborg.oppna.solarie.prevalence.queries;

import org.prevayler.Query;
import se.helsingborg.oppna.solarie.prevalence.domain.Enhet;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 04:36
 */
public class GetEnhetByKod implements Query<Root, Enhet> {

  private String kod;

  public GetEnhetByKod(String kod) {
    this.kod = kod;
  }

  @Override
  public Enhet query(Root root, Date executionTime) throws Exception {
    return root.getEnhetByKod().get(kod);
  }
}
