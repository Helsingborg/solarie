package se.helsingborg.oppna.solarie.prevalence.queries;

import org.prevayler.Query;
import se.helsingborg.oppna.solarie.prevalence.domain.Diarium;
import se.helsingborg.oppna.solarie.prevalence.domain.Enhet;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 04:36
 */
public class GetEnhetByKod implements Query<Root, Enhet> {

  private Long diariumIdentity;
  private String kod;

  public GetEnhetByKod(Diarium diarium, String kod) {
    this.diariumIdentity = diarium.getIdentity();
    this.kod = kod;
  }


  public GetEnhetByKod(Long diariumIdentity, String kod) {
    this.diariumIdentity = diariumIdentity;
    this.kod = kod;
  }

  @Override
  public Enhet query(Root root, Date executionTime) throws Exception {
    return root.getDiariumByIdentity().get(diariumIdentity).getEnhetByKod().get(kod);
  }
}
