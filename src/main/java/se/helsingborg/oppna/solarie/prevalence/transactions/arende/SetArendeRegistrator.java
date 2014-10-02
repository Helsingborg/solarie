package se.helsingborg.oppna.solarie.prevalence.transactions.arende;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.prevalence.domain.Anvandare;
import se.helsingborg.oppna.solarie.prevalence.domain.Arende;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 04:50
 */
public class SetArendeRegistrator implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long ärendeIdentity;
  private Long registratorIdentity;

  public SetArendeRegistrator() {
  }

  public SetArendeRegistrator(Arende ärende, Anvandare registrator) {
    this.ärendeIdentity = ärende.getIdentity();
    this.registratorIdentity = registrator == null ? null : registrator.getIdentity();
  }

  public SetArendeRegistrator(Long ärendeIdentity, Long registratorIdentity) {
    this.ärendeIdentity = ärendeIdentity;
    this.registratorIdentity = registratorIdentity;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {

    if (ärendeIdentity == null) {
      throw new IllegalArgumentException("Ärende has not been set!");
    }
    Arende ärende = root.getÄrendeByIdentity().get(ärendeIdentity);
    if (ärende == null) {
      throw new IllegalArgumentException("No ärende with that identity! " + ärende);
    }

    Anvandare registrator;

    if (registratorIdentity == null) {
      registrator = null;
    } else {
      registrator = root.getAnvändareByIdentity().get(registratorIdentity);
      if (registrator == null) {
        throw new IllegalArgumentException("No användare with that identity! " + registratorIdentity);
      }
    }

    if (ärende.getRegistrator() != null) {
      ärende.getRegistrator().getRegistreradeÄrenden().remove(ärende);
    }
    ärende.setRegistrator(registrator);
    if (registrator != null) {
      registrator.getRegistreradeÄrenden().add(ärende);
    }

  }

  public Long getÄrendeIdentity() {
    return ärendeIdentity;
  }

  public void setÄrendeIdentity(Long ärendeIdentity) {
    this.ärendeIdentity = ärendeIdentity;
  }

  public Long getRegistratorIdentity() {
    return registratorIdentity;
  }

  public void setRegistratorIdentity(Long registratorIdentity) {
    this.registratorIdentity = registratorIdentity;
  }
}
