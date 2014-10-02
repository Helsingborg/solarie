package se.helsingborg.oppna.solarie.prevalence.transactions.arende;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.prevalence.domain.Arende;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetArendePlaneratBeslut implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long ärendeIdentity;
  private Long planeratBeslut;

  public SetArendePlaneratBeslut() {
  }

  public SetArendePlaneratBeslut(Arende ärende, Long planeratBeslut) {
    this.ärendeIdentity = ärende.getIdentity();
    this.planeratBeslut = planeratBeslut;
  }

  public SetArendePlaneratBeslut(Long ärendeIdentity, Long planeratBeslut) {
    this.ärendeIdentity = ärendeIdentity;
    this.planeratBeslut = planeratBeslut;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getÄrendeByIdentity().get(ärendeIdentity).setPlaneratBeslut(planeratBeslut);
  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public Long getPlaneratBeslut() {
    return planeratBeslut;
  }

  public void setPlaneratBeslut(Long planeratBeslut) {
    this.planeratBeslut = planeratBeslut;
  }
}
