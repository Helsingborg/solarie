package se.helsingborg.oppna.solarie.prevalence.transactions.diarium;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.prevalence.domain.Diarium;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-03 00:08
 */
public class SetDiariumSenasteSynkronisering implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long diariumIdentity;
  private Long senasteSynkronisering;

  public SetDiariumSenasteSynkronisering() {
  }

  public SetDiariumSenasteSynkronisering(Diarium diarium, Long senasteSynkronisering) {
    this.diariumIdentity = diarium.getIdentity();
    this.senasteSynkronisering = senasteSynkronisering;
  }

  public SetDiariumSenasteSynkronisering(Long diariumIdentity, Long senasteSynkronisering) {
    this.diariumIdentity = diariumIdentity;
    this.senasteSynkronisering = senasteSynkronisering;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getDiariumByIdentity().get(diariumIdentity).setSenasteSynkronisering(senasteSynkronisering);
  }

  public Long getDiariumIdentity() {
    return diariumIdentity;
  }

  public void setDiariumIdentity(Long diariumIdentity) {
    this.diariumIdentity = diariumIdentity;
  }

  public Long getSenasteSynkronisering() {
    return senasteSynkronisering;
  }

  public void setSenasteSynkronisering(Long senasteSynkronisering) {
    this.senasteSynkronisering = senasteSynkronisering;
  }
}
